# WebUI Examples

This module demonstrates how an Ignition 8.3 module contributes **configuration resources** and
the **gateway web UI** that manages them. It covers the three canonical shapes a configuration
resource can take:

| Example | Shape | What it shows |
| --- | --- | --- |
| **Greeting** | **Named** resource | Many instances, each with a unique name (like database connections). List + create + edit + delete. |
| **Greeting Settings** | **Singleton** resource | Exactly one instance for the whole gateway (like "Gateway Settings"). |
| **Greeting Provider** | **Extension point** | A category with multiple module-provided *types* (`static`, `timeBased`), each with its own settings and a schema-driven editor. |

It also serves a small React **landing page** (added under the gateway **Home** section as *WebUI
Examples*) that reads all three resource types back through the REST API.

> **The substance of this example is on the Java side.** Registering a resource type is what makes
> the gateway generate its REST routes and render a schema-driven editor — no front-end code is
> required for editing. See [A note on the web UI](#a-note-on-the-web-ui) below for why the shipped
> React page is intentionally thin.

## The three resource examples

Every configuration resource is described to the platform by a
`ResourceTypeMeta`, which is registered with the `ResourceTypeMetaRegistry` at gateway startup (see
[`WebuiWebpageGatewayHook`](gateway/src/main/java/org/webui/test/gateway/WebuiWebpageGatewayHook.java)).
When a meta provides a *route delegate*, the platform automatically mounts CRUD REST routes for the
resource and exposes a JSON schema (built from `x-form` annotations on the config record) that the
gateway web UI turns into an editor form.

### 1. Named resource — Greeting

* Config record: [`GreetingConfig`](gateway/src/main/java/org/webui/test/gateway/named/GreetingConfig.java)
* Built with `ResourceTypeMeta.newBuilder(GreetingConfig.class)`.
* At runtime a `NamedResourceHandler` tracks create/update/delete of the many named instances.

### 2. Singleton resource — Greeting Settings

* Config record: [`GreetingSettings`](gateway/src/main/java/org/webui/test/gateway/singleton/GreetingSettings.java)
* Identical to a named resource except the meta is built with `.singleton()`.
* A `SingletonResourceHandler` seeds the default values on first startup, then tracks changes. Because
  of this the singleton always exists — the landing page can read it immediately.

### 3. Extension point — Greeting Provider

* Profile config (shared by all types): [`GreetingProviderConfig`](gateway/src/main/java/org/webui/test/gateway/extensionpoint/GreetingProviderConfig.java)
* Category base class: [`GreetingProviderExtensionPoint`](gateway/src/main/java/org/webui/test/gateway/extensionpoint/GreetingProviderExtensionPoint.java)
* Concrete types: [`StaticGreetingProviderExtensionPoint`](gateway/src/main/java/org/webui/test/gateway/extensionpoint/StaticGreetingProviderExtensionPoint.java) and [`TimeBasedGreetingProviderExtensionPoint`](gateway/src/main/java/org/webui/test/gateway/extensionpoint/TimeBasedGreetingProviderExtensionPoint.java)
* Runtime object produced by each type: [`GreetingProvider`](gateway/src/main/java/org/webui/test/gateway/extensionpoint/GreetingProvider.java)

An extension point splits its configuration into a shared **profile** (defined by the category) and
type-specific **settings** (defined by each type). Key points demonstrated here:

* The meta is built with `ResourceTypeMeta.newExtensionPointBuilder(...)` and an
  `ExtensionPointCollection`. Because the collection is populated from every module hook's
  `getExtensionPoints()`, a *different* module could contribute additional greeting-provider types.
* The module exposes its two types from `WebuiWebpageGatewayHook.getExtensionPoints()`.
* Each type returns an `ExtensionPointResourceForm` from `getWebUiComponent(...)`. This is a
  **schema-driven** form descriptor rendered by the gateway itself — the module ships **no**
  front-end code for the editor. The gateway swaps the "settings" portion of the form based on the
  selected type.

## Trying it out (REST)

Once the module is installed, the platform serves CRUD routes for each resource type under
`/data/api/v1/resources/`. The `<resourceType>` path segment is `<module-id>/<type-id>`.

```bash
# List greetings (named)
curl -u admin:password http://localhost:8088/data/api/v1/resources/list/org.webui.test.WebuiWebpage/greeting

# Read the singleton settings
curl -u admin:password http://localhost:8088/data/api/v1/resources/singleton/org.webui.test.WebuiWebpage/greeting-settings

# Inspect the extension point (its available types + JSON schema)
curl -u admin:password http://localhost:8088/data/api/v1/resources/type/org.webui.test.WebuiWebpage/greeting-provider

# Create a greeting (named)
curl -u admin:password -X POST \
  http://localhost:8088/data/api/v1/resources/org.webui.test.WebuiWebpage/greeting \
  -H 'Content-Type: application/json' \
  -d '{"name":"welcome","config":{"message":"Hi there","tone":"FRIENDLY","repeatCount":1,"shout":false}}'
```

The landing page at `<gateway>/web/config` → **Home → WebUI Examples → Overview** (URL
`/webui-examples`) reflects whatever you create.

## A note on the web UI

The canonical way first-party modules build these pages is with the
`@inductiveautomation/ignition-gateway-lib` React library (`ResourceModeDataGrid`,
`ResourceModeSingletonPage`, `ExtensionPointDataGridPage`, and the schema-driven `GeneratedForm`).

**That package is not currently published to the SDK npm registry** (`ignition-web-ui` and
`ignition-icons` are). First-party modules resolve it from the Ignition monorepo, which an external
SDK consumer can't do. So this example takes a **Java-first** approach:

* All three resource types are fully implemented and functional in Java — REST routes, schemas, the
  extension-point form descriptors, and runtime handlers all work today.
* The shipped React page ([`web-ui/src/pages/WebUiExamples`](web-ui/src/pages/WebUiExamples/WebUiExamples.tsx))
  uses only the published `@inductiveautomation/ignition-web-ui` primitives and talks to the resource
  REST API directly. It is a read-only dashboard, kept deliberately thin.
* The [`web-ui/reference/`](web-ui/reference) folder contains the **fully-canonical** pages
  (`ResourceModeDataGrid` / `ResourceModeSingletonPage` / `ExtensionPointDataGridPage`) as
  documentation. They are excluded from the build; see [`web-ui/reference/README.md`](web-ui/reference/README.md)
  for how to enable them once `ignition-gateway-lib` is available.

## Building

Build with the [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html); it
downloads the appropriate versions of all tools (including Node/Yarn for the front end).

```
// on Windows
gradlew build

// on linux/macOS
./gradlew build
```

This produces a `.modl` file in the `build/` directory that can be installed on an 8.3+ gateway. See
the [Ignition Module Tool](https://github.com/inductiveautomation/ignition-module-tools) docs for the
tasks and configuration options provided by the module plugin.

### How the front-end bundle is served

1. `web-ui` compiles a single UMD JS bundle with webpack (entry name `webuiExamples` →
   `webuiExamples.js`). Named exports from `web-ui/src/index.ts` (here `WebUiExamples`) become the
   "component ids" the gateway mounts.
2. The bundle is packaged into the module and served at
   `/res/<mount-alias>/webuiExamples.js`, where the mount alias (`web-ui-test`) and mounted folder
   (`mounted`) are set by `getMountPathAlias()` / `getMountedResourceFolder()` in the gateway hook.
3. The hook registers a `SystemJsModule` for that URL and mounts a nav page with
   `.mount("/webui-examples", "WebUiExamples", jsModule)`.

## Project layout

```
webui-webpage/
├─ build.gradle.kts             # ignitionModule { } config
├─ common/                      # module id constant (shared)
├─ gateway/                     # Gateway scope — the three resource examples + the hook
│  └─ src/main/java/org/webui/test/gateway/
│     ├─ WebuiWebpageGatewayHook.java
│     ├─ named/GreetingConfig.java
│     ├─ singleton/GreetingSettings.java
│     └─ extensionpoint/…        # profile, base class, two types, settings, provider
└─ web-ui/                      # React front end
   ├─ src/                       # the thin, shipped landing page (uses ignition-web-ui only)
   └─ reference/                 # canonical ignition-gateway-lib pages (not compiled)
```
