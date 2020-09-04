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
package org.apache.karaf.devx.extensions.maven;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.util.List;
import java.util.Properties;

@Mojo(name = "package", defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, threadSafe = true)
public class DistributionMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject mavenProject;

    @Parameter(defaultValue = "${session}", required = true, readonly = true)
    private MavenSession mavenSession;

    @Parameter(defaultValue = "4.2.7", required = true, readonly = false)
    private String karafVersion;

    @Parameter(defaultValue = "true", required = true, readonly = false)
    private boolean dynamicRuntime;

    @Parameter(defaultValue = "true", required = true, readonly = false)
    private boolean generateDockerFile;

    @Parameter(defaultValue = "false", required = true, readonly = false)
    private boolean buildDockerImage;

    @Parameter(defaultValue = "karaf", required = true, readonly = false)
    private String dockerImageName;

    @Parameter(defaultValue = "1.8", required = true, readonly = false)
    private String javase;

    @Parameter(defaultValue = "${project.artifactId}-${project.version}", required = true, readonly = false)
    private String archiveFinalName;

    @Parameter(defaultValue = "8101", required = true, readonly = false)
    private String karafSshPort;

    @Parameter(defaultValue = "1099", required = true, readonly = false)
    private String rmiRegistryPort;

    @Parameter(defaultValue = "444444", required = true, readonly = false)
    private String rmiServerPort;

    @Parameter(defaultValue = "127.0.0.1", required = true, readonly = false)
    private String rmiRegistryHost;

    @Parameter(defaultValue = "127.0.0.1", required = true, readonly = false)
    private String rmiServerHost;

    @Parameter(defaultValue = "8181", required = true, readonly = false)
    private String karafHttpPort;

    @Parameter(defaultValue = "${localRepository}")
    protected ArtifactRepository localRepo;

    @Parameter(defaultValue = "${project.remoteArtifactRepositories}")
    protected List<ArtifactRepository> remoteRepos;

    @Parameter(defaultValue = "${project.build.directory}/assembly")
    protected File assemblyDirectory;

    @Component
    private BuildPluginManager buildPluginManager;

    @Component
    protected ArtifactResolver artifactResolver;

    @Component
    protected ArtifactHandler artifactHandler;

    @Component
    private ArtifactFactory artifactFactory;

    private Plugin karafToolPlugin;

    /**
     * Execution of the Mojo.
     * @throws MojoExecutionException
     */
    public void execute() throws MojoExecutionException {
        karafToolPlugin = new Plugin();
        karafToolPlugin.setGroupId("org.apache.karaf.tooling");
        karafToolPlugin.setArtifactId("karaf-maven-plugin");
        karafToolPlugin.setVersion(karafVersion);
        karafToolPlugin.setInherited(true);
        karafToolPlugin.setExtensions(true);

        mavenProject.getDependencyArtifacts().add(resolveKarafFramework());

        mavenProject.getDependencyArtifacts().add(resolveKarafFeature("framework"));
        mavenProject.getDependencyArtifacts().add(resolveKarafFeature("standard"));
        //mavenProject.getDependencyArtifacts().add(resolveKarafFeature("enterprise"));

        mavenProject.getDependencyArtifacts().stream().filter(
                artifact -> artifact.getScope().equals("compile")).forEach(artifact ->
                getLog().info(artifact.getArtifactId()));

        // build distribution
        getLog().info("Building distribution");
        buildDistribution();

        // generate docker file
        if (generateDockerFile) {
            getLog().info("Generating docker file");
            generateDockerFile();
        }

        // build docker image
        if (buildDockerImage) {
            getLog().info("Building docker image");
            buildDockerImage();
        }
    }

    /**
     * Execution of the assembly goal of the karaf-maven-plugin.
     */
    private void buildDistribution() {
        // Build karaf-maven-plugin configuration
        StringBuilder config = new StringBuilder();
        config.append("<configuration>" +
                "<project>${project}</project>" +
                "<mavenSession>${session}</mavenSession>" +
                "<localRepo>${localRepository}</localRepo>" +
                "<workDirectory>${project.build.directory}/assembly</workDirectory>" +
                "<sourceDirectory>${project.basedir}/src/main/resources/assembly</sourceDirectory>");
        config.append("<javase>".concat(javase).concat("</javase>"));
        if (!dynamicRuntime) {
            config.append("<framework>static-framework</framework>");
            config.append("<environment>static</environment>");
        } else {
            config.append("<framework>framework</framework>");
        }

        config.append("<installAllFeaturesByDefault>false</installAllFeaturesByDefault>");

        // Stage.Boot : scope=runtime
        // Stage.Installed : scope=provided
        // Stage.Startup : scope=compile

        //bootFeatures
        config.append("<bootFeatures>standard</bootFeatures>");
        //startupFeatures
        config.append("<startupFeatures>http</startupFeatures>");
        //installedFeatures
        config.append("<installedFeatures></installedFeatures>");

        //bootBundles
        config.append("<bootBundles></bootBundles>");
        //startupBundles
        config.append("<startupBundles>mvn:fr.openobject.karaf.labs/karaf-boot/1.0.0-SNAPSHOT</startupBundles>");
        //installedBundles
        config.append("<installedBundles></installedBundles>");

        //bootRepositories
        config.append("<bootRepositories></bootRepositories>");
        //startupRepositories
        config.append("<startupRepositories></startupRepositories>");
        //installedRepositories
        config.append("<installedRepositories></installedRepositories>");


        config.append("<useReferenceUrls>true</useReferenceUrls>");
        config.append("<writeProfiles>true</writeProfiles>");
        config.append("</configuration>");
        executePlugin(karafToolPlugin, config.toString(), "assembly");

        updateConfigurationFiles();

        config = new StringBuilder();
        config.append("<configuration>");
        config.append("<project>${project}</project>");
        config.append("<targetServerDirectory>${project.build.directory}/assembly</targetServerDirectory>");
        config.append("<targetFile>".concat(archiveFinalName).concat("</targetFile>"));
        config.append("<pathPrefix>${project.artifactId}-${project.version}</pathPrefix>");
        config.append("<destDir>${project.build.directory}</destDir>");
        config.append("</configuration>");
        executePlugin(karafToolPlugin, config.toString(), "archive");
    }

    /**
     * Execution of the dockerfile goal of the karaf-maven-plugin.
     */
    private void generateDockerFile() {
        StringBuilder config = new StringBuilder();
        config.append("<configuration>");
        config.append("<destDir>${project.build.directory}</destDir>");
        config.append("<assembly>${project.build.directory}/assembly</assembly>");
        config.append("<command>[\"karaf\", \"run\"]</command>");
        config.append("</configuration>");
        executePlugin(karafToolPlugin, config.toString(), "dockerfile");
    }

    /**
     * Execution of the docker goal of the karaf-maven-plugin.
     */
    private void buildDockerImage() {

        StringBuilder config = new StringBuilder();
        config.append("<configuration>");
        config.append("<location>${project.build.directory}</location>");
        config.append("<imageName>".concat(dockerImageName).concat("</imageName>"));
        config.append("</configuration>");
        executePlugin(karafToolPlugin, config.toString(), "docker");
    }

    private void executePlugin(Plugin plugin, String config, String goal) {
        try {
            Xpp3Dom configuration = Xpp3DomBuilder.build(new StringReader(config));
            PluginDescriptor pluginDescriptor = buildPluginManager.loadPlugin(plugin,
                    mavenProject.getRemotePluginRepositories(),
                    mavenSession.getRepositorySession());
            MojoDescriptor mojoDescriptor = pluginDescriptor.getMojo(goal);
            MojoExecution execution = new MojoExecution(mojoDescriptor, configuration);
            buildPluginManager.executeMojo(mavenSession, execution);
        } catch (Exception e) {
            getLog().error("Error while invoking the "+ plugin.getArtifactId() + ":" + goal, e);
        }
    }

    private Artifact resolveKarafFramework() {
        try {
            Artifact artifact = artifactFactory.createDependencyArtifact(
                    "org.apache.karaf.features", "framework", VersionRange.createFromVersion(karafVersion), "kar", null, "compile");
            artifactResolver.resolve(artifact, remoteRepos, localRepo);
            return artifact;
        } catch (ArtifactResolutionException | ArtifactNotFoundException e) {
            getLog().error(e.getMessage());
        }
        return null;
    }

    private Artifact resolveKarafFeature(String featureRepo) {
        try {
            Artifact artifact = artifactFactory.createDependencyArtifact(
                    "org.apache.karaf.features", featureRepo, VersionRange.createFromVersion(karafVersion), "xml", "features", "compile");
            artifactResolver.resolve(artifact, remoteRepos, localRepo);
            return artifact;
        } catch (ArtifactResolutionException | ArtifactNotFoundException e) {
            getLog().error(e.getMessage());
        }
        return null;
    }

    private void updateConfigurationFiles() {
        File file = new File(assemblyDirectory.getPath().concat("/etc/org.apache.karaf.management.cfg"));
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            properties.load(fileInputStream);
        } catch (Exception e) {
            getLog().error(e.getMessage());
            return;
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            properties.replace("rmiRegistryHost",rmiRegistryHost);
            properties.replace("rmiRegistryPort",rmiRegistryPort);
            properties.replace("rmiServerHost",rmiServerHost);
            properties.replace("rmiServerPort",rmiServerPort);
            properties.store(fileOutputStream, "Updated by Karaf-boot-plugin");
        } catch (Exception e) {
            getLog().error(e.getMessage());
            return;
        }
    }

}
