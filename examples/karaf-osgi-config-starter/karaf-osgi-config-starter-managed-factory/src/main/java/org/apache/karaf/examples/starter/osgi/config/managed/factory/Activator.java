package org.apache.karaf.examples.starter.osgi.config.managed.factory;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * This activator registers a managed service factory automatically called when the configuration is updated.
 * It's possible to create multiple instance based on the config-factory syntax.
 * The configuration PID is defined as managed service factory property.
 */
public class Activator implements BundleActivator {

    private ServiceRegistration<ManagedServiceFactory> serviceRegistration;

    @Override
    public void start(BundleContext bundleContext) {
        ConfigManagedServiceFactory configManagedServiceFactory = new ConfigManagedServiceFactory();
        Hashtable<String, Object> serviceProperties = new Hashtable<>();
        serviceProperties.put(Constants.SERVICE_PID, "org.apache.karaf.examples.starter.osgi.config.managed.factory");
        serviceRegistration = bundleContext.registerService(ManagedServiceFactory.class, configManagedServiceFactory, serviceProperties);
    }

    @Override
    public void stop(BundleContext bundleContext) {
        if (serviceRegistration != null) {
            serviceRegistration.unregister();
        }
    }

    class ConfigManagedServiceFactory implements ManagedServiceFactory {

        @Override
        public String getName() {
            return "Karaf OSGi Starter Config Managed Service Factory";
        }

        @Override
        public void updated(String s, Dictionary<String, ?> dictionary) throws ConfigurationException {
            System.out.println("Configuration has changed/created:");
            Enumeration<String> keys = dictionary.keys();
            while (keys.hasMoreElements()) {
                String key = keys.nextElement();
                System.out.println("\t" + key + " = " + dictionary.get(key));
            }
        }

        @Override
        public void deleted(String pid) {
            System.out.println("Config " + pid + " deleted");
        }
    }

}
