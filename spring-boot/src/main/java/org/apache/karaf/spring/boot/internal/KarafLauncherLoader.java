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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import static java.util.Objects.requireNonNull;

public class KarafLauncherLoader extends URLClassLoader {
    static {
        ClassLoader.registerAsParallelCapable();
    }

    private final ClassGenerator integrations;
    private final File base;
    private Object contextHolder;

    public KarafLauncherLoader(final File baseOrJar, final ClassLoader parent) throws MalformedURLException {
        super(new URL[]{baseOrJar.toURI().toURL()}, parent);
        this.integrations = new ClassGenerator();
        this.base = baseOrJar;
    }

    public synchronized void launch(final String main, final String... args) throws InvocationTargetException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException {
        final Method method = loadClass(main).getMethod("main", String[].class);
        if (!method.isAccessible()) {
            method.setAccessible(true);
        }
        method.invoke(null, new Object[]{args});
    }

    public synchronized void destroy() {
        try {
            if (contextHolder != null) {
                try {
                    final Field context = contextHolder.getClass().getDeclaredField("context");
                    context.setAccessible(true);
                    final Object contextInstance = context.get(contextHolder);
                    final Method stop = contextInstance.getClass().getMethod("stop");
                    if (!stop.isAccessible()) {
                        stop.setAccessible(true);
                    }
                    stop.invoke(contextInstance);
                } catch (final Exception e) {
                    throw new IllegalStateException(e);
                }
            }
        } finally {
            try {
                super.close();
            } catch (final IOException e) {
                // no-op
            }
        }
    }

    @Override
    protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
        if (name == null) {
            throw new ClassNotFoundException("<null>");
        }
        synchronized (getClassLoadingLock(name)) {
            final Class<?> existing = findLoadedClass(name);
            if (existing == null) {
                if (name.startsWith("org.apache.karaf.spring.boot.internal.")) {
                    final String sub = name.substring("org.apache.karaf.spring.boot.internal.".length());
                    switch (sub) {
                        case "KarafEnhancedChildLauncherClassLoader":
                            return loadIntegrationClass(name, resolve, integrations.karafLauncherClassLoader());
                        case "KarafEnhancedChildLauncherClassLoader$StaticURLStreamHandler":
                            return loadIntegrationClass(name, resolve, integrations.karafLauncherClassLoaderUrlHandler());
                        case "KarafEnhancedChildLauncherClassLoader$StaticURLStreamHandler$StaticURLConnection":
                            return loadIntegrationClass(name, resolve, integrations.karafLauncherClassLoaderUrlHandlerConnection());
                        default:
                            return super.loadClass(name, resolve);
                    }
                }
                if (name.startsWith("org.springframework.")) {
                    final String sub = name.substring("org.springframework.".length());
                    if (sub.startsWith("boot.loader.")) {
                        final String subName = sub.substring("boot.loader.".length());
                        switch (subName) {
                            case "Launcher": {
                                try {
                                    final byte[] original = readBytes(name.replace('.', '/') + ".class");
                                    return loadIntegrationClass(name, resolve, integrations.patchLauncher(original, base.getAbsolutePath()));
                                } catch (final IOException e) {
                                    throw new IllegalStateException(e);
                                }
                            }
                            default:
                                return super.loadClass(name, resolve);
                        }
                    }
                    throw new ClassNotFoundException(name);
                }
                return super.loadClass(name, resolve);
            }
            if (resolve) {
                resolveClass(existing);
            }
            return existing;
        }
    }

    @Override
    public URL getResource(final String name) {
        if ("org/springframework/boot/loader/Launcher.class".equals(name)) {
            return findResource(name);
        }
        return super.getResource(name);
    }

    private byte[] readBytes(final String name) throws IOException {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        final byte[] tmp = new byte[1024];
        int read;
        try (final InputStream stream = requireNonNull(getResourceAsStream(name), "Can't find '" + name + "'")) {
            while ((read = stream.read(tmp)) >= 0) {
                if (read > 0) {
                    buffer.write(tmp, 0, read);
                }
            }
        }
        return buffer.toByteArray();
    }

    private Class<?> loadIntegrationClass(final String name, final boolean resolve, final byte[] bytes) {
        final Class<?> value = super.defineClass(name, bytes, 0, bytes.length);
        if (resolve) {
            resolveClass(value);
        }
        return value;
    }

    public void setContextHolder(final Object instance) {
        this.contextHolder = instance;
    }
}