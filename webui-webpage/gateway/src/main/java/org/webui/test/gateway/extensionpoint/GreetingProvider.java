package org.webui.test.gateway.extensionpoint;

/**
 * The runtime object that an extension point <i>produces</i>.
 *
 * <p>An extension point separates <i>configuration</i> (the resource, stored on disk) from
 * <i>behavior</i> (this interface). Each configured extension point resource is turned into one
 * live {@code GreetingProvider} instance by its type's {@link
 * GreetingProviderExtensionPoint#createProvider} factory method.
 *
 * <p>In a real module this would be something meaningful - an alarm notification profile, a device
 * driver, a tag historian, etc. Here it simply produces a greeting string.
 */
public interface GreetingProvider {

  /**
   * @return the greeting produced by this provider, computed fresh on each call so that
   *     time-sensitive providers can vary their output.
   */
  String getGreeting();
}
