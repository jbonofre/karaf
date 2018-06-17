# Apache Karaf REST Example

## Abstract

This example shows how to use JAX-RS to implement a REST service.

This example uses blueprint to deal with the jaxrs-server and Apache CXF as the implementation of the JAXRS specification.

It implements a `BookingService` with a REST implementation. 

The "client" bundle uses the `BookingService` with a REST client stub.

## Artifacts

* **karaf-rest-example-api** is a common bundle containing the `Booking` POJO and the `BookingService` interface.   
* **karaf-rest-example-provider** is a blueprint bundle providing the `BookingServiceRest` implementation of the `BookingService` interface.
* **karaf-rest-example-client** is a regular Blueprint bundle using the `BookingService`.
* **karaf-rest-example-client-http** is a regular Blueprint REST client bundle using Java Http.
* **karaf-rest-example-client-cxf** is a regular Blueprint REST client bundle using Apache CXF.
* **karaf-rest-example-features** provides a Karaf features repository used for the deployment.

## Build

The build uses Apache Maven. Simply use:

```
mvn clean install
```

## Feature and Deployment

On a running Karaf instance, register the features repository using:

```
karaf@root()> feature:repo-add mvn:org.apache.karaf.examples/karaf-rest-example-features/4.2.1-SNAPSHOT/xml
```

Then, you can install the service provider feature:

```
karaf@root()> feature:install karaf-rest-example-provider
```

And the service client feature using Java Http:

```
karaf@root()> feature:install karaf-rest-example-client-http
```

And the service client feature using Apache CXF:

```
karaf@root()> feature:install karaf-rest-example-client-cxf
```

## Usage

Once you have install a client feature, a new "client" thread is started, adding and listing bookings directly in the 
shell console using the REST service.