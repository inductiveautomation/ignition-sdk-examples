package com.inductiveautomation.ignition.examples.eventstream.handler.designer;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.inductiveautomation.eventstream.designer.api.EventStreamContext;
import com.inductiveautomation.eventstream.designer.api.handler.HandlerEditor;
import com.inductiveautomation.ignition.common.gson.JsonObject;
import com.inductiveautomation.ignition.examples.eventstream.handler.ExampleHandlerConfig;
import net.miginfocom.swing.MigLayout;

/**
 * The editor contains 2 {@link JTextField} components and a {@link JCheckBox} component. The text fields are used to
 * input the {@code file path} and {@code test file path}. <br>
 * <br>
 * If the {@code useTestFilePathCheckBox} is checked, the {@code testTextField} is enabled, and
 * the payload will be written to the {@code test file path}, otherwise the payload will be written to the
 * logger. <br>
 * <br>
 * The {@link #initialize(EventStreamContext, JsonObject)} method is called in the following scenarios:
 * <ul>
 *     <li>When the editor is first initialized</li>
 *     <li>Anytime that the event stream is saved</li>
 * </ul>
 * The {@link #getConfig()} method is called when the config is needed, such as when the event stream is saved.
 */
public class ExampleHandlerEditor extends HandlerEditor {

    private final JTextField textField = new JTextField();
    private final JTextField testTextField = new JTextField();
    private final JCheckBox useTestFilePathCheckBox = new JCheckBox("Use Test Filepath");

    public ExampleHandlerEditor() {
        super();
        setLayout(new MigLayout(
            "ins 0, fillx, gapy 4, wrap 1",
            "[fill, grow]", "")
        );
        add(new JLabel("File Path"));
        add(textField, "width 20:400:400, wrap 16");
        add(useTestFilePathCheckBox);
        add(testTextField, "width 20:400:400");

        installListeners();
    }


    /**
     * This method is executed on the Event Dispatcher Thread (EDT).
     */
    @Override
    public void initialize(EventStreamContext context, JsonObject json) {
        ExampleHandlerConfig config = ExampleHandlerConfig.fromJson(json);
        textField.setText(config.filePath());
        testTextField.setText(config.testFilePath());
        useTestFilePathCheckBox.setSelected(config.useTestFilePath());
    }

    /**
     * This method is executed on the Event Dispatcher Thread (EDT).
     */
    @Override
    public JsonObject getConfig() {
        return new ExampleHandlerConfig(
            textField.getText(),
            testTextField.getText(),
            useTestFilePathCheckBox.isSelected()
        ).toJson();
    }


    // -------------------------------------------------------------------------------------------------------------
    // This part of the code illustrates how enabled/disabled state should be handled. When handlers are
    // enabled/disabled, child components of the editors are also enabled/disabled. If there are components
    // for which there is logic that affects the enabled/disabled state, the logic needs to be re-evaluated when
    // the {@link #setEnabled(boolean)} method is called.
    //
    // In this example, the testTextField is only enabled when the useTestFilePathCheckBox is checked. We override the
    // {@link #setEnabled(boolean)} method to re-evaluate the enabled/disabled logic.
    //
    // See what happens when you comment out the setEnabled method below and perform the following steps:
    // 1. Uncheck the "Use Test Filepath" checkbox (Note that the testTextField is disabled)
    // 2. Disabled the handler
    // 3. Re-enable the handler (Not that the testTextField is enabled, when it should be disabled)
    // -------------------------------------------------------------------------------------------------------------
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        updateComponentState();
    }

    private void installListeners() {
        useTestFilePathCheckBox.addActionListener(e -> updateComponentState());
    }

    /**
     * The logic that effects the enabled/disabled state of the components is contained in this method.
     */
    private void updateComponentState() {
        testTextField.setEnabled(useTestFilePathCheckBox.isSelected());
    }

}

