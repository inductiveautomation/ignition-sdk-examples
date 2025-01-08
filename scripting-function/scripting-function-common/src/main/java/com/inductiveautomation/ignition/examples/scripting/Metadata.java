package com.inductiveautomation.ignition.examples.scripting;

/**
 * This simple record in common will act as our data exchange format between the client/designer and gateway
 * Because it's a simple record containing "primitives" (just strings) it's natively GSON-serializable implicitly
 * by ProtoRpcSerializer, but if you have more exotic types to send you'll need custom serialization behavior
 *
 * @see RpcFunctions#SERIALIZER
 */
public record Metadata(
        String osName,
        String osArch,
        String osVersion
) {
}
