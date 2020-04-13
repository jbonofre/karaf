package org.apache.karaf.devx.examples.first.impl;

import org.apache.karaf.devx.examples.first.api.MyFirst;
import org.osgi.service.component.annotations.Component;

@Component
public class MyFirstImpl implements MyFirst {

    @Override
    public String echo(String message) {
        return message;
    }

}
