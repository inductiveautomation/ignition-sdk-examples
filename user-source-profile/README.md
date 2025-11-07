# UserSourceProfile

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
