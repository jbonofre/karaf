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
package org.apache.karaf.examples.rest.provider;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.karaf.examples.rest.api.Booking;
import org.apache.karaf.examples.rest.api.BookingService;

@Path("/booking")
public class BookingServiceRest implements BookingService {

    @Override
    @Path("/all")
    @Produces("application/json")
    @GET
    public List<Booking> list() {
        List<Booking> bookings = new ArrayList<>();
        Booking booking = new Booking();
        booking.setId(new Long(1));
        booking.setCustomer("Obiwan Kenobi");
        booking.setFlight("AF3030");
        bookings.add(booking);
        booking = new Booking();
        booking.setId(new Long(2));
        booking.setCustomer("Luke Skywalker");
        booking.setFlight("AF3030");
        bookings.add(booking);
        return bookings;
    }

    @Override
    @Path("/{id}")
    @Produces("application/json")
    @GET
    public Booking get(@PathParam("id") Long id) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setCustomer("John Doo");
        booking.setFlight("AF3030");
        return booking;
    }
    
    @Override
    @Path("/")
    @Consumes("application/json")
    @POST
    public void add(Booking booking) {
    }

    @Override
    @Path("/")
    @Consumes("application/json")
    @PUT
    public void update(Booking booking) {
    }

    @Override
    @Path("/{pid}")
    @DELETE
    public void remove(@PathParam("id") Long id) {
    }
}
