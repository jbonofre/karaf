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
package org.apache.karaf.examples.starter.osgi.config.managed;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * This activator registers a managed service automatically called when the configuration is updated.
 * The configuration PID is defined as managed service property.
 */
public class Activator implements BundleActivator {

    private ServiceRegistration<ManagedService> serviceRegistration;

    @Override
    public void start(BundleContext bundleContext) {
        ConfigManagedService managedService = new ConfigManagedService();
        Hashtable<String, Object> serviceProperties = new Hashtable<>();
        serviceProperties.put(Constants.SERVICE_PID, "org.apache.karaf.examples.starter.config.managed");
        serviceRegistration = bundleContext.registerService(ManagedService.class, managedService, serviceProperties);
    }

    @Override
    public void stop(BundleContext bundleContext) {
        if (serviceRegistration != null) {
            serviceRegistration.unregister();
        }
    }

    /**
     * Very simple managed service reacting when the configuration is changing.
     * You can use a managed service to automatically update your POJO/Bean and inject changed configuration properties.
     */
    class ConfigManagedService implements ManagedService {

        @Override
        public void updated(Dictionary dictionary) throws ConfigurationException {
            System.out.println("Configuration has changed:");
            if (dictionary != null) {
                Enumeration<String> keys = dictionary.keys();
                while (keys.hasMoreElements()) {
                    String key = keys.nextElement();
                    System.out.println("\t" + key + " = " + dictionary.get(key));
                }
            }
        }

    }

}
