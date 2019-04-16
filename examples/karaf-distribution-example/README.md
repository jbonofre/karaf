<!--
    Licensed to the Apache Software Foundation (ASF) under one
    or more contributor license agreements.  See the NOTICE file
    distributed with this work for additional information
    regarding copyright ownership.  The ASF licenses this file
    to you under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.
-->
# Apache Karaf Distribution example

## Abstract

If a "classic" use case is to start from a Apache Karaf vanilla (from Karaf website) and deploy your applications in
a running instance, you can also create your own Karaf distribution.

It's what we named a custom distribution.

A custom Karaf distribution is a Karaf instance where you can "pre-install" and "pre-configure" your applications.

Karaf gives you the complete flexibility to have the kind of distributions addressing your use cases. You can build
two kinds of distributions:
* dynamic where you start Karaf with some applications at startup, and you can add additional applications later. It works as an applications container.
* static where you start Karaf with applications in kind of immutable state. To update the configurations or the applications, you create a new distribution that you start again.

## Build

To build the two distributions (dynamic and static), just use:

```
mvn clean install
```

## Distributions

The example creates two kind of distributions, both starting the Karaf WebConsole by default.

### Dynamic/Container

### Static/Immutable