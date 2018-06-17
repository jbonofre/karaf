# Apache Karaf Deployer example

## Abstract

This example shows how to create a Karaf deployer.

Karaf uses deployer services on the `deploy` folder (using Felix FileInstall). For each file in the `deploy`, Karaf calls
each deployer service to know:

1. If the deployer service should handle the file (based on file contain, file name, ...)
2. Actually let the deployer service deploys the file.

## Build

The build uses Apache Maven. Simply use:

```
mvn clean install
```

## Deployment

On a running Karaf instance, just install the deployer service using:

```
karaf@root()> feature:install scr
karaf@root()> bundle:install -s mvn:org.apache.karaf.examples/karaf-deployer-example/4.2.1-SNAPSHOT
```


## Usage

Once deployed, you can drop any file in the `deploy` folder, you should see (for instance, if you dropped a file name `TEST` in the `deploy` folder):

```
Example deployer should install TEST
```

If you change the file, you should see:

```
Example deployer should update TEST
```

And if you remove the file from the `deploy` folder, you should see:

```
Example deployer should uninstall TEST
```