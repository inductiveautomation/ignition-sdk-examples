package com.inductiveautomation.ignition.examples.task;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import com.inductiveautomation.ignition.examples.GatewayHook;
import com.inductiveautomation.ignition.gateway.localdb.persistence.PersistentRecord;
import com.inductiveautomation.ignition.gateway.localdb.persistence.RecordMeta;
import com.inductiveautomation.ignition.gateway.localdb.persistence.ReferenceField;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.tasks.AbstractTaskType;
import com.inductiveautomation.ignition.gateway.tasks.GatewayTaskRecord;
import com.inductiveautomation.ignition.gateway.tasks.Task;
import org.apache.wicket.extensions.wizard.WizardModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

/**
 * Created by mattgross on 9/15/2016.
 */
public class GetWrapperLogTaskType extends AbstractTaskType {

    public static final String BUNDLE_KEY = "remotelogging.tasks";
    public static final String TYPE_ID = "getwrapperlog";

    private GatewayContext context;


    public GetWrapperLogTaskType(GatewayContext context) {
       super(GatewayHook.TASK_OWNERID, TYPE_ID, BUNDLE_KEY, false, false);
        this.context = context;
    }

    /**
     * A private task type is one that cannot be created from the general task management system. It is either created
     * programmatically, or from a system specific management screen. We are creating a task that can be handled by
     * the general system, so we return false here.
     **/
    @Override
    public boolean isPrivate() {
        return false;
    }

    @Override
    public Task createInstance(GatewayContext context, GatewayTaskRecord gatewayRecord) {
        return new GetWrapperLogTask(UUID.randomUUID(), findProfileSettingsRecord(context, gatewayRecord));
    }

    /**
     * Allows new wizard pages to be added that can be used to configure the task settings. The first page added to
     * the WizardModel object will be displayed as the third page in the wizard when creating a new task (the first page will be
     * the task type selection apge, and the second page will be the scheduling page).
     * dataModel is actually: IModel<List<PersistentRecord>>, entry 0=GatewayTaskRecord, 1=Task specific settings
     *
     * @param model
     * @param dataModel
     **/
    @Override
    public void buildWizardModel(WizardModel model, IModel<? extends List<? extends PersistentRecord>> dataModel) {

        // Use a LoadableDetachableModel here to reload the data on demand, which prevents serialization errors.
        IModel<RemoteGatewaySelection> stepModel = new LoadableDetachableModel<RemoteGatewaySelection>() {
            @Override
            protected RemoteGatewaySelection load() {
                return (RemoteGatewaySelection) dataModel.getObject().get(1);
            }
        };

        model.add(new RemoteServerSelectionStep(stepModel, context));
    }

    /**
     * Returns the record meta of the settings record for this type of task.
     **/
    @Override
    public RecordMeta<? extends PersistentRecord> getSettingsRecordType() {
        return GetWrapperLogTaskSettingsRecord.META;
    }

    /**
     * If a settings record type is defined, return the foreign key field that references the main record type
     */
    @Override
    public ReferenceField<GatewayTaskRecord> getSettingsRecordForeignKey() {
        return GetWrapperLogTaskSettingsRecord.Profile;
    }

}
