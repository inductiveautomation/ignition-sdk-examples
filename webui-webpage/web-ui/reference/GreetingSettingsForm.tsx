/**
 * REFERENCE ONLY — see reference/README.md. Not part of the build.
 *
 * The form rendered inside {@link GreetingSettingsPage}. Uses `GeneratedForm` from a `FormConfig`
 * describing the fields of `GreetingSettings`. `ResourceModeSingletonPage` supplies the
 * react-hook-form `context`, `defaultValues`, and `type` props via cloning, so this component just
 * forwards them along with the form config.
 *
 * Requires @inductiveautomation/ignition-gateway-lib.
 */
import React from "react";
import { GeneratedForm } from "@inductiveautomation/ignition-gateway-lib";

const greetingSettingsFormConfig = {
  fields: [
    {
      fieldName: "pageTitle",
      inputType: "text",
      label: "Page Title *",
      category: "GENERAL",
      yup: { type: "string", default: "WebUI Examples", validations: [{ type: "required" }] },
    },
    {
      fieldName: "showTimestamp",
      inputType: "checkbox",
      label: "Show Timestamp",
      category: "GENERAL",
      yup: { type: "boolean", default: true },
    },
    {
      fieldName: "refreshIntervalMs",
      inputType: "number",
      label: "Refresh Interval (ms) *",
      category: "GENERAL",
      yup: { type: "number", default: 5000, validations: [{ type: "required" }] },
    },
  ],
};

const GreetingSettingsForm = (props: any) => (
  <GeneratedForm {...props} formConfig={greetingSettingsFormConfig} />
);

export default GreetingSettingsForm;
