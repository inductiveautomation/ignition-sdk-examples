package com.inductiveautomation.ignition.examples.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.gateway.gan.GatewayAreaNetworkManager;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.metro.api.ServerId;
import org.apache.wicket.Application;
import org.apache.wicket.extensions.wizard.WizardStep;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

/**
 * Created by mattgross on 9/21/2016. Wizard page that allows remote Gateways to be selected.
 */
public class RemoteServerSelectionStep extends WizardStep {

    private static final String TITLE = "remotelogging.tasks.getwrapperlog.wizard.Title";
    private List<SimpleRemoteGateway> gateways;

    public RemoteServerSelectionStep(IModel<RemoteGatewaySelection> settings){
        super(BundleUtil.get().getString(TITLE), null, settings);

        // A form is required when using elements like checkboxes
        Form form = new Form("form");
        add(form);

        // The gateways list is serialized when the Previous button is clicked in the wizard. This functionality
        // allows the checkbox selections to be preserved when returning to this page in the wizard.
        if(gateways == null){
            gateways = loadRemoteGateways(settings.getObject().getSelectedGateways());
        }

        // A list of remote Gateways, the status of each and a checkbox for each.
        ListView<SimpleRemoteGateway> list = new ListView<SimpleRemoteGateway>("list", gateways) {
            @Override
            protected void populateItem(ListItem<SimpleRemoteGateway> item) {
                SimpleRemoteGateway rg = item.getModelObject();

                CheckBox checkBox = new CheckBox("serverChk", new PropertyModel<>(rg, "isSelected"));
                item.add(checkBox);

                Model<String> lblModel = new Model<>(String.format("%s (%s)", rg.getName(), rg.getStatus()));
                Label serverLbl = new Label("server", lblModel);
                item.add(serverLbl);
            }
        };

        list.setReuseItems(true);   // Needed to preserve checkbox selections when moving through the wizard.
        form.add(list);
    }

    /**
     * This gets called when the user clicks on the Next or Finish button. The list of selected Gateways is written
     * to the task's persistent record as a comma-delimited list.
     */
    @Override
    public void applyState() {

        List<String> selectedGateways = new ArrayList<>();

        for(SimpleRemoteGateway rg: gateways){
            if(rg.isSelected()){
                selectedGateways.add(rg.getName());
            }
        }

        RemoteGatewaySelection settings = (RemoteGatewaySelection) this.getDefaultModel().getObject();
        settings.setSelectedGateways(selectedGateways);
    }

    /**
     * Convenience method to load this page's Gateway selection list. If this task was previously saved and is currently
     * being edited, the checkboxes for previously selected Gateways will be checked (if the previously selected Gateway
     * still exists).
     * @param initialSelectedGateways
     * @return
     */
    private List<SimpleRemoteGateway> loadRemoteGateways(List<String> initialSelectedGateways){
        List<SimpleRemoteGateway> gateways = new ArrayList<>();

        // Determine all known remote Gateways
        GatewayContext context = (GatewayContext) Application.get();
        GatewayAreaNetworkManager gn = context.getGatewayAreaNetworkManager();
        List<ServerId> servers = gn.getKnownServers();
        for(ServerId server: servers){
            SimpleRemoteGateway sg = new SimpleRemoteGateway(server.getServerName(), gn.getServer(server).getState().toString());
            if(initialSelectedGateways.contains(server.getServerName())){
                // This Gateway's checkbox will be checked when the wizard page loads.
                sg.setSelected(true);
            }
            gateways.add(sg);
        }

        Collections.sort(gateways, new Comparator<SimpleRemoteGateway>() {
            @Override
            public int compare(SimpleRemoteGateway obj1, SimpleRemoteGateway obj2) {
                return obj1.getName().toLowerCase().compareTo(obj2.getName().toLowerCase());
            }
        });

        return gateways;
    }

}
