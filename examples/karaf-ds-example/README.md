# Apache Karaf DS Example

## Abstract

DS (Declarative Services) is a convenient way to use services with annotations.

As it's part of the OSGi compendium specification, you don't need any large dependency to use it.

## Artifacts

* `karaf-ds-example-common` provides the `BookingService` interface and `Booking` POJO.
* `karaf-ds-example-provider` implements and exposes a `BookingService` using a DS annotations.
* `karaf-ds-example-client` use a `BookingService` reference injected thanks to DS.
* `karaf-ds-example-features` contains a Karaf features repository used for the deployment.

## Build 

Simply use:

```
mvn clean install
```

## Feature and Deployment

On a running Karaf instance, you register the ds example features repository with:

```
karaf@root()> feature:repo-add mvn:org.apache.karaf.examples/karaf-ds-example-features/4.1.0-SNAPSHOT/xml
```

Then you can install the `karaf-ds-example-provider` feature:

```
karaf@root()> feature:install karaf-ds-example-provider
```

Now, you can install the `karaf-ds-example-client` feature:

```
karaf@root()> feature:install karaf-ds-example-client
```

When you install the client feature, you should see on the console:

```

```