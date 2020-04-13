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

# Apache Karaf DevX

Karaf DevX focus on simplifying and improving the developer experience on Apache Karaf.

The purpose is to address the following:

* the developer should not deal with "bundle headers" anymore. The developer just writes a regular jar file and devX is dealing with the rest.
* no need to write a feature XML or JSON anymore. Most of the time, the features XML is just a wrapper for the bundle and required dependencies.
* easy packaging and Karaf distribution. Today, creating a custom distribution embedding an application is not straight forward. DevX simplify the creation of Karaf distribution and Docker packaging.

Other improvements are also:

* support of a "simple" resolver, just taking step by step the feature description (no complex resolution or refresh). It delegates the "clever" resolution to the user.
* support of features as JSON (in addition of XML)

DevX is also targeting to support other kind of programming framework like CDI and Spring Boot.