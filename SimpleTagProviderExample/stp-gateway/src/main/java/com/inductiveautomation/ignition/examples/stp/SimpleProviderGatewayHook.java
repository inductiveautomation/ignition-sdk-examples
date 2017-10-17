/**
 * Created by pjones on 12/8/14.
 */
package com.inductiveautomation.ignition.examples.stp;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Random;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.inductiveautomation.ignition.common.TypeUtilities;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.model.values.CommonQualities;
import com.inductiveautomation.ignition.common.model.values.Quality;
import com.inductiveautomation.ignition.common.sqltags.model.TagPath;
import com.inductiveautomation.ignition.common.sqltags.model.types.DataQuality;
import com.inductiveautomation.ignition.common.sqltags.model.types.DataType;
import com.inductiveautomation.ignition.common.sqltags.model.types.ExtendedTagType;
import com.inductiveautomation.ignition.common.sqltags.model.types.TagEditingFlags;
import com.inductiveautomation.ignition.common.sqltags.model.types.TagType;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.sqltags.simple.ProviderConfiguration;
import com.inductiveautomation.ignition.gateway.sqltags.simple.SimpleTagProvider;
import com.inductiveautomation.ignition.gateway.sqltags.simple.WriteHandler;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * The "gateway hook" is the entry point for a module on the gateway. Since this example is so simple, we just do
 * everything here.
 * <p/>
 * This example uses the {@link SimpleTagProvider} to expose tags through SQLTags. We create a number of tags under a
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
    Logger logger;
    GatewayContext context;
    SimpleTagProvider ourProvider;
    ExtendedTagType ourTagType;
    //This example adds/removes tags, so we'll track how many we currently have.
    int currentTagCount = 0;

    public SimpleProviderGatewayHook() {
        logger = LogManager.getLogger(this.getClass());
    }

    @Override
    public void setup(GatewayContext context) {
        try {
            this.context = context;
            ourProvider = new SimpleTagProvider("DynamicTags");

            //Set up our tag type. By doing this, we can allow our tags to use alerting, history, etc.
            //The STANDARD_STATUS flag set in TagEditingFlags provides for all features, without allowing tags to
            //be renamed.
            ourTagType = TagType.Custom;
            ourProvider.configureTagType(ourTagType, TagEditingFlags.STANDARD_STATUS, null);

            // Needed to allow tag configuration to be editable. Comment this out to disable tag configuration editing.
            ProviderConfiguration config = new ProviderConfiguration().setAllowTagCustomization(true);
            ourProvider.configureProvider(config);

            //Set up the control tag.
            //1) Register the tag, and configure its type.
            //2) Register the write handler, so the tag can be modified.
            ourProvider.configureTag(CONTROL_TAG, DataType.Int4, ourTagType);
            ourProvider.registerWriteHandler(CONTROL_TAG, new WriteHandler() {
                @Override
                public Quality write(TagPath target, Object value) {
                    Integer intVal = TypeUtilities.toInteger(value);
                    //The adjustTags function will add/remove tags, AND update the current value of the control tag.
                    adjustTags(intVal);
                    return CommonQualities.GOOD;
                }
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
            ourProvider.startup(context);

            //Register a task with the execution system to update values every second.
            context.getExecutionManager().register(getClass().getName(), TASK_NAME, new Runnable() {
                @Override
                public void run() {
                    updateValues();
                }
            }, 1000);

            logger.info("Example Provider module started.");
        } catch (Exception e) {
            logger.fatal("Error starting up SimpleTagProvider example module.", e);
        }

        pahoTest();
    }

    private static void pahoTest() {
        String topic = "MQTT Examples";
        String content = "Message from MqttPublishSample";
        int qos = 2;
        String broker = "ssl://test.mosquitto.org:8883";
        String clientId = "JavaSample";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();

            connOpts.setCleanSession(true);
            connOpts.setConnectionTimeout(5000);
            try {
                connOpts.setSocketFactory(createSocketFactory());
            } catch (Exception e) {
                e.printStackTrace();
            }


            System.out.println("Connecting to broker: " + broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");
            System.out.println("Publishing message: " + content);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
            sampleClient.publish(topic, message);
            System.out.println("Message published");
            sampleClient.disconnect();
            System.out.println("Disconnected");
        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }

    private static SSLSocketFactory createSocketFactory() throws Exception {
        TrustManager[] naiveTrustManager = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, naiveTrustManager, new SecureRandom());

        return sc.getSocketFactory();
    }

    @Override
    public void shutdown() {
        //Clean up the things we've registered with the platform, namely, our provider type.
        try {
            if (context != null) {
                //Remove our value update task
                context.getExecutionManager().unRegister(getClass().getName(), TASK_NAME);
                //Shutdown our provider
                ourProvider.shutdown();
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
    protected synchronized void adjustTags(int newCount) {
        if (newCount > currentTagCount) {
            for (int i = currentTagCount; i < newCount; i++) {
                ourProvider.configureTag(String.format(TAG_NAME_PATTERN, i), DataType.Float8, ourTagType);
            }
        } else if (newCount < currentTagCount) {
            for (int i = currentTagCount; i > newCount; i--) {
                ourProvider.removeTag(String.format(TAG_NAME_PATTERN, i));
            }
        }
        //Update current count.
        currentTagCount = newCount;
        //Make sure to update the control tag with the current value.
        ourProvider.updateValue(CONTROL_TAG, currentTagCount, DataQuality.GOOD_DATA);
    }

    /**
     * Update the values of the tags.
     */
    protected synchronized void updateValues() {
        Random r = new Random();
        for (int i = 0; i < currentTagCount; i++) {
            ourProvider.updateValue(String.format(TAG_NAME_PATTERN, i), r.nextFloat(), DataQuality.GOOD_DATA);
        }
    }
}
