/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.karaf.spring.boot.command;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.spring.boot.SpringBootService;

import java.net.URI;

@Service
@Command(scope = "spring-boot", name = "install", description = "Install a spring-boot fatjar")
public class InstallCommand implements Action {

    @Reference
    private SpringBootService springBootService;

    @Argument(name = "location", description = "The fatjar location", required = true)
    private String location;

    @Option(name = "name", description = "The name of Spring Boot app", required = false)
    private String name;

    @Option(name = "stack", description = "The stack id to use for that app", required =  false)
    private String stack;

    @Override
    public Object execute() throws Exception {
        String named = springBootService.install(name, URI.create(location), stack);
        return "Spring Boot " + named + " has been installed";
    }
}