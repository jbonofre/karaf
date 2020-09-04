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
package org.apache.karaf.devx.core.runtime.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface Management {

    Type type();
    Property[] properties();

    enum Type {
        Shell,
        Http
    }

    abstract class Shell {
        public final static String RmiRegistryHost = "rmiRegistryHost";
        public final static String RmiRegistryPort = "rmiRegistryHost";
        public final static String RmiServerHost = "rmiRegistryHost";
        public final static String RmiServerPort = "rmiRegistryHost";
    }

    abstract class Http {
        public final static String HttpPort = "org.osgi.service.http.port";
        public final static String HttpsPort = "org.osgi.service.https.port";
    }

}
