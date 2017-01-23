# Apache Karaf OSGi Config Starter

## Abstract

This starter shows how to use a configuration in your bundle. If it's always possible to directly access the
Karaf `ConfigurationAdmin` service (it's a regular service, so you can use the same approach as in the `karaf-osgi-starter` example),
this example shows to use a `ManagedService` or `ManagedServiceFactory` allowing you to dynamically react when the configuration changed.

This starter actually provides two examples:

* `karaf-osgi-config-starter-managed` is a bundle with an `Activator` registering a `ManagedService` service. A `ManagedService` service
implements an `update()` method automatically called when the configuration is updated (for instance listening for changes in 
`etc/org.apache.karaf.examples.starter.osgi.managed.cfg` configuration file).
* `karaf-osgi-config-starter-managed-factory` is a bundle with an `Activator` registering a `ManagedServiceFactory` service.
It's very similar to a `ManagedService` service but allows you to dynamically create new instance using the `configpid-factorypid` syntax.

So, this starter creates a bundle with an `Activator`. This `Activator` registers a `ManagedService` service.
The `ManagedService` interface implements an `updated()` method automatically called when the configuration is updated.

## Artifacts

The `karaf-osgi-config-starter` provides the following artifacts:

* `karaf-osgi-config-starter-managed` is a bundle registering a `ManagedService` service for the `org.apache.karaf.examples.starter.osgi.managed` configuration.
* `karaf-osgi-config-starter-managed-factory` is a bundle registering a `ManagedServiceFactory` service for the `org.apache.karaf.examples.starter.osgi.managed.factory-XXX` configurations.
* `karaf-osgi-config-starter-features` provides a Karaf features repository used for deployment.

## Build

As always:

```
mvn clean install
```

## Feature and Deployment

