package com.inductiveautomation.ignition.examples.eventstream.source.designer;

import com.inductiveautomation.eventstream.designer.api.EventStreamContext;
import com.inductiveautomation.eventstream.designer.api.source.EventStreamSourceDesignDelegate;
import com.inductiveautomation.eventstream.designer.api.source.SourceEditor;
import com.inductiveautomation.ignition.examples.eventstream.source.ExampleSourceModule;

public class ExampleSourceDesignDelegate implements EventStreamSourceDesignDelegate {

    @Override
    public SourceEditor getEditor(EventStreamContext context) {
        return new ExampleSourceEditor();
    }

    @Override
    public String getType() {
        return ExampleSourceModule.MODULE_ID;
    }

}
