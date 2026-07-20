/**
 * REFERENCE ONLY — see reference/README.md. Not part of the build.
 *
 * Canonical EXTENSION-POINT page, modeled on the platform's OPC UA Device Connections page.
 *
 * This is the lightest of the three, because most of the work is already done on the Java side:
 * each `GreetingProviderExtensionPoint` returns an `ExtensionPointResourceForm` from
 * `getWebUiComponent(...)`, so `ExtensionPointDataGridPage` renders the type picker and the
 * type-specific add/edit forms directly from the profile + settings schemas — no per-type React.
 *
 * `extensionPointData` (the list of available types + their labels) comes from the resource
 * description route generated from the extension-point `ResourceTypeMeta`.
 *
 * Requires @inductiveautomation/ignition-gateway-lib.
 */
import React from "react";
import {
  ExtensionPointDataGridPage,
  useGetResourceDescriptionQuery,
} from "@inductiveautomation/ignition-gateway-lib";

const MODULE_ID = "org.webui.test.WebuiWebpage";
const RESOURCE_TYPE = `${MODULE_ID}/greeting-provider`;

const columnDefs = [
  { header: "NAME", fieldName: "name" },
  { header: "TYPE", fieldName: "config.profile.type" },
  { header: "PREFIX", fieldName: "config.profile.prefix" },
];

const GreetingProvidersPage = () => {
  // The resource description route reports the registered extension-point types for this resource.
  const { data } = useGetResourceDescriptionQuery(RESOURCE_TYPE);
  const extensionPoints = data?.extensionPoints ?? [];
  const extensionPointLabelMapping = Object.fromEntries(
    extensionPoints.map((ep: any) => [ep.typeId, ep.name])
  );

  return (
    <ExtensionPointDataGridPage
      resourceType={RESOURCE_TYPE}
      itemName="Greeting Provider"
      resourceNoun="Greeting Provider"
      pageTitle="Greeting Providers"
      columnDefs={columnDefs}
      extensionPointData={{ extensionPoints, extensionPointLabelMapping }}
    />
  );
};

export default GreetingProvidersPage;
