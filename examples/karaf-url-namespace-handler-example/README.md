# Apache Karaf URL Namespace Handler Example

## Abstract

This example shows how to create a new URL namespace handler and use it in all Apache Karaf parts.

## Artifacts

* **karaf-url-namespace-handler-example-core** is the core bundle providing the URL handler.
* **karaf-url-namespace-handler-example-features** contains the features repository used for deployment.

## Build

The build uses Apache Maven. Simply use:

```
mvn clean install
```

## Features and Deployment

On a running Karaf instance, register the features repository:

```
karaf@root()> feature:repo-add mvn:org.apache.karaf.examples/karaf-url-namespace-handler-example-features/4.2.0-SNAPSHOT/xml
```

## Usage

Once you have installed the feature, you can use URL like `example:*` wrapping any already supported URL. When you use
the `example:*` URL, a greeting message will be displayed.

For instance:

```
karaf@root()> bundle:install example:mvn:commons-lang/commons-lang/2.6
Thanks for using the Example URL !
Bundle ID: 44
```