# Event Stream Source Example
This example shows how to add a source to the Ignition Event Stream.  The source takes comma separated values 
and generates events from them. Values are sent every second and loops back to the beginning after the last value.

For instance, if the source is configured with the values `A,B,C`, the source will generate events with the 
values `A`, `B`, `C`, `A`, `B`, `C` sending `A` at time 0, `B` at time 1, `C` at time 2, `A` at time 3, and so on.




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
