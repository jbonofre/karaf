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
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.spring.boot.SpringBootService;

import java.util.List;

@Service
@Command(scope = "spring-boot", name = "start", description = "Start an already installed spring-boot fatjar")
public class StartCommand implements Action {
    @Reference
    private SpringBootService springBootService;

    @Argument(name = "jarname", description = "The fatjar name followed by the main args", required = true, multiValued = true)
    private List<String> args;

    @Override
    public Object execute() throws Exception {
        if (args == null || args.isEmpty()) {
            throw new IllegalArgumentException("Missing jar name");
        }
        String name = args.remove(0);
        String[] concreteArgs = args.toArray(new String[0]);
        springBootService.start(name , concreteArgs);
        return "Spring Boot app " + name + " started with " + concreteArgs;
    }
}