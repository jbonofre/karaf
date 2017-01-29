# Apache Karaf Blueprint Starter

## Abstract

In the `karaf-osgi-starter` example, we use the "pure" OSGi approach to expose a service.

The `Activator` gives you a complete control but requires you to write boilerplate code.

An alternative to pure OSGi is to use Blueprint.

Blueprint is similar to Spring with support of OSGi services. It allows you to describe and register your services using
a XML descriptor. This XML description can be written by hand (like in the `karaf-blueprint-starter-provider` module) or
using annotations (like in the `karaf-blueprint-starter-annotation` module).

The code is pretty close to the one in `karaf-osgi-starter` but the `Activator` is replaced by Blueprint XML in the
`OSGI-INF/blueprint` directory of the bundle (you can have several Blueprint XML in this directory, all considered as a big one).
The file name of the Blueprint XML doesn't matter.

## Artifacts

* `karaf-blueprint-starter-provider` uses `OSGI-INF/blueprint/provider.xml` Blueprint XML to expose an OSGi service.
* `karaf-blueprint-starter-client` uses `OSGI-INF/blueprint/client.xml` Blueprint XML to get a service.
* `karaf-blueprint-starter-annotation` generated the Blueprint XML via a Maven plugin and using annotations directly in the code.
* `karaf-blueprint-starter-features` contains a Karaf features repository used for the deployment.

## Build 

Simply use:

```
mvn clean install
```

## Feature and Deployment

On a running Karaf instance, you register the blueprint starter features repository with:

```
karaf@root()> feature:repo-add mvn:org.apache.karaf.examples/karaf-blueprint-starter-features/4.1.0-SNAPSHOT/xml
```

Then you can install the `karaf-blueprint-starter-provider` feature:

```
karaf@root()> feature:install karaf-blueprint-starter-provider
```

OR the `karaf-blueprint-starter-annotation` feature:

```
karaf@root()> feature:install karaf-blueprint-starter-annotation
```

Now, you can install the `karaf-blueprint-starter-client` feature:

```
karaf@root()> feature:install karaf-blueprint-starter-client
```

When you install the client feature, you should see on the console:

```

```