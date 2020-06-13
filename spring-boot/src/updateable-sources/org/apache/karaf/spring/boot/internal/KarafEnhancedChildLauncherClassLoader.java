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

import org.objectweb.asm.util.ASMifier;
import org.springframework.boot.loader.LaunchedURLClassLoader;
import org.springframework.boot.loader.archive.Archive;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.stream.Stream;

import static java.util.Collections.enumeration;
import static java.util.Collections.list;
import static java.util.stream.Collectors.toList;

public class KarafEnhancedChildLauncherClassLoader extends LaunchedURLClassLoader {
    static {
        ClassLoader.registerAsParallelCapable();
    }

    private final ClassGenerator integrations;

    public KarafEnhancedChildLauncherClassLoader(final boolean exploded, final Archive rootArchive,
                                                 final URL[] urls, final ClassLoader parent) {
        super(exploded, rootArchive, urls, parent);
        this.integrations = new ClassGenerator();
    }

    @Override
    protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
        if (name == null) {
            throw new ClassNotFoundException("<null>");
        }
        synchronized (getClassLoadingLock(name)) {
            final Class<?> existing = findLoadedClass(name);
            if (existing == null && integrations.handlesInChild(name)) {
                return loadIntegrationClass(name, resolve, integrations.findByteCode(name));
            }
        }
        return super.loadClass(name, resolve);
    }

    @Override
    public Enumeration<URL> getResources(final String name) throws IOException {
        if ("META-INF/spring.factories".equals(name)) {
            return enumeration(Stream.concat(
                    list(super.getResources(name)).stream(),
                    Stream.of(new URL("karaf-spring-boot", null, -1, name, new StaticURLStreamHandler(
                            "org.springframework.boot.SpringApplicationRunListener=org.apache.karaf.spring.boot.internal.SpringApplicationContextCapture"))))
                    .collect(toList()));
        }
        return super.getResources(name);
    }

    private Class<?> loadIntegrationClass(final String name, final boolean resolve, final byte[] bytes) {
        final Class<?> value = super.defineClass(name, bytes, 0, bytes.length);
        if (resolve) {
            resolveClass(value);
        }
        return value;
    }

    public static class Dumper {
        public static void main(String[] args) throws IOException {
            ASMifier.main(new String[]{
                    "target/test-classes/org/apache/karaf/spring/boot/internal/KarafEnhancedChildLauncherClassLoader.class"});
            System.out.println("-------------------");
            ASMifier.main(new String[]{
                    "target/test-classes/org/apache/karaf/spring/boot/internal/KarafEnhancedChildLauncherClassLoader$StaticURLStreamHandler.class"});
            System.out.println("-------------------");
            ASMifier.main(new String[]{
                    "target/test-classes/org/apache/karaf/spring/boot/internal/KarafEnhancedChildLauncherClassLoader$StaticURLStreamHandler$StaticURLConnection.class"});
        }
    }

    private static class StaticURLStreamHandler extends URLStreamHandler {
        private final String content;

        private StaticURLStreamHandler(final String content) {
            this.content = content;
        }

        @Override
        protected URLConnection openConnection(final URL u) {
            return new StaticURLConnection(u, content);
        }

        private static class StaticURLConnection extends URLConnection {
            private final String content;

            public StaticURLConnection(final URL u, final String content) {
                super(u);
                this.content = content;
            }

            @Override
            public void connect() {
                // no-op
            }

            @Override
            public InputStream getInputStream() {
                return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
            }
        }
    }
}