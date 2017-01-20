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
package org.apache.karaf.examples.starter.osgi.client;

import org.apache.karaf.examples.starter.osgi.common.Booking;
import org.apache.karaf.examples.starter.osgi.common.BookingService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple activator starting a thread to periodically add a random booking and display the bookings. We use a service tracker to track the state of the booking service.
 */
public class Activator implements BundleActivator {

    private final static Logger LOGGER = LoggerFactory.getLogger(Activator.class);

    private ServiceTracker<BookingService, BookingDisplayThread> serviceTracker;
    private Thread bookingThread;
    private boolean bookingThreadStarted = false;

    @Override
    public void start(BundleContext bundleContext) {
        serviceTracker = new ServiceTracker<BookingService, BookingDisplayThread>(bundleContext, BookingService.class, null) {

            /**
             * This method is called when a booking service is available (at startup or when it becomes available in the service registry).
             */
            @Override
            public BookingDisplayThread addingService(ServiceReference<BookingService> reference) {
                BookingService bookingService = bundleContext.getService(reference);
                BookingDisplayThread thread = new BookingDisplayThread(bookingService);
                thread.start();
                return thread;
            }

            @Override
            public void removedService(ServiceReference<BookingService> reference, BookingDisplayThread thread) {
                thread.terminate();
                super.removedService(reference, thread);
            }

        };
        serviceTracker.open();
    }

    @Override
    public void stop(BundleContext bundleContext) {
        serviceTracker.close();
    }

    /**
     * Very simple thread adding random booking and displaying the bookings on System.out every 5s.
     */
    private class BookingDisplayThread extends Thread {

        private BookingService bookingService;
        private volatile boolean running = true;

        public BookingDisplayThread(BookingService bookingService) {
            this.bookingService = bookingService;
        }

        @Override
        public void run() {
            while (running) {
                try {

                    // TODO test
                    Booking booking = new Booking("John Doo", "AF3030", Booking.Status.WAITING);
                    bookingService.add(booking);

                    System.out.println(displayBookings());
                    Thread.sleep(5000);
                } catch (Exception e) {
                    LOGGER.error("Error in the karaf-starter client thread", e);
                }
            }
        }

        private String displayBookings() {
            StringBuilder builder = new StringBuilder();
            for (Booking booking : bookingService.list()) {
                builder.append(booking.getId()).append(" | ").append(booking.getCustomer()).append(" | ").append(booking.getFlight()).append(" | ").append(booking.getStatus().toString()).append("\n");
            }
            return builder.toString();
        }

        public void terminate() {
            running = false;
        }

    }

}
