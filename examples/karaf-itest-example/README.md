# Apache Karaf Integration Test example

## Abstract

This example shows how to create integration tests.

In addition of unit tests you do in each bundles, you can easily implement integration test in Karaf.

The life cycle is basically:

1. Download and extract any Apache Karaf version.
2. Start the Karaf instance (eventually overwriting some configuration).
3. Execution some actions on this running instance (executing shell commandes, installing features, ...).
4. Verify if the instance state is the expected one.

This simple example shows how to extend `KarafTestSupport` and implements a test performing shell commands and verify
the results.

## Artifacts

The example itself is a Maven project performing the tests.

## Build & Executing Tests

To test, simply use:

```
mvn clean test
```