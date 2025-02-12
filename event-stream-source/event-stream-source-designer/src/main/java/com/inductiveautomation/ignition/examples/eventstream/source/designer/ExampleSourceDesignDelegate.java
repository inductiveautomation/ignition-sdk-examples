package com.inductiveautomation.ignition.examples.eventstream.source.designer;

import com.inductiveautomation.eventstream.EventStreamModule;
import com.inductiveautomation.eventstream.designer.api.EventStreamContext;
import com.inductiveautomation.eventstream.designer.api.source.EventStreamSourceDesignDelegate;
import com.inductiveautomation.eventstream.designer.api.source.SourceEditor;

public class ExampleSourceDesignDelegate implements EventStreamSourceDesignDelegate {

    @Override
    public SourceEditor getEditor(EventStreamContext context) {
        return new ExampleSourceEditor();
    }

    @Override
    public String getType() {
        return EventStreamModule.MODULE_ID;
    }

    @Override
    public String getName() {
        return "Example Source";
    }

    @Override
    public String getDescription() {
        return "Given a comma delimited string, will stream each value on a 15 second interval.";
    }

}
