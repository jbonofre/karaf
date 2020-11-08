package org.apache.karaf.devx.examples.generate;

public class InnerService {

    private SimpleService simpleService;

    public void setSimpleService(SimpleService simpleService) {
        this.simpleService = simpleService;
    }

    public String exposed(String message) {
        return simpleService.greeting(message);
    }

}
