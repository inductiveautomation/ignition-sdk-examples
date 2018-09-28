package com.inductiveautomation.ignition.examples.tagdriver.configuration;

import org.eclipse.milo.opcua.sdk.server.api.DataItem;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;

public class BasicDataItem implements DataItem {
    @Override
    public void setValue(DataValue dataValue) {

    }

    @Override
    public void setQuality(StatusCode statusCode) {

    }

    @Override
    public double getSamplingInterval() {
        return 0;
    }

    @Override
    public UInteger getId() {
        return null;
    }

    @Override
    public UInteger getSubscriptionId() {
        return null;
    }

    @Override
    public ReadValueId getReadValueId() {
        return null;
    }

    @Override
    public TimestampsToReturn getTimestampsToReturn() {
        return null;
    }

    @Override
    public boolean isSamplingEnabled() {
        return false;
    }
}
