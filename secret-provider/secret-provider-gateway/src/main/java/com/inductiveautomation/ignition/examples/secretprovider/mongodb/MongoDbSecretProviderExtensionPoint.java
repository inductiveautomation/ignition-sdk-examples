package com.inductiveautomation.ignition.examples.secretprovider.mongodb;

import com.inductiveautomation.ignition.gateway.config.AbstractExtensionPoint;
import com.inductiveautomation.ignition.gateway.config.ExtensionPointConfig;
import com.inductiveautomation.ignition.gateway.config.ValidationErrors;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.SchemaUtil;
import com.inductiveautomation.ignition.gateway.secrets.*;
import com.inductiveautomation.ignition.gateway.web.nav.ExtensionPointResourceForm;
import com.inductiveautomation.ignition.gateway.web.nav.WebUiComponent;

import java.util.Optional;

public class MongoDbSecretProviderExtensionPoint
        extends AbstractExtensionPoint<MongoDbSecretProviderResource>
        implements SecretProviderType<MongoDbSecretProviderResource> {

    public static final String EXTENSION_POINT_TYPE = "MONGODB";

    public MongoDbSecretProviderExtensionPoint() {
        super(EXTENSION_POINT_TYPE,
                "MongoDbSecretProvider.SecretProviderType.Name",
                "MongoDbSecretProvider.SecretProviderType.Desc");

        // The 'password' field can be a secret reference, so we define a reference property for it
        // to handle name changes to the provider pointed to by a referenced secret.
        //
        // Ignition 8.3.3 or later is required to use the SecretReferenceProperty helper class.
        // Alternatively, you can implement similar logic manually by creating a custom
        // ReferencePropertyBuilder.
        addReferenceProperty("password",
            SecretReferenceProperty.<MongoDbSecretProviderResource>builder()
                .setGetSecretConfigFunction(MongoDbSecretProviderResource::password)
                .setUpdateSecretConfigFunction((resource, secret) -> new MongoDbSecretProviderResource(
                    resource.connectionString(),
                    resource.databaseName(),
                    resource.username(),
                    secret,
                    resource.authenticationDb()
                ))
                .build());
    }

    @Override
    public SecretProvider createProvider(SecretProviderContext context) throws SecretProviderTypeException {
        ExtensionPointConfig<SecretProviderConfig, ?> config = context.getResource().config();
        MongoDbSecretProviderResource settings = getSettings(config)
                .orElseThrow(() -> new IllegalStateException("Secret provider configuration missing for: "
                        + context.getResource().name()));
        return new MongoDbSecretProvider(context, settings);
    }

    @Override
    public Optional<MongoDbSecretProviderResource> defaultSettings() {
        return Optional.of(MongoDbSecretProviderResource.DEFAULT);
    }

    @Override
    public Optional<WebUiComponent> getWebUiComponent(ComponentType type) {
        return Optional.of(
                new ExtensionPointResourceForm(
                        SecretProviderConfig.RESOURCE_TYPE,
                        "Secret Provider",
                        EXTENSION_POINT_TYPE,
                        SchemaUtil.fromType(SecretProviderConfig.class),
                        SchemaUtil.fromType(MongoDbSecretProviderResource.class)
                )
        );
    }

    @Override
    protected void validate(MongoDbSecretProviderResource settings, ValidationErrors.Builder errors) {
        super.validate(settings, errors);
        settings.validate(errors);
    }
}
