package com.inductiveautomation.ignition.examples.eventstream.handler.designer;

import com.inductiveautomation.eventstream.EventStreamModule;
import com.inductiveautomation.eventstream.designer.api.EventStreamContext;
import com.inductiveautomation.eventstream.designer.api.handler.EventStreamHandlerDesignDelegate;
import com.inductiveautomation.eventstream.designer.api.handler.HandlerEditor;

public class ExampleHandlerDesignDelegate implements EventStreamHandlerDesignDelegate {

    @Override
    public HandlerEditor getEditor(EventStreamContext context) {
        return new ExampleHandlerEditor();
    }

    @Override
    public String getType() {
        return EventStreamModule.MODULE_ID;
    }

    @Override
    public String getName() {
        return "Example Handler";
    }

    @Override
    public String getDescription() {
        return "Writes payload to a specified path";
    }

}
