package com.inductiveautomation.reporting.examples.datasource.designer.ui;

import javax.swing.JTextField;
import java.io.Serializable;

import com.google.common.base.Optional;
import com.inductiveautomation.ignition.client.util.gui.HeaderLabel;
import com.inductiveautomation.ignition.client.util.gui.ValidatedTextField;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import com.inductiveautomation.reporting.common.resource.ReportResource;
import com.inductiveautomation.reporting.designer.api.AbstractDataSourceConfigFactory;
import com.inductiveautomation.reporting.designer.api.DataSourceConfigFactory;
import com.inductiveautomation.reporting.designer.api.DataSourceConfigPanel;
import com.inductiveautomation.reporting.examples.datasource.common.RestJsonDataObject;
import com.inductiveautomation.rm.base.RMKey;
import com.jidesoft.editor.CodeEditor;
import com.jidesoft.editor.DefaultSettings;
import net.miginfocom.swing.MigLayout;

import static com.inductiveautomation.ignition.common.BundleUtil.i18n;

/**
 * This is an extended JPanel and can be configured with any UI necessary for your data source.  In this case, the
 * RestJsonDataConfigPanel offers a few simple fields for entering a key that gives validation feedback using
 * RMKey, as well as space for a url that (hopefully) points to a valid REST URL.
 *
 * @author Perry Arellano-Jones
 */
public class RestJsonDataConfigPanel extends DataSourceConfigPanel {
    private final JTextField dataKeyTextField;
    private final CodeEditor urlField;

    /**
     * This Factory will get registered in the DesignerHook by calling
     * <pre>
     *     DesignerDataSourceRegistry.get(
     * </pre>
     */
    public static final DataSourceConfigFactory FACTORY =
            new AbstractDataSourceConfigFactory(RestJsonDataObject.ID, "datasource.DataSource.Type" ) {
                @Override
                public Serializable newConfigObject() {
                    return new RestJsonDataObject("NasaMeteorData", "https://data.nasa.gov/resource/mc52-syum.json");
                }

                @Override
                public DataSourceConfigPanel createConfigPanel(DesignerContext designerContext, ReportResource reportResource, Serializable dataObject) {
                    RestJsonDataObject obj = (RestJsonDataObject) dataObject;
                    return new RestJsonDataConfigPanel(obj);
                }

                @Override
                public Optional getDataKeyForConfigObject(Serializable dataObject) {
                    RestJsonDataObject restJsonDataObject = (RestJsonDataObject) dataObject;
                    return Optional.fromNullable(restJsonDataObject.getKey());
                }
            };


    public RestJsonDataConfigPanel(RestJsonDataObject obj){
        super(new MigLayout("fill"));

        urlField = new CodeEditor(DefaultSettings.getDefaults());
        urlField.setLineNumberVisible(false);
        urlField.setText(obj.getUrl());
        /* field that will hold our data key */
        dataKeyTextField = new JTextField(16);
        dataKeyTextField.setText(obj.getKey());

        ValidatedTextField dataKeyValidatedField = new ValidatedTextField(dataKeyTextField) {
            @Override
            protected String validate(String s) {
                if (RMKey.isKey(dataKeyTextField.getText())) {
                    return null;
                } else {
                    return i18n("datasource.Data.InvalidKey");
                }

            }
        };

        /* create some Header Labels for our fields using properties registered via BundleUtils */
        HeaderLabel dataKeyLabel = HeaderLabel.forKey("datasource.Data.DataKey");
        HeaderLabel urlLabel = HeaderLabel.forKey("datasource.Data.Url");

        add(dataKeyLabel, "wrap r");
        add(dataKeyValidatedField, "gapleft 2lp, wrap u");
        add(urlLabel, "wrap r");
        add(urlField, "gapleft 2lp, push, grow");

    }


    @Override
    public Serializable getConfigObject() {
        return new RestJsonDataObject(dataKeyTextField.getText(), urlField.getText());
    }
}
