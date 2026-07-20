package org.webui.test.gateway.extensionpoint;

import com.inductiveautomation.ignition.gateway.config.DecodedResource;
import com.inductiveautomation.ignition.gateway.config.ExtensionPointConfig;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import java.time.LocalTime;
import java.util.Optional;

/**
 * A concrete "Greeting Provider" type whose output depends on the time of day. Contrasts with
 * {@link StaticGreetingProviderExtensionPoint} to show two types sharing one extension point
 * category while having entirely different settings and behavior.
 */
public class TimeBasedGreetingProviderExtensionPoint
    extends GreetingProviderExtensionPoint<TimeBasedGreetingSettings> {

  public static final String TYPE_ID = "timeBased";

  public TimeBasedGreetingProviderExtensionPoint() {
    super(
        TYPE_ID,
        "Time-Based Greeting",
        "Returns a greeting appropriate to the current time of day.",
        TimeBasedGreetingSettings.class);
  }

  @Override
  public Optional<TimeBasedGreetingSettings> defaultSettings() {
    return Optional.of(
        new TimeBasedGreetingSettings("Good morning!", "Good afternoon!", "Good evening!"));
  }

  @Override
  public GreetingProvider createProvider(
      GatewayContext context,
      DecodedResource<ExtensionPointConfig<GreetingProviderConfig, ?>> resource,
      TimeBasedGreetingSettings settings) {
    String prefix = resource.config().profile().prefix();
    String prefixText = prefix == null ? "" : prefix;
    TimeBasedGreetingSettings s =
        settings == null
            ? new TimeBasedGreetingSettings("Good morning!", "Good afternoon!", "Good evening!")
            : settings;
    return () -> {
      int hour = LocalTime.now().getHour();
      String greeting;
      if (hour < 12) {
        greeting = s.morning();
      } else if (hour < 18) {
        greeting = s.afternoon();
      } else {
        greeting = s.evening();
      }
      return prefixText + greeting;
    };
  }
}
