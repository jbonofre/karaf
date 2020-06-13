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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.impl.SimpleLogger;

import javax.swing.*;
import java.io.File;
import java.net.URI;
import java.nio.file.Paths;

public class SpringBootServiceImplTest {

    @Before
    public void cleanup() {
        System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG");
        System.setProperty("karaf.data", "target");
        File file = new File("target/spring-boot");
        file.delete();
    }

    @Test
    public void testInstallInvalidArtifact() throws Exception {
        SpringBootServiceImpl service = new SpringBootServiceImpl();
        try {
            service.install(new File("target/commons-lang-2.6.jar").toURI(), null);
        } catch (IllegalArgumentException e) {
            // good
            return;
        }
        Assert.fail("IllegalArgumentException expected");
    }

    @Test
    public void testInstallValidArtifact() throws Exception {
        SpringBootServiceImpl service = new SpringBootServiceImpl();
        service.install(new File("target/test-classes/rest-service-0.0.1-SNAPSHOT.jar").toURI(), null);

        Assert.assertEquals(1, service.list().keySet().size());
        Assert.assertFalse(service.list().get("RestServiceApplication"));

        Assert.assertTrue(new File("target/spring-boot/applications/rest-service-0.0.1-SNAPSHOT.jar").exists());
        Assert.assertTrue(new File("target/spring-boot/metadata/RestServiceApplication.properties").exists());
    }

    @Test
    public void testStart() throws Exception {
        SpringBootServiceImpl service = new SpringBootServiceImpl();
        service.install(new File("target/test-classes/rest-service-0.0.1-SNAPSHOT.jar").toURI(), null);
        service.start("RestServiceApplication", new String[]{});
        Assert.assertTrue(service.list().get("RestServiceApplication"));
    }

}
