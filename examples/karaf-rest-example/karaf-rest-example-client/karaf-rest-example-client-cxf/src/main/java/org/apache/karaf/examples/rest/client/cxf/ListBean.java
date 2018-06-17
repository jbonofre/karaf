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
                    // create booking with id = 1
                    System.out.println("Call POST id=1 " + client.getBaseURI());
                    Booking booking = new Booking();
                    booking.setId(new Long(1));
                    booking.setCustomer("Obiwan Kenobi");
                    booking.setFlight("AF3030");
                    client.header("Content-Type", "application/json");
                    client.accept(MediaType.APPLICATION_JSON).post(booking);
                    
                    // get booking with id = 1
                    System.out.println("Call GET " + client.getBaseURI() + "/1");
                    booking = client.replacePath("/1").accept(MediaType.APPLICATION_JSON)
                            .get(Booking.class);
                    
                    if (booking != null) {
                        System.out.println(booking.getId() + " | "
                                + booking.getCustomer() + " | "
                                + booking.getFlight() + "\n");
                    } else {
                        System.out.println("Response is empty");
                    }
                    
                    // create booking with id = 2
                    System.out.println("Call POST id=2 " + client.getBaseURI());
                    booking = new Booking();
                    booking.setId(new Long(2));
                    booking.setCustomer("Luke Skywalker");
                    booking.setFlight("AF3030");
                    client.header("Content-Type", "application/json");
                    client.replacePath("/").accept(MediaType.APPLICATION_JSON).post(booking);
                    
                    // get booking with id = 2
                    System.out.println("Call GET " + client.getBaseURI() + "/2");
                    booking = client.replacePath("/2").accept(MediaType.APPLICATION_JSON)
                            .get(Booking.class);
                    
                    if (booking != null) {
                        System.out.println(booking.getId() + " | "
                                + booking.getCustomer() + " | "
                                + booking.getFlight() + "\n");
                    } else {
                        System.out.println("Response is empty");
                        booking = new Booking();
                    }
                    
                    // update booking with id = 2
                    System.out.println("Call PUT id=2 " + client.getBaseURI());
                    booking = new Booking();
                    booking.setId(new Long(2));
                    booking.setCustomer("Luke Skywalker");
                    booking.setFlight("AF1234");
                    client.header("Content-Type", "application/json");
                    client.replacePath("/").accept(MediaType.APPLICATION_JSON).put(booking);
                    
                    // get all bookings
                    System.out.println("Call GET " + client.getBaseURI() + "/");
                    List<Booking> response = client.replacePath("/").accept(MediaType.APPLICATION_JSON)
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
                    
                    // delete booking with id = 2
                    System.out.println("Call DELETE " + client.getBaseURI() + "/2");
                    client.replacePath("/2").accept(MediaType.APPLICATION_JSON).delete();
                    
                    // get all bookings
                    System.out.println("Call " + client.getBaseURI() + "/");
                    response = client.replacePath("/").accept(MediaType.APPLICATION_JSON)
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
