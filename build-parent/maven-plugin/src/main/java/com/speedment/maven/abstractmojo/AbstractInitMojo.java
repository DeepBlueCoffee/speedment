/**
 * Copyright (c) 2006-2017, Speedment, Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); You may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.speedment.maven.abstractmojo;

import com.speedment.maven.parameter.ConfigParam;
import com.speedment.runtime.config.Dbms;
import com.speedment.runtime.config.Project;
import com.speedment.runtime.config.Schema;
import com.speedment.runtime.config.internal.ProjectImpl;
import com.speedment.runtime.core.ApplicationBuilder;
import com.speedment.runtime.core.Speedment;
import com.speedment.tool.config.ProjectProperty;
import com.speedment.tool.config.internal.component.DocumentPropertyComponentImpl;
import com.speedment.tool.core.internal.util.ConfigFileHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.util.*;
import java.util.function.Consumer;

/**
 * A maven goal (re)creates the configuration file with just the provided initialisation parameters.
 *
 * @author Mark Schrijver
 * @since 3.0.17
 */
public abstract class AbstractInitMojo extends AbstractSpeedmentMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject mavenProject;

    private @Parameter(defaultValue = "${debug}")
    Boolean debug;
    private @Parameter(defaultValue = "${companyName}")
    String companyName;
    private @Parameter(defaultValue = "${appName}")
    String appName;
    private @Parameter(defaultValue = "${package.location}")
    String packageLocation;
    private @Parameter(defaultValue = "${package.name}")
    String packageName;
    private @Parameter(defaultValue = "${dbms.type}")
    String dbmsType = "MySQL"; // default to MySQL
    private @Parameter(defaultValue = "${dbms.host}")
    String dbmsHost;
    private @Parameter(defaultValue = "${dbms.port}")
    Integer dbmsPort = Integer.valueOf(3306); // Default port for MySQL
    private @Parameter(defaultValue = "${dbms.schemas}")
    String dbmsSchemas;
    private @Parameter(defaultValue = "${dbms.username}")
    String dbmsUsername;
    private @Parameter(defaultValue = "${dbms.password}")
    String dbmsPassword;
    private @Parameter
    ConfigParam[] parameters;
    private @Parameter(defaultValue = "${configFile}")
    String configFile;

    protected AbstractInitMojo() {
    }

    protected AbstractInitMojo(Consumer<ApplicationBuilder<?, ?>> configurer) {
        super(configurer);
    }

    @Override
    protected void execute(Speedment speedment) throws MojoExecutionException, MojoFailureException {
        getLog().info("Saving default configuration from database to '" + configLocation().toAbsolutePath() + "'.");

        final ConfigFileHelper helper = speedment.getOrThrow(ConfigFileHelper.class);

        ProjectProperty project = createProject();

        helper.saveProjectToCurrentlyOpenFile(project);

        try {
            helper.setCurrentlyOpenFile(configLocation().toFile());
        } catch (final Exception ex) {
            final String err = "An error occured while reloading.";
            getLog().error(err);
            throw new MojoExecutionException(err, ex);
        }
    }

    @Override
    protected MavenProject project() {
        return mavenProject;
    }

    @Override
    protected boolean debug() {
        return debug == null ? false : debug;
    }

    public String getConfigFile() {
        return configFile;
    }

    @Override
    protected String[] components() {
        return null;
    }

    @Override
    protected String[] typeMappers() {
        return null;
    }

    @Override
    protected ConfigParam[] parameters() {
        return parameters;
    }

    @Override
    protected String dbmsHost() {
        return dbmsHost;
    }

    @Override
    protected int dbmsPort() {
        return (dbmsPort == null) ? 0 : dbmsPort;
    }

    @Override
    protected String dbmsUsername() {
        return dbmsUsername;
    }

    @Override
    protected String dbmsPassword() {
        return dbmsPassword;
    }

    private ProjectProperty createProject() {
        ProjectProperty projectProperty = new ProjectProperty();

        Map<String, Object> projectData = new HashMap<>();
        addStringToMap(Project.COMPANY_NAME, companyName, projectData);
        addStringToMap(Project.NAME, appName, projectData);
        addStringToMap(Project.APP_ID, UUID.randomUUID().toString(), projectData);
        addStringToMap(Project.PACKAGE_LOCATION, packageLocation, projectData);
        addStringToMap(Project.PACKAGE_NAME, packageName, projectData);
        addStringToMap(Project.ID, appName, projectData);
        addBooleanToMap(Project.ENABLED, Boolean.TRUE, projectData);
        addListToMap(Project.DBMSES, createDbmses(), projectData);

        projectProperty.merge(new DocumentPropertyComponentImpl(), new ProjectImpl(projectData));

        return projectProperty;
    }

    private List<Map<String, Object>> createDbmses() {
        List<Map<String, Object>> dbmses = new ArrayList<>(1);
        dbmses.add(createDbms());
        return dbmses;
    }

    private Map<String, Object> createDbms() {
        Map<String, Object> dbmsData = new HashMap<>();
        addStringToMap(Dbms.NAME, appName, dbmsData);
        addStringToMap(Dbms.TYPE_NAME, dbmsType, dbmsData);
        addStringToMap(Dbms.ID, appName, dbmsData);
        addIntegerToMap(Dbms.PORT, dbmsPort, dbmsData);
        addStringToMap(Dbms.IP_ADDRESS, dbmsHost, dbmsData);
        addStringToMap(Dbms.USERNAME, dbmsUsername, dbmsData);
        addBooleanToMap(Dbms.ENABLED, Boolean.TRUE, dbmsData);
        addListToMap(Dbms.SCHEMAS, createSchemas(), dbmsData);
        return dbmsData;
    }

    private List<Map<String, Object>> createSchemas() {
        List<Map<String, Object>> schemas = new ArrayList<>();
        if (StringUtils.isNotBlank(dbmsSchemas)) {
            Arrays.stream(dbmsSchemas.split(",")).forEach((schema) -> {
                schemas.add(createSchema(schema));
            });
        }
        return schemas;
    }

    private Map<String, Object> createSchema(String schema) {
        String schemaName = schema.trim();
        Map<String, Object> schemaData = new HashMap<>();
        addStringToMap(Schema.NAME, schemaName, schemaData);
        addStringToMap(Schema.ID, schemaName, schemaData);
        addBooleanToMap(Schema.ENABLED, Boolean.TRUE, schemaData);
        return schemaData;
    }

    private void addStringToMap(String key, String value, Map<String, Object> map) {
        if (StringUtils.isNotBlank(value)) {
            map.put(key, value);
        }
    }

    private void addIntegerToMap(String key, Integer value, Map<String, Object> map) {
        if (value != null) {
            map.put(key, value);
        }
    }

    private void addBooleanToMap(String key, Boolean value, Map<String, Object> map) {
        if (value != null) {
            map.put(key, value);
        }
    }

    private void addListToMap(String key, List<Map<String, Object>> value, Map<String, Object> map) {
        if (value != null) {
            map.put(key, value);
        }
    }
}