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
package org.apache.karaf.examples.starter.jpa.client;

import org.apache.karaf.examples.starter.jpa.Booking;
import org.apache.karaf.examples.starter.jpa.BookingService;

/**
 * Simple bean controlling a thread to display the bookings on System.out.
 */
public class ListBean {

    private BookingService bookingService;
    private ListThread thread;

    // injected with blueprint
    public void setBookingService(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    public void start() {
        thread = new ListThread(bookingService);
    }

    public void stop() {
        thread.terminate();
    }

    private class ListThread extends Thread {

        private BookingService bookingService;
        private volatile boolean running = true;

        public ListThread(BookingService bookingService) {
            this.bookingService = bookingService;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    if (bookingService != null) {

                        Booking booking = new Booking();
                        booking.setCustomer("John Doo");
                        booking.setFlight("AF3030");
                        bookingService.add(booking);

                        System.out.println(displayBookings());
                    }
                    Thread.sleep(5000);
                } catch (Exception e) {
                    // nothing to do
                }
            }
        }

        private String displayBookings() {
            if (bookingService != null) {
                StringBuilder builder = new StringBuilder();
                for (Booking booking : bookingService.list()) {
                    builder.append(booking.getId()).append(" | ").append(booking.getCustomer()).append(" | ").append(booking.getFlight()).append("\n");
                }
                return builder.toString();
            } else {
                return "";
            }
        }

        public void terminate() {
            running = false;
        }

    }

}
