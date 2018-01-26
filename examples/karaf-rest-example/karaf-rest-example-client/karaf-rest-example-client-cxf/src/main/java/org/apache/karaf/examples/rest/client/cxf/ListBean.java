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
package org.apache.karaf.examples.rest.client.cxf;

import java.util.List;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.karaf.examples.rest.api.Booking;

/**
 * Simple bean controlling a thread to display the bookings on System.out.
 */
public class ListBean {

    private ListThread thread;
    
    private WebClient client;
    
    public void setClient(WebClient client) {
        this.client = client;
    }

    public void start() {
        thread = new ListThread();
        thread.start();
    }

    public void stop() {
        thread.terminate();
    }

    private class ListThread extends Thread {
        
        private volatile boolean running = true;
        
        @Override
        public void run() {
            
            while (running) {
                try {
                    System.out.println("Call " + client.getBaseURI() + "/1");
                    Booking booking = client.replacePath("/1").accept(MediaType.APPLICATION_JSON)
                            .get(Booking.class);
                    
                    if (booking != null) {
                        System.out.println(booking.getId() + " | "
                                + booking.getCustomer() + " | "
                                + booking.getFlight() + "\n");
                    } else {
                        System.out.println("Response is empty");
                    }
                    
                    System.out.println("Call " + client.getBaseURI() + "/all");
                    List<Booking> response = client.replacePath("/all").accept(MediaType.APPLICATION_JSON)
                            .get(new GenericType<List<Booking>>() {});
                    
                    if (!response.isEmpty()) {
                        
                        StringBuilder builder = new StringBuilder();
                        for (Booking element : response) {
                            builder.append(element.getId()).append(" | ")
                                   .append(element.getCustomer()).append(" | ")
                                   .append(element.getFlight()).append("\n");
                        }
                        System.out.println(builder.toString());
                    } else {
                        System.out.println("Response is empty");
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException exception) {
                    // nothing to do
                }
            }
            
            client.close();
        }

        public void terminate() {
            running = false;
        }

    }

}
