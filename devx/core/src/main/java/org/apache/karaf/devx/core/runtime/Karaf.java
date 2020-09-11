/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.karaf.devx.core.runtime;

import org.apache.karaf.main.ConfigProperties;
import org.apache.karaf.main.Main;

import java.io.File;
import java.nio.file.Files;


/**
 * Karaf wrapper to start/connect/use Karaf runtime.
 */
public class Karaf {

    private Main karafMain;

    private Karaf(String[] args) {
        karafMain = new Main(args);
    }

    public void launch() throws Exception {
        karafMain.launch();
    }

    public static class Builder {

        private String[] args = new String[]{};
        private String location;
        private String source;
        private String extracted;

        public Builder args(String[] args) {
            this.args = args;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Builder download(String source, String extracted) {
            this.source = source;
            this.extracted = extracted;
            return this;
        }

        public Karaf build() throws Exception {
            // default is embedded
            // check if download/extract is required
            if (source != null) {
                if (extracted == null) {
                    extracted = Files.createTempDirectory("karaf", null).toFile().getAbsolutePath();
                }

                location = extracted;
            }
            System.setProperty(ConfigProperties.PROP_KARAF_HOME, location);
            if (args == null) {
                args = new String[]{};
            }
            Karaf karaf = new Karaf(args);
            return karaf;
        }

        private void downloadAndExtract(String source, String extracted) throws Exception {

        }

    }

}
