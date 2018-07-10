/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.karaf.examples.rest.client.http;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.ws.rs.HttpMethod;

/**
 * Simple bean controlling a thread to display the bookings on System.out.
 */
public class ListBean {

    private ListThread thread;
    
    public void start() {
        thread = new ListThread();
        thread.start();
    }

    public void stop() {
        thread.terminate();
    }

    private class ListThread extends Thread {
        
        private static final String GET_ALL_URL = "http://localhost:8181/cxf/example/booking/";
        
        private volatile boolean running = true;

        @Override
        public void run() {
            while (running) {
                try {
                    System.out.println("Call " + GET_ALL_URL);
                    URL url = new URL(GET_ALL_URL);

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod(HttpMethod.GET);
                    connection.connect();
                    
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader buffer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String line;
                        StringBuffer sb = new StringBuffer();
                        while ((line = buffer.readLine())!= null) {
                           sb.append(line);
                        }
                        System.out.println(sb.toString());
                    } else {
                        System.out.println("Error when sending GET method : HTTP_CODE = " + connection.getResponseCode());
                    }
                    
                    Thread.sleep(5000);
                } catch (Exception e) {
                    // nothing to do
                }
            }
        }

        public void terminate() {
            running = false;
        }

    }

}
