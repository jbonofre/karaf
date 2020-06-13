/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.karaf.spring.boot.internal;

import org.apache.karaf.spring.boot.SpringBootService;
import org.apache.karaf.util.StreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import static java.util.Optional.ofNullable;

public class SpringBootServiceImpl implements SpringBootService {

    private final static Logger LOGGER = LoggerFactory.getLogger(SpringBootServiceImpl.class);

    private File metadata;
    private File storage;
    private File stacksBase;
    private final ConcurrentMap<String, KarafLauncherLoader> loaders = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, URLClassLoader> stacks = new ConcurrentHashMap<>();

    public SpringBootServiceImpl() {
        metadata = new File(new File(System.getProperty("karaf.data")), "spring-boot/metadata");
        storage = new File(new File(System.getProperty("karaf.data")), "spring-boot/applications");
        stacksBase = new File(new File(System.getProperty("karaf.data")), "spring-boot/stacks");
        metadata.mkdirs();
        storage.mkdirs();
        stacksBase.mkdirs();

        // todo: configadmin?
        System.setProperty("org.springframework.boot.logging.LoggingSystem", "org.apache.karaf.spring.boot.embed.pax.logging.PaxLoggingSystem");
    }

    @Override
    public String install(final URI uri, final String stack) throws Exception {
        return install(null, uri, stack);
    }

    @Override
    public String install(String name, final URI uri, final String stack) throws Exception {
        LOGGER.info("Installing Spring Boot application located {}", uri);
        final Path source = Paths.get(uri);
        String fileName = source.getFileName().toString();

        LOGGER.debug("Copying {} to storage", fileName);
        File springBootJar = new File(storage, fileName);

        if (!Files.exists(source)) {
            throw new IllegalArgumentException(source + " does not exist");
        }
        // todo: digest?
        // todo: handle exploded dirs
        StreamUtils.copy(uri.toURL().openStream(), new FileOutputStream(springBootJar));
        Attributes attributes = getAttributes(springBootJar); // validates it is a spring boot app
        if (name == null) {
            String startClass = attributes.getValue("Start-Class");
            if (startClass != null) {
                name = startClass.substring(startClass.lastIndexOf(".") + 1);
            } else {
                name = fileName;
            }
        }
        final Properties meta = new Properties();
        try (final Writer writer = Files.newBufferedWriter(metadata.toPath().resolve(name + ".properties"))) {
            meta.setProperty("stack", stack == null || stack.trim().isEmpty() ? "<none>" : stack);
            meta.setProperty("jar", fileName);
            meta.setProperty("name", name);
            meta.store(writer , null);
        }
        return name;
    }

    @Override
    public void start(final String name, final String[] args) throws Exception {
        LOGGER.info("Starting Spring Boot application {} with args {}", name, args);
        final Path meta = metadata.toPath().resolve(name + ".properties");
        if (!Files.exists(meta)) {
            throw new IllegalArgumentException("No metadata for " + name);
        }
        final Properties config = new Properties();
        config.load(Files.newBufferedReader(meta));
        final File springBootJar = new File(storage, config.get("jar").toString());
        if (!springBootJar.exists()) {
            throw new IllegalArgumentException(name + " is not fully installed");
        }
        final Attributes attributes = getAttributes(springBootJar);
        final String main = attributes.getValue(Attributes.Name.MAIN_CLASS);
        LOGGER.debug("Got Spring Boot Main-Class {}", main);
        if (main == null) {
            throw new IllegalArgumentException("No main in " + springBootJar);
        }
        final ClassLoader bundleLoader = getClass().getClassLoader();
        final String stack = config.getProperty("stack", "");
        final KarafLauncherLoader loader = new KarafLauncherLoader(
                springBootJar,
                "<none>".equals(stack) || stack.isEmpty() ? createLauncherRootParent(bundleLoader) : createLauncherRootParent(getStackLoader(stack, bundleLoader)));
        final Thread thread = Thread.currentThread();
        final ClassLoader old = thread.getContextClassLoader();
        thread.setContextClassLoader(loader);
        loaders.put(name, loader);
        try {
            loader.launch(main, args);
        } catch (final Exception e) {
            loaders.remove(name);
            try {
                loader.close();
            } catch (final IOException ioe) {
                // no-op
            }
            throw e;
        } finally {
            thread.setContextClassLoader(old);
        }
    }

    @Override
    public void stop(final String name) {
        ofNullable(loaders.remove(name)).ifPresent(KarafLauncherLoader::destroy);
    }

    @Override
    public void stopAll() {
        loaders.keySet().forEach(this::stop);
        loaders.clear();
        stacks.values().forEach(it -> {
            try {
                it.close();
            } catch (final IOException e) {
                // no-op
            }
        });
        stacks.clear();
    }

    @Override
    public Map<String, Boolean> list() throws Exception {
        Map<String, Boolean> state = new HashMap<>();
        for (File file : metadata.listFiles()) {
            Properties properties = new Properties();
            properties.load(Files.newBufferedReader(file.toPath()));
            String name = properties.getProperty("name");
            state.put(name, loaders.get(name) != null);
        }
        return state;
    }

    private URLClassLoader getStackLoader(final String stack, final ClassLoader bundleLoader) {
        return stacks.computeIfAbsent(stack, stackName -> {
            try {
                return new URLClassLoader(
                        Files.list(stacksBase.toPath().resolve(stackName))
                                .filter(it -> {
                                    final String jarName = it.getFileName().toString();
                                    return jarName.endsWith(".jar") || jarName.endsWith(".zip");
                                })
                                .map(it -> {
                                    try {
                                        return it.toUri().toURL();
                                    } catch (final MalformedURLException e) {
                                        throw new IllegalArgumentException(e);
                                    }
                                })
                                .toArray(URL[]::new),
                        bundleLoader);
            } catch (final IOException e) {
                throw new IllegalStateException(e);
            }
        });
    }

    private ClassLoader createLauncherRootParent(final ClassLoader parent) {
        return new ClassLoader(parent) {
            @Override
            protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
                if (name != null && name.startsWith("org.springframework.")) {
                    throw new ClassNotFoundException(name);
                }
                try {
                    return super.loadClass(name, resolve);
                } catch (final ClassNotFoundException cnfe) {
                    return getSystemClassLoader().loadClass(name);
                }
            }
        };
    }

    private Attributes getAttributes(File source) throws IOException {
        try (JarFile jar = new JarFile(source)) {
            Manifest manifest = jar.getManifest();
            final Attributes attributes = manifest.getMainAttributes();
            if (attributes.getValue("Spring-Boot-Version") == null) {
                LOGGER.warn("Spring-Boot-Version not found in MANIFEST");
                source.delete();
                throw new IllegalArgumentException("Invalid Spring Boot application artifact");
            }
            return attributes;
        } catch (Exception e) {
            source.delete();
            throw e;
        }
    }
}