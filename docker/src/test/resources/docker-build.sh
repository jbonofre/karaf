#!/bin/bash
dockerClient build --cache-from openjdk:8-jre --tag karaf/karaf-dockerClient:4.2.0 .