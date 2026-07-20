# Reference: canonical resource pages (require `ignition-gateway-lib`)

The files in this folder show how you would build the **fully-canonical** gateway web UI
pages for the three example resources — a named-resource grid, a singleton settings page,
and an extension-point grid — exactly the way first-party modules (OPC UA, Alarm
Notification, etc.) do.

They are **not compiled** as part of this example. They live outside `src/`, so webpack,
`tsc`, and ESLint all ignore them. They are here purely as documentation.

## Why they aren't part of the build

These pages import from **`@inductiveautomation/ignition-gateway-lib`**, which contains the
`ResourceModeDataGrid`, `ResourceModeSingletonPage`, and `ExtensionPointDataGridPage`
components (plus the schema-driven `GeneratedForm`). As of this writing that package is **not
published to the SDK npm registry** (`@inductiveautomation/ignition-web-ui` and
`@inductiveautomation/ignition-icons` are). First-party modules resolve it from the Ignition
monorepo workspace, which an external SDK consumer can't do — so an example that imported it
wouldn't `yarn install` / build.

That's why the shipped `src/` landing page is deliberately thin and talks to the resource
REST API directly. The heavy lifting for these examples is on the **Java** side (the
`ResourceTypeMeta` registrations), which is fully functional today.

## Enabling these pages (once `ignition-gateway-lib` is available)

1. Add the dependency in `web-ui/package.json`:
   ```json
   "@inductiveautomation/ignition-gateway-lib": "^1.3.8"
   ```
2. Add it to the `externals` array in `web-ui/webpack.config.js` (the gateway provides it at
   runtime, so it must not be bundled):
   ```js
   "@inductiveautomation/ignition-gateway-lib",
   ```
3. Move the desired page(s) into `src/pages/…` and export them from `src/index.ts`, e.g.:
   ```ts
   export { default as GreetingsPage } from "./pages/GreetingsPage";
   ```
4. Mount them in `WebuiWebpageGatewayHook.setup(...)`, associating each with its resource type
   so search / deep-linking work:
   ```java
   context.getWebResourceManager().getNavigationModel().getConfig()
       .addCategory("webui-examples", cat -> cat.label("WebUI Examples")
           .addPage("Greetings", page -> page
               .mount("/config/webui/greetings", "GreetingsPage", JS_MODULE)
               .addAssociatedResourceType(GreetingConfig.RESOURCE_TYPE)));
   ```

These files are illustrative and mirror the platform's own pages; verify the exact prop names
against the version of `ignition-gateway-lib` you target.
