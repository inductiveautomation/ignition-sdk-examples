package com.inductiveautomation.ignition.examples.eventstream.source.designer;

import javax.swing.JLabel;
import javax.swing.JTextField;

import com.inductiveautomation.eventstream.designer.api.EventStreamContext;
import com.inductiveautomation.eventstream.designer.api.source.SourceEditor;
import com.inductiveautomation.ignition.common.gson.JsonObject;
import com.inductiveautomation.ignition.examples.eventstream.source.ExampleSourceConfig;
import net.miginfocom.swing.MigLayout;

public class ExampleSourceEditor extends SourceEditor {

    private final JTextField textField = new JTextField();

    public ExampleSourceEditor() {
        super();
        setLayout(new MigLayout(
            "ins 0, fillx, gapy 4, wrap 1",
            "[fill, grow]", "")
        );
        add(new JLabel("Items to Stream"));
        add(textField, "width 20:400:400, wrap 16");
    }

    /**
     * This method is executed on the Event Dispatcher Thread (EDT).
     */
    @Override
    public void initialize(EventStreamContext context, JsonObject json) {
        ExampleSourceConfig config = ExampleSourceConfig.fromJson(json);
        textField.setText(config.textToStream());
    }

    /**
     * This method is executed on the Event Dispatcher Thread (EDT).
     */
    @Override
    public JsonObject getConfig() {
        return new ExampleSourceConfig(textField.getText()).toJson();
    }
}

