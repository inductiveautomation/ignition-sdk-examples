package com.inductiveautomation.ignition.examples.scripting;

import java.util.function.Supplier;

public class GatewayScriptModule extends AbstractScriptModule {
    private final Supplier<Metadata> metadataSupplier;

    public GatewayScriptModule(Supplier<Metadata> metadataSupplier) {
        this.metadataSupplier = metadataSupplier;
    }

    @Override
    protected Metadata getArchImpl() {
        return metadataSupplier.get();
    }
}
