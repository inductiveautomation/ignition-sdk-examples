# Vision Component Example

Adds a simple component to the Vision module.

## Dev Mode Descriptor

The build binds the `ignition-maven-plugin`'s `write-dev-descriptor` goal to the `package` phase, next to the `modl` goal:

```xml
<goals>
    <goal>modl</goal>
    <goal>write-dev-descriptor</goal>
</goals>
```

Running `mvn package` then produces, in the build module's `target/` directory:

* the packaged `*.modl` you install from the Gateway's `Config > Modules` page, and
* a JSON dev descriptor at `target/dev/<moduleId>.json`.

The descriptor records the module metadata (id, name, version, hooks, dependencies) along with each subproject's compiled `target/classes` directory and resolved dependency JARs, letting a development Ignition Gateway load the module straight from the Maven build outputs instead of requiring a full `.modl` install. Because it points at `target/classes`, the module must be compiled first — the `package`-phase binding ensures that. It is generated automatically by the module build, so `mvn package` (re)produces it.
