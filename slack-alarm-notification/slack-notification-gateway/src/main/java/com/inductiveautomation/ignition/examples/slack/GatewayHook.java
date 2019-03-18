package com.inductiveautomation.ignition.examples.slack;

import com.inductiveautomation.ignition.alarming.AlarmNotificationContext;
import com.inductiveautomation.ignition.alarming.common.ModuleMeta;
import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.examples.slack.profile.SlackNotificationProfileSettings;
import com.inductiveautomation.ignition.examples.slack.profile.SlackNotificationProfileType;
import com.inductiveautomation.ignition.examples.slack.profile.SlackProperties;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.services.ModuleServiceConsumer;
import org.apache.log4j.Logger;

public class GatewayHook extends AbstractGatewayModuleHook implements ModuleServiceConsumer{
	public static final String MODULE_ID = "com.inductiveautomation.ignition.examples.slack-notification";

	private final LoggerEx log = new LoggerEx(Logger.getLogger(getClass()));

	private volatile GatewayContext gatewayContext;
	private volatile AlarmNotificationContext notificationContext;

	@Override
	public void setup(GatewayContext context) {
		this.gatewayContext = context;
		
		//allows you to add keys to properties file 
		BundleUtil.get().addBundle("SlackNotification", getClass(), "SlackNotification");
		
		context.getModuleServicesManager().subscribe(AlarmNotificationContext.class, this);

		// This allows local overrides in the alarming section of the tag edit config
		context.getAlarmManager()
			.registerExtendedConfigProperties(ModuleMeta.MODULE_ID, SlackProperties.CUSTOM_MESSAGE);

		context.getUserSourceManager().registerContactType(SlackNotificationProfileType.SLACK);

		//update the schema for the local db
		try{
			context.getSchemaUpdater().updatePersistentRecords(SlackNotificationProfileSettings.META);
		} catch (Exception e) {
			log.error("Error configuring internal database for console notification module.", e);
		}
	}

	@Override
	public void shutdown() {
		gatewayContext.getModuleServicesManager().unsubscribe(AlarmNotificationContext.class, this);

		if (notificationContext != null) {
			try {
				notificationContext.getAlarmNotificationManager().removeAlarmNotificationProfileType(
						new SlackNotificationProfileType());
			} catch (Exception e) {
				log.error("Error removing notification profile.", e);
			}
		}
		BundleUtil.get().removeBundle("SlackNotification");
	}

	@Override
	public void startup(LicenseState licenseState) {
        // no-op
	}

    @Override
	public void serviceReady(Class<?> serviceClass) {
		if (serviceClass == AlarmNotificationContext.class) {
			//must set our notification context to the current running version of the AlarmNotificationContext
			notificationContext = gatewayContext.getModuleServicesManager()
				.getService(AlarmNotificationContext.class);

			try {
				//here's where we added our new notification type
				notificationContext.getAlarmNotificationManager().addAlarmNotificationProfileType(
						new SlackNotificationProfileType());
			} catch (Exception e) {
				log.error("Error adding notification profile.", e);
			}
		}
	}

	@Override
	public void serviceShutdown(Class<?> arg0) {
		notificationContext = null;
	}

}
