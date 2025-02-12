package com.inductiveautomation.ignition.examples.eventstream.source.designer;

import java.awt.BorderLayout;
import javax.swing.JTextField;

import com.inductiveautomation.eventstream.designer.api.EventStreamContext;
import com.inductiveautomation.eventstream.designer.api.source.SourceEditor;
import com.inductiveautomation.ignition.common.gson.JsonObject;
import com.inductiveautomation.ignition.examples.eventstream.source.ExampleSourceConfig;

public class ExampleSourceEditor extends SourceEditor {

    private final JTextField textField = new JTextField();

    public ExampleSourceEditor() {
        super();
        setLayout(new BorderLayout());
        add(textField, BorderLayout.NORTH);
    }

    @Override
    // IS THIS EDT?
    public void initialize(EventStreamContext context, JsonObject json) {
        ExampleSourceConfig config = ExampleSourceConfig.fromJson(json);
        textField.setText(config.textToStream());
    }

    @Override
    // IS THIS EDT?
    public JsonObject getConfig() {
        return new ExampleSourceConfig(textField.getText()).toJson();
    }
}

