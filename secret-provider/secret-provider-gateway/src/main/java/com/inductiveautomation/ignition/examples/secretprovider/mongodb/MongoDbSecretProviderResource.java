package com.inductiveautomation.ignition.examples.secretprovider.mongodb;

import com.inductiveautomation.ignition.common.resourcecollection.ResourceType;
import com.inductiveautomation.ignition.gateway.config.ResourceTypeMeta;
import com.inductiveautomation.ignition.gateway.config.ValidationErrors;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.*;
import com.inductiveautomation.ignition.gateway.secrets.SecretConfig;
import com.inductiveautomation.ignition.gateway.web.nav.FormFieldType;
import org.apache.commons.lang3.StringUtils;

/**
 * Configuration for a MongoDB secret provider. This resource will be persisted to disk as part of the secret
 * provider config.
 */
public record MongoDbSecretProviderResource(
        @FormCategory("CUSTOM SETTINGS")
        @Label("Connection String")
        @FormField(FormFieldType.TEXT)
        @DefaultValue("mongodb://localhost:27017")
        @Required
        @DescriptionKey("MongoDbSecretProvider.connectionString.Desc")
        String connectionString,

        @FormCategory("CUSTOM SETTINGS")
        @Label("Database Name")
        @FormField(FormFieldType.TEXT)
        @DefaultValue("secrets_db")
        @Required
        @DescriptionKey("MongoDbSecretProvider.databaseName.Desc")
        String databaseName,

        @FormCategory("CUSTOM SETTINGS")
        @Label("Username")
        @FormField(FormFieldType.TEXT)
        @DescriptionKey("MongoDbSecretProvider.username.Desc")
        String username,

        @FormCategory("CUSTOM SETTINGS")
        @Label("Password")
        @FormField(FormFieldType.SECRET)
        @DescriptionKey("MongoDbSecretProvider.password.Desc")
        SecretConfig password,

        @FormCategory("CUSTOM SETTINGS")
        @Label("Authentication Database")
        @FormField(FormFieldType.TEXT)
        @DefaultValue("admin")
        @DescriptionKey("MongoDbSecretProvider.authenticationDb.Desc")
        String authenticationDb
) {
    public static final ResourceType RESOURCE_TYPE = new ResourceType(GatewayHook.MODULE_ID, "mongodb-user-source");

    public static final MongoDbSecretProviderResource DEFAULT = new MongoDbSecretProviderResource(
            "mongodb://localhost:27017",
            "secrets_db",
            null,
            null,
            "admin"
    );

    public static final ResourceTypeMeta<MongoDbSecretProviderResource> META = ResourceTypeMeta.newBuilder(MongoDbSecretProviderResource.class)
            .resourceType(RESOURCE_TYPE)
            .categoryName("MongoDB Secret Provider")
            .defaultConfig(DEFAULT)
            .buildValidator((resource, validator) -> {
                // Custom validation logic for the resource. This gets called anytime the resource system creates
                // an instance of this resource, such as when a secret provider is created, updated, or loaded.
            })
            .build();

    /**
     * Canonical constructor that fills in default values for any null or blank parameters.
     *
     * @param connectionString The MongoDB connection string.
     * @param databaseName     The name of the database containing secret provider documents.
     * @param username         The username for authenticating to MongoDB.
     * @param password         The password for authenticating to MongoDB.
     * @param authenticationDb The database to authenticate against.
     */
    public MongoDbSecretProviderResource {
        if (StringUtils.isBlank(connectionString)) {
            connectionString = DEFAULT.connectionString();
        }

        if (StringUtils.isBlank(databaseName)) {
            databaseName = DEFAULT.databaseName();
        }

        if (StringUtils.isBlank(authenticationDb)) {
            authenticationDb = DEFAULT.authenticationDb();
        }
    }

    /**
     * Perform validation on the resource fields. These checks will be invoked when the resource is
     * created or updated. Error messages will be returned via the REST API and displayed in the
     * web UI.
     *
     * @param errors The builder to collect validation errors.
     */
    void validate(ValidationErrors.Builder errors) {
        // Optionally, add validation to an incoming configuration object.
    }
}
