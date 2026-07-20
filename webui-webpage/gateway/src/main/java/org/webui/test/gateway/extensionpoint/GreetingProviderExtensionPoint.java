package org.webui.test.gateway.extensionpoint;

import com.inductiveautomation.ignition.common.gson.JsonObject;
import com.inductiveautomation.ignition.common.resourcecollection.ResourceType;
import com.inductiveautomation.ignition.gateway.config.AbstractExtensionPoint;
import com.inductiveautomation.ignition.gateway.config.DecodedResource;
import com.inductiveautomation.ignition.gateway.config.ExtensionPointConfig;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.SchemaUtil;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.web.nav.ExtensionPointResourceForm;
import com.inductiveautomation.ignition.gateway.web.nav.WebUiComponent;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * Abstract base class for every "Greeting Provider" extension point type.
 *
 * <p>This is the "category" class. Following the platform convention, each extension point category
 * (alarm journals, device drivers, ... and here, greeting providers) defines an abstract subclass
 * of {@link AbstractExtensionPoint} that:
 *
 * <ul>
 *   <li>pins the {@link #resourceType()} to the category's resource type,
 *   <li>declares an abstract factory method ({@link #createProvider}) that concrete types implement
 *       to produce the runtime object, and
 *   <li>provides the web UI form descriptor via {@link #getWebUiComponent(ComponentType)}.
 * </ul>
 *
 * <p>Concrete types (see {@link StaticGreetingProviderExtensionPoint} and {@link
 * TimeBasedGreetingProviderExtensionPoint}) extend this and are returned from the gateway hook's
 * {@code getExtensionPoints()}.
 *
 * @param <S> the type of this extension point's type-specific settings object
 */
public abstract class GreetingProviderExtensionPoint<S> extends AbstractExtensionPoint<S> {

  private final String displayName;
  private final String displayDescription;
  private final Class<S> settingsType;

  protected GreetingProviderExtensionPoint(
      String typeId, String displayName, String description, Class<S> settingsType) {
    // AbstractExtensionPoint resolves name/description through i18n bundle keys; this example
    // overrides name()/description() below to return raw strings, so we simply pass them through.
    super(typeId, displayName, description);
    this.displayName = displayName;
    this.displayDescription = description;
    this.settingsType = settingsType;
  }

  @Override
  public ResourceType resourceType() {
    return GreetingProviderConfig.RESOURCE_TYPE;
  }

  @Override
  public String name(Locale locale) {
    return displayName;
  }

  @Override
  public String description(Locale locale) {
    return displayDescription;
  }

  @Override
  public Optional<Class<S>> settingsType() {
    return Optional.ofNullable(settingsType);
  }

  /**
   * Supplies the React component used to add/edit an instance of this extension point type.
   *
   * <p>This example returns a platform-provided, <b>schema-driven</b> {@link
   * ExtensionPointResourceForm} rather than a bespoke React component. The gateway renders an editor
   * form directly from the profile and settings JSON schemas (derived from the annotated config
   * records), so the module ships <b>no</b> front-end code for the editor. A module that needs a
   * custom UI could instead return a {@link
   * com.inductiveautomation.ignition.gateway.web.nav.ReactComponentInfo ReactComponentInfo}
   * pointing at a component in its own JS bundle.
   */
  @Override
  public Optional<WebUiComponent> getWebUiComponent(ComponentType type) {
    JsonObject settingsSchema =
        settingsType == null ? new JsonObject() : SchemaUtil.fromType(settingsType);
    return Optional.of(
        new ExtensionPointResourceForm(
            GreetingProviderConfig.RESOURCE_TYPE,
            "Greeting Provider",
            typeId(),
            SchemaUtil.fromType(GreetingProviderConfig.class),
            settingsSchema,
            Set.of()));
  }

  /**
   * Convenience overload that decodes the settings out of the resource before delegating to {@link
   * #createProvider(GatewayContext, DecodedResource, Object)}.
   */
  public GreetingProvider createProvider(
      GatewayContext context,
      DecodedResource<ExtensionPointConfig<GreetingProviderConfig, ?>> resource) {
    S settings = getSettings(resource.config()).orElse(null);
    return createProvider(context, resource, settings);
  }

  /**
   * Factory method: build the live {@link GreetingProvider} for a configured resource.
   *
   * @param context the gateway context
   * @param resource the decoded resource, giving access to the shared profile config
   * @param settings the type-specific settings (may be null if this type has none)
   */
  public abstract GreetingProvider createProvider(
      GatewayContext context,
      DecodedResource<ExtensionPointConfig<GreetingProviderConfig, ?>> resource,
      S settings);
}
