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
package org.apache.karaf.examples.starter.osgi.internal;

import org.apache.karaf.examples.starter.osgi.common.Booking;
import org.apache.karaf.examples.starter.osgi.common.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Very simple implementation of the booking service using in memory list.
 */
public class BookingServiceImpl implements BookingService {

    private final static Logger LOGGER = LoggerFactory.getLogger(BookingServiceImpl.class);

    private Map<Long, Booking> bookings = new HashMap<>();

    @Override
    public List<Booking> list() {
        LOGGER.debug("Get bookings");
        return new LinkedList<>(bookings.values());
    }

    @Override
    public Booking get(Long id) {
        return bookings.get(id);
    }

    @Override
    public void add(Booking booking) {
        bookings.put(booking.getId(), booking);
    }

    @Override
    public void confirm(Long id) {
        Booking booking = bookings.get(id);
        if (booking == null) {
            throw new NoSuchElementException("Booking " + id + " is not found");
        }
        booking.setStatus(Booking.Status.CONFIRMED);
    }

    @Override
    public void cancel(Long id) {
        Booking booking = bookings.get(id);
        if (booking == null) {
            throw new NoSuchElementException("Booking " + id + " is not found");
        }
        booking.setStatus(Booking.Status.CANCELED);
    }

    @Override
    public void delete(Long id) {
        bookings.remove(id);
    }

}
