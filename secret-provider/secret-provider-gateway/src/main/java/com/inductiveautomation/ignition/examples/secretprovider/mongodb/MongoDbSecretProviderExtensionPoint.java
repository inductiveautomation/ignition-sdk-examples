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
    }

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
        /*
         Optionally, add validation to an incoming configuration object
         These error messages will be conveyed back to the standard web UI automatically
        */
        // errors.requireNotNull("someField", settings.auditProfileName());
        super.validate(settings, errors);
    }
}
