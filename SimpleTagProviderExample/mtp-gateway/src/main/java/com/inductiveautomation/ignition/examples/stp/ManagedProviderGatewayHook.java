package com.inductiveautomation.ignition.examples.stp;

import java.util.Random;

import com.inductiveautomation.ignition.common.TypeUtilities;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.model.values.QualityCode;
import com.inductiveautomation.ignition.common.sqltags.model.TagProviderMeta;
import com.inductiveautomation.ignition.common.sqltags.model.types.DataType;
import com.inductiveautomation.ignition.common.tags.model.TagPath;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.tags.managed.ManagedTagProvider;
import com.inductiveautomation.ignition.gateway.tags.managed.ProviderConfiguration;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * The "gateway hook" is the entry point for a module on the gateway. Since this example is so simple, we just do
 * everything here.
 * <p/>
 * This example uses the {@link ManagedTagProvider} to expose tags. We create a number of tags under a
 * folder, and update their values every second with random values.
 * <p/>
 * There is a "control" tag that can be used to modify the number of tags provided. This tag illustrates how to set up
 * write handling.
 */
public class SimpleProviderGatewayHook extends AbstractGatewayModuleHook {
    private static final String TASK_NAME = "UpdateSampleValues";
    //This pattern will be used for tag names. So, tags will be created under the "Custom Tags" folder.
    private static final String TAG_NAME_PATTERN = "Custom Tags/Tag %d";
    //This is the name of our "control" tag. It will be in the root folder.
    private static final String CONTROL_TAG = "Tag Count";
    private Logger logger;
    private GatewayContext context;
    private ManagedTagProvider ourProvider;
    //This example adds/removes tags, so we'll track how many we currently have.
    private int currentTagCount = 0;

    public SimpleProviderGatewayHook() {
        logger = LogManager.getLogger(this.getClass());
    }

    @Override
    public void setup(GatewayContext context) {
        try {
            this.context = context;
            ProviderConfiguration configuration = new ProviderConfiguration("Example");

            // Needed to allow tag configuration to be editable. Comment this out to disable tag configuration editing.
            configuration.setAllowTagCustomization(true);
            configuration.setPersistTags(false);
            configuration.setPersistValues(false);
            configuration.setMetaFlag(TagProviderMeta.FLAG_HAS_OPCBROWSE, false);

            ourProvider = context.getTagManager().getOrCreateManagedProvider(configuration);
            //Set up the control tag.
            //1) Create the tag, and set its type.
            //2) Register the write handler, so the tag can be modified.
            ourProvider.configureTag(CONTROL_TAG, DataType.Int4);
            ourProvider.registerWriteHandler(CONTROL_TAG, (TagPath target, Object value) -> {
                Integer intVal = TypeUtilities.toInteger(value);
                //The adjustTags function will add/remove tags, AND update the current value of the control tag.
                adjustTags(intVal);
                return QualityCode.Good;
            });

            //Now set up our first batch of tags.
            adjustTags(10);
        } catch (Exception e) {
            logger.fatal("Error setting up SimpleTagProvider example module.", e);
        }
    }

    @Override
    public void startup(LicenseState activationState) {
        try {
            //Register a task with the execution system to update values every second.
            context.getExecutionManager().register(getClass().getName(), TASK_NAME, this::updateValues, 1000);

            logger.info("Example Provider module started.");
        } catch (Exception e) {
            logger.fatal("Error starting up ManagedTagProvider example module.", e);
        }

    }

    @Override
    public void shutdown() {
        //Clean up the things we've registered with the platform, namely, our provider type.
        try {
            if (context != null) {
                //Remove our value update task
                context.getExecutionManager().unRegister(getClass().getName(), TASK_NAME);
                //Shutdown our provider (and delete all data)
                ourProvider.shutdown(true);
            }
        } catch (Exception e) {
            logger.error("Error stopping SimpleTagProvider example module.", e);
        }
        logger.info("SimpleTagProvider Example module stopped.");
    }

    /**
     * This function adds or removes tags to/from our custom provider. Notice that it is synchronized, since we are
     * updating the values asynchronously. If we weren't careful to synchronize the threading, it might happen that
     * right as we remove tags, they're added again implicitly, because the value update is happening at the same time.
     *
     * @param newCount
     */
    private synchronized void adjustTags(int newCount) {
        if (newCount > currentTagCount) {
            for (int i = currentTagCount; i < newCount; i++) {
                ourProvider.configureTag(String.format(TAG_NAME_PATTERN, i), DataType.Float8);
            }
        } else if (newCount < currentTagCount) {
            for (int i = currentTagCount; i > newCount; i--) {
                ourProvider.removeTag(String.format(TAG_NAME_PATTERN, i));
            }
        }
        //Update current count.
        currentTagCount = newCount;
        //Make sure to update the control tag with the current value.
        ourProvider.updateValue(CONTROL_TAG, currentTagCount, QualityCode.Good);
    }

    /**
     * Update the values of the tags.
     */
    private synchronized void updateValues() {
        Random r = new Random();
        for (int i = 0; i < currentTagCount; i++) {
            ourProvider.updateValue(String.format(TAG_NAME_PATTERN, i), r.nextFloat(), QualityCode.Good);
        }
    }
}
