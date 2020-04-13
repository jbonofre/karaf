package org.apache.karaf.devx.examples.first;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

@Component
public class MyFirst {

    @Activate
    public void activate() {
        System.out.println("Hello world");
    }

    @Deactivate
    public void deactivate() {
        System.out.println("Bye bye world");
    }

}
