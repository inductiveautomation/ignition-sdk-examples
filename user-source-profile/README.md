# User Source Profile

This module provides an implementation of the `UserSourceProvider` interface, which allows for the management of user 
profiles in a system. It includes methods for creating, updating, and deleting user profiles, as well as retrieving user
information and authenticating users. It is backed by a MongoDB backend.

In a production environment, you may want to use the MongoDB Connector module, but for simplicity, this module uses the
MongoDB Java driver directly. This allows for easy testing without the need to add another module to Ignition.

This implementation is set up for easy testing, and the default configuration should connect to a local, unsecured
MongoDB. To start one in a Docker container, run:

```bash
docker run -d -p 27017:27017 --rm --name insecure-mongo mongo
```

Should you wish to start a MongoDB instance requiring authentication, you can use the following command. Remember
to replace `admin` and `secret` with your desired username and password and configure your user source to use these 
credentials.

```bash
docker run -d -p 27017:27017 --rm --name secure-mongo \
  -e MONGO_INITDB_ROOT_USERNAME=admin \
  -e MONGO_INITDB_ROOT_PASSWORD=secret \
  mongo
```

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
