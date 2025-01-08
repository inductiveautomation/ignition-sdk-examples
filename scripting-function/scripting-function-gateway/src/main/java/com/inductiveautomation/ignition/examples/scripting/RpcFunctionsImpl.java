package com.inductiveautomation.ignition.examples.scripting;

import com.inductiveautomation.ignition.common.project.ClientPermissionsConstants;
import com.inductiveautomation.ignition.gateway.rpc.RpcDelegate;

import java.util.function.Supplier;

/**
 * This is the actual implementation of the RPC functions that will be called by the client/designer.
 * The @RunsOnClient annotation is needed for <b>any</b> RPC function that will be allowed to be invoked by Vision
 * clients.
 * If you do not have a custom client permission ID registered with the rest of the system, use the special UNRESTRICTED
 * value, as below.
 */
@RpcDelegate.RunsOnClient(clientPermissionId = ClientPermissionsConstants.UNRESTRICTED)
public class RpcFunctionsImpl implements RpcFunctions {
    private final Supplier<Metadata> metadataSupplier;

    public RpcFunctionsImpl(Supplier<Metadata> metadataSupplier) {
        this.metadataSupplier = metadataSupplier;
    }

    @Override
    public Metadata getGatewayMetadata() {
        return metadataSupplier.get();
    }
}
