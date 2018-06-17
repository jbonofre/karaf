# Apache Karaf JPA Example

## Abstract

This example shows how to use JPA with an entity. The entity manager is used in the persistence implementation.

It implements a `BookingService` similar using a database for the storage, with a JPA entity.

This example uses blueprint to deal with JPA entity manager.

The "client" bundle uses the `BookingService`.

## Artifacts

* karaf-jpa-example-provider is a blueprint bundle providing the `Booking` entity used in the `BookingService`. As a best practice, this bundle should use a common bundle containing
the `BookingService` interface, and then wrapping `Booking` POJO as a `JpaBooking` entity for instance. For convenience and reduce the number of
example artifacts, we gather interface and implementation in the same bundle (again, it's bad).
* karaf-jpa-example-client is a regular Blueprint bundle using the `BookingService`.
* karaf-jpa-example-features provides a Karaf features repository used for the deployment.

## Build

The build uses Apache Maven. Simply use:

```
mvn clean install
```

## Feature and Deployment

On a running Karaf instance, register the features repository using:

```
karaf@root()> feature:repo-add mvn:org.apache.karaf.examples/karaf-jpa-example-features/4.2.1-SNAPSHOT/xml
```

Then, you can install the datasource feature:

```
karaf@root()> feature:install karaf-jpa-example-datasource
```

Then, you can install the service provider feature:

```
karaf@root()> feature:install karaf-jpa-example-provider
```

And the service client feature:

```
karaf@root()> feature:install karaf-jpa-example-client
```
