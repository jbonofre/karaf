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
package org.apache.karaf.devx.extensions.junit;

import org.apache.karaf.main.ConfigProperties;
import org.apache.karaf.main.Main;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

public class KarafExtension implements BeforeAllCallback, AfterAllCallback {

    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(KarafExtension.class.getName());

    private static File karafHome;
    private static File karafBase;
    private static File karafData;
    private static File karafEtc;
    private static File karafLog;
    private static File karafInstances;

    private static ClassLoader tccl = Thread.currentThread().getContextClassLoader();

    @Override
    public void beforeAll(final ExtensionContext context) throws Exception {
        Karaf karaf = new Karaf(Stream.of("").toArray(String[]::new));
        store(context).put(Karaf.class, karaf);
        context.getElement()
                .map(e -> e.getAnnotation(KarafTest.class))
                .map(this::createKarafBase);
        configureKaraf(karaf);
        karaf.launch();
        karaf.awaitShutdown();
    }

    @Override
    public void afterAll(final ExtensionContext context) {
        ofNullable(store(context).get(Main.class, Main.class))
                .ifPresent(main -> {
                    try {
                        main.destroy();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private boolean createKarafBase(final KarafTest karaftest) {
        final StringBuilder karafHomeBuilder = new StringBuilder("");
         of(karaftest.karafBase()).filter(it -> !it.isEmpty()).ifPresent(karafHomeBuilder::append);
         if (karafHomeBuilder.toString().isEmpty()) {
             karafHomeBuilder.append(File.separator.concat("tmp".concat(File.separator).concat("karaf_" + System.nanoTime())));

             //create home
             File dir = new File(karafHomeBuilder.toString());
             if (!dir.exists()) {
                 dir.mkdirs();
             }
             Stream.of("etc","system").forEach(
                     folder -> copyFullDirectory(folder, karafHomeBuilder.toString().concat(File.separator).concat(folder)));
         }
         karafHome = new File(karafHomeBuilder.toString());
         karafBase = new File(karafHomeBuilder.toString());
         karafInstances = new File(karafHomeBuilder.toString().concat(File.separator).concat("instances"));
         karafData = new File(karafHomeBuilder.toString().concat(File.separator).concat("data"));
         karafEtc = new File(karafHomeBuilder.toString().concat(File.separator).concat("etc"));
         karafLog = new File(karafHomeBuilder.toString().concat(File.separator).concat("data").concat(File.separator).concat("log"));
         return true;
    }

    private void configureKaraf(final Karaf karaf) throws Exception {
        ConfigProperties config = new ConfigProperties(karafHome, karafBase, karafData, karafEtc, karafLog, karafInstances);
        karaf.setConfig(config);
    }

    private ExtensionContext.Store store(final ExtensionContext context) {
        return context.getStore(NAMESPACE);
    }

    private void copyFullDirectory(String pathSourceDirectory, String pathTargetDirectory) {
        File sourceDirectory = new File(tccl.getResource(pathSourceDirectory).getPath());
        File targetDirectory = new File(pathTargetDirectory);
        if (!targetDirectory.exists()) {
            targetDirectory.mkdirs();
        }
        try {
            Files.walk(sourceDirectory.toPath(), new FileVisitOption[] {FileVisitOption.FOLLOW_LINKS})
                    .filter(Files::isDirectory)
                    .map(Path::toFile)
                    .forEach(source -> {
                        File target = new File(targetDirectory.getPath()
                                .concat(File.separator)
                                .concat(source.getAbsolutePath().replaceFirst(sourceDirectory.getAbsolutePath(), "")));
                        ofNullable(target).ifPresent(File::mkdirs);
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Files.walk(sourceDirectory.toPath(), new FileVisitOption[] {FileVisitOption.FOLLOW_LINKS})
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .forEach(source -> {
                        File target = new File(targetDirectory.getPath()
                                .concat(File.separator)
                                .concat(source.getAbsolutePath().replaceFirst(sourceDirectory.getAbsolutePath(), "")));
                        try {
                            //InputStream fileStream = tccl.getResourceAsStream(sourceDirectory.getName().concat(File.separator).concat(source.getName()));
                            Files.copy(source.toPath(), target.toPath(), REPLACE_EXISTING);
                        } catch (IOException e) {
                            System.err.println(e.getMessage());
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
