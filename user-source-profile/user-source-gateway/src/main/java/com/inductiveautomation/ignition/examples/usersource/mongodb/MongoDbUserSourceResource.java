package com.inductiveautomation.ignition.examples.usersource.mongodb;

import com.inductiveautomation.ignition.common.resourcecollection.ResourceType;
import com.inductiveautomation.ignition.gateway.config.ResourceTypeMeta;
import com.inductiveautomation.ignition.gateway.config.ValidationErrors;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.*;
import com.inductiveautomation.ignition.gateway.secrets.SecretConfig;
import com.inductiveautomation.ignition.gateway.web.nav.FormFieldType;
import org.apache.commons.lang3.StringUtils;

/**
 * Configuration for a MongoDB user source profile. This resource will be persisted to disk as part of the user
 * source profile config.
 */
public record MongoDbUserSourceResource(
        @FormCategory("CUSTOM SETTINGS")
        @Label("Connection String")
        @FormField(FormFieldType.TEXT)
        @DefaultValue("mongodb://localhost:27017")
        @DescriptionKey("MongoDbUserSource.connectionString.Desc")
        String connectionString,

        @FormCategory("CUSTOM SETTINGS")
        @Label("Database Name")
        @FormField(FormFieldType.TEXT)
        @DefaultValue("user_db")
        @DescriptionKey("MongoDbUserSource.databaseName.Desc")
        String databaseName,

        @FormCategory("CUSTOM SETTINGS")
        @Label("Username")
        @FormField(FormFieldType.TEXT)
        @DescriptionKey("MongoDbUserSource.username.Desc")
        String username,

        @FormCategory("CUSTOM SETTINGS")
        @Label("Password")
        @FormField(FormFieldType.SECRET)
        @DescriptionKey("MongoDbUserSource.password.Desc")
        SecretConfig password,

        @FormCategory("CUSTOM SETTINGS")
        @Label("Authentication Database")
        @FormField(FormFieldType.TEXT)
        @DefaultValue("admin")
        @DescriptionKey("MongoDbUserSource.authenticationDb.Desc")
        String authenticationDb,

        @FormCategory("CUSTOM SETTINGS")
        @Label("Maximum Password Age")
        @FormField(FormFieldType.NUMBER)
        @DefaultValue("90")
        @Minimum("0")
        @Maximum(value = "360", exclusive = true)
        @NonSecret
        @DescriptionKey("MongoDbUserSource.passwordMaxAge.Desc")
        Integer passwordMaxAge,

        @FormCategory("CUSTOM SETTINGS")
        @Label("Password History")
        @FormField(FormFieldType.NUMBER)
        @DefaultValue("5")
        @Minimum("0")
        @NonSecret
        @DescriptionKey("MongoDbUserSource.passwordHistory.Desc")
        Integer passwordHistory
) {
    public static final ResourceType RESOURCE_TYPE = new ResourceType(GatewayHook.MODULE_ID, "mongodb-user-source");

    public static final MongoDbUserSourceResource DEFAULT = new MongoDbUserSourceResource(
            "mongodb://localhost:27017",
            "user_db",
            null,
            null,
            "admin",
            90,
            5
    );

    public static final ResourceTypeMeta<MongoDbUserSourceResource> META = ResourceTypeMeta.newBuilder(MongoDbUserSourceResource.class)
            .resourceType(RESOURCE_TYPE)
            .categoryName("MongoDB User Source")
            .defaultConfig(DEFAULT)
            .buildValidator((resource, validator) -> {
                // Custom validation logic for the resource. This gets called anytime the resource system creates
                // an instance of this resource, such as when a user source profile is created, updated, or loaded.
                validator.checkField(
                        resource.passwordMaxAge() >= 0 && resource.passwordMaxAge() < 360,
                        "passwordMaxAge",
                        "passwordMaxAge must be in the range [0, 360)"
                );
                validator.checkField(
                        resource.passwordHistory() < 0,
                        "passwordHistory",
                        "passwordHistory must be greater than or equal to 0"
                );
                if (StringUtils.isNotBlank(resource.username()) && resource.password != null
                        && StringUtils.isBlank(resource.authenticationDb)) {
                    validator.addFieldMessage(
                            "authenticationDb",
                            "authenticationDb must be set when username and password are provided");
                }
            })
            .build();

    /**
     * Canonical constructor that fills in default values for any null or blank parameters.
     *
     * @param connectionString The MongoDB connection string.
     * @param databaseName     The name of the database containing user information.
     * @param username         The username for authenticating to MongoDB.
     * @param password         The password for authenticating to MongoDB.
     * @param authenticationDb The database to authenticate against.
     * @param passwordMaxAge   Maximum password age in days.
     * @param passwordHistory  Number of previous passwords to remember.
     */
    public MongoDbUserSourceResource {
        if (StringUtils.isBlank(connectionString)) {
            connectionString = DEFAULT.connectionString();
        }

        if (StringUtils.isBlank(databaseName)) {
            databaseName = DEFAULT.databaseName();
        }

        if (StringUtils.isBlank(authenticationDb)) {
            authenticationDb = DEFAULT.authenticationDb();
        }

        if (passwordMaxAge == null) {
            passwordMaxAge = DEFAULT.passwordMaxAge();
        }

        if (passwordHistory == null) {
            passwordHistory = DEFAULT.passwordHistory();
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
         errors.checkField(this.passwordMaxAge >= 0 && this.passwordMaxAge < 360,
             "passwordMaxAge",
             "passwordMaxAge must be in the range [0, 360)");
         errors.checkField(this.passwordHistory >= 0,
             "passwordHistory",
             "passwordHistory must be greater than or equal to 0");
    }
}
