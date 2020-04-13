package org.apache.karaf.devx.examples.inner.impl;

import org.apache.karaf.devx.examples.inner.api.MyFirstService;
import org.osgi.service.component.annotations.Component;

@Component
public class MyFirstServiceImpl implements MyFirstService {

    @Override
    public String echo(String message) {
        return message;
    }

}
