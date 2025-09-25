package com.inductiveautomation.ignition.examples.usersource.mongodb;

import com.inductiveautomation.ignition.gateway.config.DecodedResource;
import com.inductiveautomation.ignition.gateway.config.ExtensionPointConfig;
import com.inductiveautomation.ignition.gateway.config.ValidationErrors;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.SchemaUtil;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.secrets.SecretConfig;
import com.inductiveautomation.ignition.gateway.secrets.SecretProviderConfig;
import com.inductiveautomation.ignition.gateway.user.UserSourceExtensionPoint;
import com.inductiveautomation.ignition.gateway.user.UserSourceProfile;
import com.inductiveautomation.ignition.gateway.user.UserSourceProfileConfig;
import com.inductiveautomation.ignition.gateway.user.UserSourceProfileKernel;
import com.inductiveautomation.ignition.gateway.web.nav.ExtensionPointResourceForm;
import com.inductiveautomation.ignition.gateway.web.nav.WebUiComponent;

import java.util.Optional;

/**
 * The {@link MongoDbUserSourceExtensionPoint} is responsible for creating instances of the MongoDbUserSource
 * when a user source profile of this type is configured in the Gateway.
 */
public class MongoDbUserSourceExtensionPoint extends UserSourceExtensionPoint<MongoDbUserSourceResource> {
    public static final String EXTENSION_POINT_TYPE = "MONGODB";

    public MongoDbUserSourceExtensionPoint() {
        super(EXTENSION_POINT_TYPE,
                "MongoDbUserSource.UserSourceType.Name",
                "MongoDbUserSource.UserSourceType.Desc",
                MongoDbUserSourceResource.class);

        // The password in our configuration might be a referenced secret that points to a secret provider, so we need
        // to add a reference property for it. We need to register our reference property and consume updates / renames
        // of the SecretProvider to keep our configuration in sync.
        addReferenceProperty("password", builder -> builder
                .targetType(SecretProviderConfig.RESOURCE_TYPE)
                .value(resource -> {
                    // Return the SecretProvider name if the password is a referenced secret.
                    SecretConfig secretConfig = resource.password();
                    if (secretConfig != null && secretConfig.isReferenced()) {
                        return secretConfig.getAsReferenced().getProviderName();
                    }
                    return null;
                })
                .caseSensitive(true)
                .onUpdate((resource, newName) -> {
                    // Return a new resource with the updated SecretProvider name if the password is a
                    // referenced secret.
                    SecretConfig secretConfig = resource.password();
                    if (secretConfig != null && secretConfig.isReferenced()) {
                        return new MongoDbUserSourceResource(
                                resource.connectionString(),
                                resource.databaseName(),
                                resource.username(),
                                SecretConfig.referenced(newName, secretConfig.getAsReferenced().getSecretName()),
                                resource.authenticationDb(),
                                resource.passwordMaxAge(),
                                resource.passwordHistory()
                        );
                    }
                    return resource; // Should never get here, but return the original resource if we do.
                })
        );
    }

    @Override
    public UserSourceProfile createNewProfile(
            GatewayContext context,
            DecodedResource<ExtensionPointConfig<UserSourceProfileConfig, ?>> resource) throws Exception {

        String profileName = resource.name();

        // Retrieve the settings for the user source profile from the resource configuration.
        MongoDbUserSourceResource settings = getSettings(resource.config())
                .orElseThrow(
                        () -> new IllegalStateException("User source configuration missing for profile: " + profileName)
                );

        // Create a new UserSourceProfileKernel using the profile name and settings.
        UserSourceProfileKernel kernel = createKernel(profileName, resource.config().profile(), context);
        return new MongoDbUserSource(kernel, settings);
    }

    @Override
    public Optional<MongoDbUserSourceResource> defaultSettings() {
        return Optional.of(MongoDbUserSourceResource.DEFAULT);
    }

    @Override
    public Optional<WebUiComponent> getWebUiComponent(ComponentType type) {
        return Optional.of(
                new ExtensionPointResourceForm(
                        UserSourceProfileConfig.RESOURCE_TYPE,
                        "User Source Profile",
                        EXTENSION_POINT_TYPE,
                        SchemaUtil.fromType(UserSourceProfileConfig.class),
                        SchemaUtil.fromType(MongoDbUserSourceResource.class)
                )
        );
    }

    @Override
    protected void validate(MongoDbUserSourceResource settings, ValidationErrors.Builder errors) {
        /*
         Optionally, add validation to an incoming configuration object
         These error messages will be conveyed back to the standard web UI automatically
        */
        // errors.requireNotNull("someField", settings.auditProfileName());
        super.validate(settings, errors);
    }
}
