# Apache Karaf JDBC example

## Abstract

This example shows how to used simple JDBC implementation using Pax-JDBC with an Apache Derby embedded database.

It provides a very simple command, calling a `BookingService`.

## Artifacts

* **karaf-jdbc-example-api** is a common bundle providing the `Booking` POJO and the `BookingService` interface.
* **karaf-jdbc-example-provider** is a very simple Karaf bundle providing `BookingService` implementation.
* **karaf-jdbc-example-command** is the actual bundle providing the Karaf shell command.
* **karaf-jdbc-example-features** provides a Karaf features repository used for the deployment.

## Build

The build uses Apache Maven. Simply use:

```
mvn clean install
```

## Features and Deployment

On a running Karaf instance, register the features repository:

```
karaf@root()> feature:repo-add mvn:org.apache.karaf.examples/karaf-jdbc-example-features/4.2.0-SNAPSHOT/xml
```

Then, you can install the service provider feature:

```
karaf@root()> feature:install karaf-jdbc-example-provider
```

The shell commands can be installed with the corresponding feature:

```
karaf@root()> feature:install karaf-jdbc-example-command
```

## Usage

Once you have installed the feature, you can see new commands available in the Apache Karaf shell.

`booking:add` command adds a new booking in the booking service. For instance:

```
karaf@root()> booking:add AF520 Doe
```

`booking:list` command lists the current bookings:

```
karaf@root()> booking:list
ID      │ Flight │ Customer
────────┼────────┼─────────
2226065 │ AF520  │ Doe
```

`booking:get` command get the booking with id:

```
karaf@root()> booking:get 2226065
ID      │ Flight │ Customer
────────┼────────┼─────────
2226065 │ AF520  │ Doe
```

`booking:remove` command removes a booking from the booking service:

```
karaf@root()> booking:remove 2226065
```