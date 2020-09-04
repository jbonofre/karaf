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
package org.apache.karaf.devx.examples.runtime;

import org.apache.karaf.devx.core.runtime.KarafApplication;
import org.apache.karaf.devx.core.runtime.annotation.Boot;
import org.apache.karaf.devx.core.runtime.annotation.Config;
import org.apache.karaf.devx.core.runtime.annotation.Datasource;
import org.apache.karaf.devx.core.runtime.annotation.Feature;
import org.apache.karaf.devx.core.runtime.annotation.Management;
import org.apache.karaf.devx.core.runtime.annotation.Property;
import org.apache.karaf.devx.core.runtime.annotation.Repository;
import org.apache.karaf.devx.core.runtime.annotation.Runtime;
import org.apache.karaf.devx.core.runtime.annotation.Security;

import java.util.Map;

@Runtime(name = "karaf-custom-runtime",
        environment = Runtime.Environment.DYNAMIC,
        jre = Runtime.Jre.JAVA_11,
        frameworks = {
            Runtime.Framework.STANDARD,
            Runtime.Framework.ENTERPRISE},
        repositories = {
            @Repository(name = "camel", url = "org.apache.camel.karaf/apache-camel", version = "3.0.0")},
        features = {
            @Feature(name = "http", version = "LATEST"),
            @Feature(name = "http-whiteboard", version = "LATEST")},
        bootPackages = {
            @Boot(name = "jaxrs", version = "1.2.0", pack = Boot.Pack.JAXRS),
            @Boot(name = "jpa", version = "2.1.0", pack = Boot.Pack.JPA)})
public class BootRuntime {

    @Config(pid = "my.app.config",
            policy = Config.Policy.CREATE,
            properties = {
                @Property(key = "key1", value = "value1"),
                @Property(key = "key2", value = "value2")
            })
    public Map<String, String> myConfigFile;

    @Config(pid = "my.app.config.resource",
            policy = Config.Policy.UPDATE,
            resource = "application.properties",
            properties = {
                @Property(key = "key11", value = "value11"),
                @Property(key = "key22", value = "value22")
            })
    public Map<String, String> myConfigFileFormResource;

    @Datasource(name = "jdbc/my-datasource",
            properties = {
                    @Property(key = Datasource.Key.DatabaseName, value = "database1"),
                    @Property(key = Datasource.Key.DatabaseServer, value = "localhost"),
                    @Property(key = Datasource.Key.DatabasePort, value = "5432"),
                    @Property(key = Datasource.Key.DatabaseUser, value = "user"),
                    @Property(key = Datasource.Key.DatabasePassword, value = "password"),
                    @Property(key = Datasource.Key.Driver, value = "org.postgres.Driver"),
            })
    public Map<String, String> myDatasourceFile;

    @Datasource(name = "jdbc/my-datasource-file",
            resource = "datasource.properties",
            properties = {
                    @Property(key = Datasource.Key.DatabaseUser, value = "user"),
                    @Property(key = Datasource.Key.DatabasePassword, value = "password"),
            })
    public Map<String, String> myDatasourceFileFromResource;

    @Management(type = Management.Type.Shell,
            properties = {
                    @Property(key = Management.Shell.RmiRegistryHost, value = "127.0.0.1"),
                    @Property(key = Management.Shell.RmiRegistryPort, value = "44444"),
            })
    private Map<String, String> myShellConfig;

    @Management(type = Management.Type.Http,
            properties = {
                    @Property(key = Management.Http.HttpPort, value = "8181"),
                    @Property(key = Management.Http.HttpsPort, value = "443"),
            })
    private Map<String, String> myHttpConfig;

    @Security(type = Security.Type.Group,
            properties = {
                    @Property(key = Security.Role.admin),
                    @Property(key = Security.Role.group),
                    @Property(key = Security.Role.ssh)
            })
    private Map<String, String> mySecurityCustomGroup;

    @Security(type = Security.Type.User,
            properties = {
                    @Property(key = "toto", value = "my-group"),
                    @Property(key = "titi", value = Security.Group.admingroup)
            })
    private Map<String, String> mySecurityUserConfig;


    public static void main(String[] args) {
        KarafApplication.run(BootRuntime.class, args);
    }

}
