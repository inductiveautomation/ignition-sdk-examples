/**
 * REFERENCE ONLY — see reference/README.md. Not part of the build.
 *
 * Canonical SINGLETON settings page, modeled on the platform's Gateway Settings page.
 * `ResourceModeSingletonPage` loads the single resource behind `resourceType`, renders the form
 * (passed as children) inside a save/discard workflow, and persists edits back through the
 * generated singleton route from `GreetingSettings.META`.
 *
 * Requires @inductiveautomation/ignition-gateway-lib.
 */
import React from "react";
import { ResourceModeSingletonPage } from "@inductiveautomation/ignition-gateway-lib";
import GreetingSettingsForm from "./GreetingSettingsForm";

const MODULE_ID = "org.webui.test.WebuiWebpage";

// Categories group fields into sections on the page; here a single "General" section.
const categories = [{ id: "GENERAL", label: "General" }];

const GreetingSettingsPage = () => {
  return (
    <ResourceModeSingletonPage
      resourceType={`${MODULE_ID}/greeting-settings`}
      title="Greeting Settings"
      categories={categories}
    >
      <GreetingSettingsForm />
    </ResourceModeSingletonPage>
  );
};

export default GreetingSettingsPage;
