# Apache Karaf OSGi Starter

## Abstract

This starter example shows how to implement and register a service in a bundle and reference this service from another service.

A bundle is a Java JAR containing OSGi headers in the MANIFEST. In the Karaf Examples Starters, we use the `maven-bundle-plugin` to generate the bundle MANIFEST.

An OSGi service is described by an interface. A service is actually an object instance implementing the interface (acting as a service contract).

A bundle can implement and register a service in the Karaf Service Registry.

Then, another bundle can look for a service reference from the Service Registry using the interface. Optionally, a service registration can have properties (key-value pairs).
The "client" bundle can filter the services using those properties.

The registration of a service is done in the Activator of the "provider" bundle. An Activator is a special class (implementing BundleActivator interface) implementing `start()` and `stop()` methods.
When you start a bundle, Karaf automatically calls the `start()` method of the bundle Activator.
When you stop a bundle, Karaf automatically calls the `stop()` method of the bundle Activator.

The bundle Activator is defined in the OSGi headers (in the bundle MANIFEST).

You can list the content of the Karaf Service Registry using the `service:list` command. YOu can also see the services registered by a specific bundle using the `bundle:services` command.

As a service is dynamic (it can be registered or unregistered at any time depending of the lifecycle of the "provider" bundle), the "client" bundle has to track the current state of a
service and react accordingly.
To do that, the most elegant solution is to use a Service Tracker.

## Artifacts

The karaf-osgi-starter contains three bundles:

* karaf-osgi-starter-common is a very simple bundle providing the package with the interface describing the service. In this example, we are using a airline booking service.
* karaf-osgi-starter-provider is a bundle containing a very simple implementation of the booking service. This bundle contains an activator to register the service in the registry.
* karaf-osgi-starter-client is a bundle containing a activator. This activator doesn't register a booking service but use a service tracker to track the state of booking service and display the list of bookings every 5 seconds.
* karaf-osgi-starter-feature provides a Karaf features repository used for the deployment.

## Build

The build uses Apache Maven. Simply use:

```
mvn clean install
```

It will compile, package and install bundles and features into your local Maven repository.

## Feature and Deployment

On a running Karaf instance, register the features repository using:

```
karaf@root()> feature:repo-add mvn:org.apache.karaf.examples/karaf-osgi-starter-features/4.1.0-SNAPSHOT/xml
```

Then, you can install the service provider feature:

```
karaf@root()> feature:install karaf-osgi-starter-provider
```

And the service client feature:

```
karaf@root()> feature:install karaf-osgi-starter-client
```

NB: to see the behavior of the service tracker, you can stop the `karaf-osgi-starter-provider` bundle, then you will see the thread automatically stopped in `karaf-osgi-starter-client` bundle.