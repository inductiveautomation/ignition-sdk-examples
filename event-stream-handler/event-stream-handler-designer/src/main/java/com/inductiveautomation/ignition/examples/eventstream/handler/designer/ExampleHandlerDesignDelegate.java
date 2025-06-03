package com.inductiveautomation.ignition.examples.eventstream.handler.designer;

import com.inductiveautomation.eventstream.designer.api.EventStreamContext;
import com.inductiveautomation.eventstream.designer.api.handler.EventStreamHandlerDesignDelegate;
import com.inductiveautomation.eventstream.designer.api.handler.HandlerEditor;
import com.inductiveautomation.ignition.examples.eventstream.handler.ExampleHandlerModule;

public class ExampleHandlerDesignDelegate implements EventStreamHandlerDesignDelegate {

    @Override
    public HandlerEditor getEditor(EventStreamContext context) {
        return new ExampleHandlerEditor();
    }

    @Override
    public String getType() {
        return ExampleHandlerModule.MODULE_ID;
    }
}
