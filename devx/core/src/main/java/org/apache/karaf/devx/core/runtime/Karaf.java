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

import org.apache.karaf.main.Main;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Karaf wrapper to start/connect/use Karaf runtime.
 */
public class Karaf {

    private Main karafMain;

    public Karaf(String[] args) {
        this.karafMain = new Main(args);
    }

    /**
     * Download Karaf distribution from the give location.
     *
     * @param sourceLocation the source location of the Karaf distribution.
     * @param targetLocation the target location where to install the Karaf distribution.
     * @throws Exception
     */
    static public void download(String sourceLocation, String targetLocation) throws Exception {
        URL url = new URL(sourceLocation);

    }

    public void configure() {

    }

    public void launch() throws Exception {
        karafMain.launch();
    }

}
