# Apache Karaf Blueprint Example

## Abstract

Blueprint is a convenient way of using services. You can describe your beans and services using a XML descriptor.
You can write this XML by hand or use annotation and let the `blueprint-maven-plugin` generates the XML for you.



## Artifacts

* `karaf-blueprint-example-common` provides the `BookingService` interface and `Booking` POJO.
* `karaf-blueprint-example-provider` implements and exposes a `BookingService` using a Blueprint XML file.
* `karaf-blueprint-example-client` uses `OSGI-INF/blueprint/client.xml` Blueprint XML to get a service and start a thread.
* `karaf-blueprint-example-features` contains a Karaf features repository used for the deployment.

## Build 

Simply use:

```
mvn clean install
```

## Feature and Deployment

On a running Karaf instance, you register the blueprint example features repository with:

```
karaf@root()> feature:repo-add mvn:org.apache.karaf.examples/karaf-blueprint-example-features/4.2.1-SNAPSHOT/xml
```

Then you can install the `karaf-blueprint-example-provider` feature:

```
karaf@root()> feature:install karaf-blueprint-example-provider
```

Now, you can install the `karaf-blueprint-example-client` feature:

```
karaf@root()> feature:install karaf-blueprint-example-client
```

When you install the client feature, you should see on the console:

```

```
