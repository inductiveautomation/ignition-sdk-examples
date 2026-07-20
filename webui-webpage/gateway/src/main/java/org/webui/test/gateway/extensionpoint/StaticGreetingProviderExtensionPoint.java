package org.webui.test.gateway.extensionpoint;

import com.inductiveautomation.ignition.gateway.config.DecodedResource;
import com.inductiveautomation.ignition.gateway.config.ExtensionPointConfig;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import java.util.Optional;

/**
 * A concrete "Greeting Provider" type that returns a fixed, configured string.
 *
 * <p>This is one of the two example types contributed by the module's {@code getExtensionPoints()}.
 * Registering it makes "Static Greeting" appear as a choice when a user creates a new greeting
 * provider.
 */
public class StaticGreetingProviderExtensionPoint
    extends GreetingProviderExtensionPoint<StaticGreetingSettings> {

  /** Unique-within-the-category type id. Stored as the {@code type} field of the profile config. */
  public static final String TYPE_ID = "static";

  public StaticGreetingProviderExtensionPoint() {
    super(
        TYPE_ID,
        "Static Greeting",
        "Returns a fixed, configured greeting.",
        StaticGreetingSettings.class);
  }

  /** Initial settings offered to the user when creating a new instance of this type. */
  @Override
  public Optional<StaticGreetingSettings> defaultSettings() {
    return Optional.of(new StaticGreetingSettings("Hello from the static provider!"));
  }

  @Override
  public GreetingProvider createProvider(
      GatewayContext context,
      DecodedResource<ExtensionPointConfig<GreetingProviderConfig, ?>> resource,
      StaticGreetingSettings settings) {
    String prefix = resource.config().profile().prefix();
    String prefixText = prefix == null ? "" : prefix;
    String text = settings == null ? "" : settings.text();
    return () -> prefixText + text;
  }
}
