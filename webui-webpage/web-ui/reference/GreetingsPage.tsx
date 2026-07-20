/**
 * REFERENCE ONLY — see reference/README.md. Not part of the build.
 *
 * Canonical NAMED-resource page, modeled on the platform's own Database Connections / Alarm
 * Rosters pages. `ResourceModeDataGrid` renders the list, the create/rename/duplicate/delete
 * actions, and the edit drawer; it talks to the CRUD routes generated from `GreetingConfig.META`
 * automatically (via the `resourceType` prop). The create/edit form is produced by `GeneratedForm`
 * from a `FormConfig`.
 *
 * Requires @inductiveautomation/ignition-gateway-lib.
 */
import React, { useCallback } from "react";
import { PageHeader } from "@inductiveautomation/ignition-web-ui";
import {
  Content,
  GeneratedForm,
  ResourceModeDataGrid,
} from "@inductiveautomation/ignition-gateway-lib";

const MODULE_ID = "org.webui.test.WebuiWebpage";

const columnDefs = [
  { header: "NAME", fieldName: "name" },
  { header: "MESSAGE", fieldName: "config.message" },
  { header: "TONE", fieldName: "config.tone" },
  { header: "REPEAT", fieldName: "config.repeatCount" },
];

/**
 * A form config mirrors the fields of GreetingConfig. You can hand-author it (as here, matching the
 * platform's own pages) or generate it from the resource's schema description
 * (`useGetResourceDescriptionQuery`).
 */
const greetingFormConfig = {
  fields: [
    {
      fieldName: "name",
      inputType: "text",
      label: "Name *",
      category: "GENERAL",
      yup: { type: "string", default: "", validations: [{ type: "trim" }, { type: "required" }] },
    },
    {
      fieldName: "message",
      inputType: "text",
      label: "Message *",
      category: "GENERAL",
      yup: { type: "string", default: "Hello, world!", validations: [{ type: "required" }] },
    },
    {
      fieldName: "tone",
      inputType: "select",
      label: "Tone",
      category: "GENERAL",
      settings: {
        selectOptions: [
          { value: "FRIENDLY", label: "Friendly" },
          { value: "FORMAL", label: "Formal" },
          { value: "CASUAL", label: "Casual" },
        ],
      },
      yup: { type: "string", default: "FRIENDLY" },
    },
    {
      fieldName: "repeatCount",
      inputType: "number",
      label: "Repeat Count",
      category: "GENERAL",
      yup: { type: "number", default: 1 },
    },
    {
      fieldName: "shout",
      inputType: "checkbox",
      label: "Shout",
      inputLabel: "Render the greeting in ALL CAPS.",
      category: "GENERAL",
      yup: { type: "boolean", default: false },
    },
  ],
};

const GreetingsPage = () => {
  const GreetingForm = useCallback(
    (props: any) => <GeneratedForm {...props} formConfig={greetingFormConfig} />,
    []
  );

  return (
    <>
      <PageHeader pageTitle="Greetings" />
      <Content>
        <ResourceModeDataGrid
          CreateForm={GreetingForm}
          EditForm={GreetingForm}
          resourceType={`${MODULE_ID}/greeting`}
          itemName="Greeting"
          columnDefs={columnDefs}
          rowSelect
        />
      </Content>
    </>
  );
};

export default GreetingsPage;
