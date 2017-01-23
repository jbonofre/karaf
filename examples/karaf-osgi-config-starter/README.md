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

Register the feature in a running Karaf instance:

```
karaf@root()> feature:repo-add mvn:org.apache.karaf.examples/karaf-osgi-config-starter-features/4.1.0-SNAPSHOT/xml
```

To test the managed service, you can deploy the `karaf-osgi-config-starter-managed` feature:

```
karaf@root()> feature:install karaf-osgi-config-starter-managed
```

This feature creates a `etc/org.apache.karaf.examples.starter.osgi.config.managed.cfg` configuration file containing:

```
foo=bar
```

If you change the content of this file, for instance adding `key=value`, you will see the following message in the Karaf shell console:

```
Configuration has changed/created:
        felix.fileinstall.filename = file:/home/jbonofre/Workspace/karaf/assemblies/apache-karaf/target/apache-karaf-4.1.0-SNAPSHOT/etc/org.apache.karaf.examples.starter.osgi.config.managed.cfg
        service.pid = org.apache.karaf.examples.starter.osgi.config.managed
        test = other
```

The managed service factory behaves the same, but you can also create a new file like `etc/org.apache.karaf.examples.starter.osgi.config.managed.factory-test.cfg` (containing `test=other` for example) and you will see:

```
Configuration has changed/created:
        felix.fileinstall.filename = file:/home/jbonofre/Workspace/karaf/assemblies/apache-karaf/target/apache-karaf-4.1.0-SNAPSHOT/etc/org.apache.karaf.examples.starter.osgi.config.managed.factory-test.cfg
        service.factoryPid = org.apache.karaf.examples.starter.osgi.config.managed.factory
        service.pid = org.apache.karaf.examples.starter.osgi.config.managed.factory.1c41dd91-b9ca-4127-a051-6dbc5b498d37
        test = other
```