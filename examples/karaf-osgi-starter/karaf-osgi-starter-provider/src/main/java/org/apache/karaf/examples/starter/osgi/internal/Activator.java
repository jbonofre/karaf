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

import org.apache.karaf.examples.starter.osgi.common.BookingService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;
import java.util.Properties;

/**
 * OSGi bundle activator registering the booking service.
 */
public class Activator implements BundleActivator {

    private final static Logger LOGGER = LoggerFactory.getLogger(Activator.class);

    private ServiceRegistration bookingServiceRegistration;

    @Override
    public void start(BundleContext bundleContext) {
        LOGGER.debug("Starting booking service provider");
        LOGGER.debug("Creating an instance of the booking service impl");
        BookingService bookingService = new BookingServiceImpl();
        LOGGER.debug("Registering the booking service");
        Dictionary bookingServiceProperties = new Properties();
        bookingServiceProperties.put("foo", "bar");
        bookingServiceRegistration = bundleContext.registerService(BookingService.class, bookingService, bookingServiceProperties);
    }

    @Override
    public void stop(BundleContext bundleContext) {
        LOGGER.debug("Stopping booking service provider");
        LOGGER.debug("Un-registering the booking service");
        bookingServiceRegistration.unregister();
    }

}
