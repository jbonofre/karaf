package org.apache.karaf.devx.examples.generate;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

    ServiceRegistration<SimpleService> simpleServiceServiceRegistration;

    ServiceTracker<SimpleService, SimpleService> simpleServiceServiceTracker;
    ServiceRegistration<InnerService> innerServiceServiceRegistration;

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        simpleServiceServiceRegistration = bundleContext.registerService(SimpleService.class, new SimpleService(), null);

        simpleServiceServiceTracker = new ServiceTracker<SimpleService, SimpleService>(bundleContext, SimpleService.class, null) {
            @Override
            public SimpleService addingService(ServiceReference<SimpleService> reference) {
                SimpleService simpleService = bundleContext.getService(reference);
                InnerService innerService = new InnerService();
                innerService.setSimpleService(simpleService);
                innerServiceServiceRegistration = bundleContext.registerService(InnerService.class, innerService, null);
                return simpleService;
            }

            @Override
            public void removedService(ServiceReference<SimpleService> reference, SimpleService service) {
                innerServiceServiceRegistration.unregister();
            }
        };
        simpleServiceServiceTracker.open();
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        simpleServiceServiceTracker.close();

        simpleServiceServiceRegistration.unregister();
    }

}
