# Apache Karaf JPA Starter

## Abstract

This starter example shows how to use JPA with an entity. The entity manager is used in the persistence implementation.

It implements a `BookingService` similar to the one in `karaf-osgi-starter` example, but using a database for the storage, using a JPA entity.

This example uses blueprint to deal with JPA entity manager.

As for the `karaf-osgi-starter` example, the "client" bundle uses the `BookingService`.

## Artifacts

The karaf-jpa-starter contains three artifacts:

* karaf-jpa-starter-provider is a blueprint bundle providing the `Booking` entity used in the `BookingService`. As a best practice, this bundle should use a common bundle containing
the `BookingService` interface (as we do in `karaf-osgi-starter` example), and then wrapping `Booking` POJO as a `JpaBooking` entity for instance. For convenience and reduce the number of
example artifacts, we gather interface and implementation in the same bundle (again, it's bad).
* karaf-jpa-starter-client is a regular bundle using the `BookingService`.
* karaf-jpa-starter-features provides a Karaf features repository used for the deployment.

## Build

The build uses Apache Maven. Simply use:

```
mvn clean install
```

It will compile, package and install bundles and features into your local Maven repository.

## Feature and Deployment

On a running Karaf instance, register the features repository using:

```
karaf@root()> feature:repo-add mvn:org.apache.karaf.examples/karaf-jpa-starter-features/4.1.0-SNAPSHOT/xml
```

Then, you can install the datasource feature:

```
karaf@root()> feature:install karaf-jpa-starter-datasource
```

Then, you can install the service provider feature:

```
karaf@root()> feature:install karaf-jpa-starter-provider
```

And the service client feature:

```
karaf@root()> feature:install karaf-jpa-starter-client
```
