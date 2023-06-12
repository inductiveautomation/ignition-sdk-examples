# Project Resource - Common

As a general pattern, your actual definition of a resource class should ideally be stored in a common scope, so that it
can be serialized and deserialized on the gateway and in the designer. You should also define a `ResourceType` that can
be reused in dependent scopes; the `ResourceType` is a tuple of your module's ID and a unique ID for the actual resource
type. 

