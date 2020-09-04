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
package org.apache.karaf.devx.core.runtime;

import com.sun.tools.javac.code.Attribute;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonBuilderFactory;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonGeneratorFactory;
import org.apache.karaf.devx.core.runtime.annotation.Config;
import org.apache.karaf.devx.core.runtime.annotation.Datasource;
import org.apache.karaf.devx.core.runtime.annotation.Management;
import org.apache.karaf.devx.core.runtime.annotation.Security;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class RuntimeProcessor extends AbstractProcessor {

    private static final JsonBuilderFactory builderFactory = Json.createBuilderFactory(null);

    public RuntimeProcessor() {
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new HashSet<>();
        set.add(Runtime.class.getName());
        return set;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        JsonObjectBuilder runtime = builderFactory.createObjectBuilder();
        String finalName = null;

        for (Element elem : roundEnv.getElementsAnnotatedWith(Runtime.class)) {
            finalName = "KARAF-INF/".concat(((TypeElement) elem).getQualifiedName().toString().concat(".json"));

            for (AnnotationMirror mirror : elem.getAnnotationMirrors()) {
                if (Runtime.class.getName().equals(((TypeElement) mirror.getAnnotationType().asElement()).getQualifiedName().toString())) {
                    Map<String, Object> values = getAnnotationValues(mirror);
                    if (values.containsKey("name")) {
                        runtime.add("name", String.class.cast(values.get("name")));
                    }
                    if (values.containsKey("environment")) {
                        runtime.add("environment", VariableElement.class.cast(values.get("environment")).getSimpleName().toString());
                    }
                    if (values.containsKey("jre")) {
                        runtime.add("jre", VariableElement.class.cast(values.get("jre")).getSimpleName().toString());
                    }
                    if (values.containsKey("frameworks")) {
                        JsonArrayBuilder frameworks = builderFactory.createArrayBuilder();
                        for (Attribute.Enum enm : (List<Attribute.Enum>) values.get("frameworks")) {
                            frameworks.add(enm.value.getSimpleName().toString());
                        }
                        runtime.add("frameworks", frameworks);
                    }
                    if (values.containsKey("features")) {
                        JsonArrayBuilder features = builderFactory.createArrayBuilder();
                        for (AnnotationMirror r : (List<AnnotationMirror>) values.get("features")) {
                            Map<String, Object> rv = getAnnotationValues(r);
                            features.add(
                                    builderFactory.createObjectBuilder()
                                            .add("name", rv.get("name").toString())
                                            .add("version", String.class.cast(rv.getOrDefault("version", "LATEST")))
                                            .build());
                        }
                        runtime.add("features", features);
                    }
                    if (values.containsKey("repositories")) {
                        JsonArrayBuilder repositories = builderFactory.createArrayBuilder();
                        for (AnnotationMirror r : (List<AnnotationMirror>) values.get("repositories")) {
                            Map<String, Object> rv = getAnnotationValues(r);
                            repositories.add(
                                    builderFactory.createObjectBuilder()
                                            .add("name", rv.get("name").toString())
                                            .add("url", rv.get("url").toString())
                                            .add("version", String.class.cast(rv.getOrDefault("version", "LATEST")))
                                            .build());
                        }
                        runtime.add("repositories", repositories);
                    }
                    if (values.containsKey("bootPackages")) {
                        JsonArrayBuilder bootPackages = builderFactory.createArrayBuilder();
                        for (AnnotationMirror r : (List<AnnotationMirror>) values.get("bootPackages")) {
                            Map<String, Object> rv = getAnnotationValues(r);
                            bootPackages.add(
                                    builderFactory.createObjectBuilder()
                                            .add("name", rv.get("name").toString())
                                            .add("pack", VariableElement.class.cast(rv.get("pack")).getSimpleName().toString())
                                            .add("version", String.class.cast(rv.getOrDefault("version", "LATEST")))
                                            .build());
                        }
                        runtime.add("bootPackages", bootPackages);
                    }
                }
            }
        }

        if (!roundEnv.getElementsAnnotatedWith(Datasource.class).isEmpty()) {
            JsonArrayBuilder datasources = builderFactory.createArrayBuilder();
            for (Element elem : roundEnv.getElementsAnnotatedWith(Datasource.class)) {
                for (AnnotationMirror mirror : elem.getAnnotationMirrors()) {
                    if (Datasource.class.getName().equals(((TypeElement) mirror.getAnnotationType().asElement()).getQualifiedName().toString())) {
                        Map<String, Object> values = getAnnotationValues(mirror);
                        JsonObjectBuilder datasource = builderFactory.createObjectBuilder();
                        if (values.containsKey("name")) {
                            datasource.add("name", String.class.cast(values.get("name")));
                        }
                        if (values.containsKey("resource")) {
                            datasource.add("resource", String.class.cast(values.get("resource")));
                        }
                        if (values.containsKey("properties")) {
                            JsonArrayBuilder properties = builderFactory.createArrayBuilder();
                            for (AnnotationMirror r : (List<AnnotationMirror>) values.get("properties")) {
                                Map<String, Object> rv = getAnnotationValues(r);
                                properties.add(
                                        builderFactory.createObjectBuilder()
                                                .add("key", rv.get("key").toString())
                                                .add("value", rv.get("value").toString())
                                                .build());
                            }
                            datasource.add("properties", properties);
                        }
                        datasources.add(datasource.build());
                    }
                }
            }
            runtime.add("datasource", datasources);
        }

        if (!roundEnv.getElementsAnnotatedWith(Management.class).isEmpty()) {
            JsonArrayBuilder managements = builderFactory.createArrayBuilder();
            for (Element elem : roundEnv.getElementsAnnotatedWith(Management.class)) {
                for (AnnotationMirror mirror : elem.getAnnotationMirrors()) {
                    if (Management.class.getName().equals(((TypeElement) mirror.getAnnotationType().asElement()).getQualifiedName().toString())) {
                        Map<String, Object> values = getAnnotationValues(mirror);
                        JsonObjectBuilder management = builderFactory.createObjectBuilder();
                        if (values.containsKey("type")) {
                            management.add("type", VariableElement.class.cast(values.get("type")).getSimpleName().toString());
                        }
                        if (values.containsKey("properties")) {
                            JsonArrayBuilder properties = builderFactory.createArrayBuilder();
                            for (AnnotationMirror r : (List<AnnotationMirror>) values.get("properties")) {
                                Map<String, Object> rv = getAnnotationValues(r);
                                properties.add(
                                        builderFactory.createObjectBuilder()
                                                .add("key", rv.get("key").toString())
                                                .add("value", rv.get("value").toString())
                                                .build());
                            }
                            management.add("properties", properties);
                        }
                        managements.add(management.build());
                    }
                }
            }
            runtime.add("management", managements);
        }

        if (!roundEnv.getElementsAnnotatedWith(Config.class).isEmpty()) {
            JsonArrayBuilder configs = builderFactory.createArrayBuilder();
            for (Element elem : roundEnv.getElementsAnnotatedWith(Config.class)) {
                for (AnnotationMirror mirror : elem.getAnnotationMirrors()) {
                    if (Config.class.getName().equals(((TypeElement) mirror.getAnnotationType().asElement()).getQualifiedName().toString())) {
                        Map<String, Object> values = getAnnotationValues(mirror);
                        JsonObjectBuilder config = builderFactory.createObjectBuilder();
                        if (values.containsKey("pid")) {
                            config.add("pid", String.class.cast(values.get("pid")));
                        }
                        if (values.containsKey("policy")) {
                            config.add("policy", VariableElement.class.cast(values.get("policy")).getSimpleName().toString());
                        }
                        if (values.containsKey("properties")) {
                            JsonArrayBuilder properties = builderFactory.createArrayBuilder();
                            for (AnnotationMirror r : (List<AnnotationMirror>) values.get("properties")) {
                                Map<String, Object> rv = getAnnotationValues(r);
                                properties.add(
                                        builderFactory.createObjectBuilder()
                                                .add("key", rv.get("key").toString())
                                                .add("value", rv.get("value").toString())
                                                .build());
                            }
                            config.add("properties", properties);
                        }
                        configs.add(config.build());
                    }
                }
            }
            runtime.add("config", configs);
        }

        if (!roundEnv.getElementsAnnotatedWith(Security.class).isEmpty()) {
            JsonArrayBuilder securities = builderFactory.createArrayBuilder();
            for (Element elem : roundEnv.getElementsAnnotatedWith(Security.class)) {
                for (AnnotationMirror mirror : elem.getAnnotationMirrors()) {
                    if (Security.class.getName().equals(((TypeElement) mirror.getAnnotationType().asElement()).getQualifiedName().toString())) {
                        Map<String, Object> values = getAnnotationValues(mirror);
                        JsonObjectBuilder security = builderFactory.createObjectBuilder();
                        if (values.containsKey("type")) {
                            security.add("type", VariableElement.class.cast(values.get("type")).getSimpleName().toString());
                        }
                        if (values.containsKey("properties")) {
                            JsonArrayBuilder properties = builderFactory.createArrayBuilder();
                            for (AnnotationMirror r : (List<AnnotationMirror>) values.get("properties")) {
                                Map<String, Object> rv = getAnnotationValues(r);
                                JsonObjectBuilder property = builderFactory.createObjectBuilder();
                                property.add("key", rv.get("key").toString());
                                Optional.ofNullable(rv.get("value")).ifPresent(value -> property.add("value", value.toString()));
                                properties.add(property.build());
                            }
                            security.add("properties", properties);
                        }
                        securities.add(security.build());
                    }
                }
            }
            runtime.add("security", securities);
        }

        if (finalName != null) {
            try (OutputStream os = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", finalName).openOutputStream();
            ) {
                Map<String, Boolean> config = new HashMap<>();
                config.put(JsonGenerator.PRETTY_PRINTING, true);
                JsonGeneratorFactory factory = Json.createGeneratorFactory(config);
                JsonGenerator generator = factory.createGenerator(os);
                generator.write(runtime.build());
                generator.flush();
                generator.close();

            } catch (Exception e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Error writing to " + finalName + " :: " + e);
            }
        }

        return true;
    }

    private Map<String, Object> getAnnotationValues(AnnotationMirror mirror) {
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : mirror.getElementValues().entrySet()) {
            map.put(entry.getKey().getSimpleName().toString(), entry.getValue().getValue());
        }
        return map;
    }

}
