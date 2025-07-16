# Managed Tag Provider

Sets up the scaffolding for a simple implementation of the 8.3+ `ManagedTagProvider` interface. In the `GatewayHook`,  `setup` creates a new provider, which is then used to expose some read-only tags, and a single writeable tag.