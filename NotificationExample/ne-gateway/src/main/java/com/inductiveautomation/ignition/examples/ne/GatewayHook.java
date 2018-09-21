/* Filename: GatewayHook.java
 * Created by Perry Arellano-Jones on 12/11/14.
 * Copyright Inductive Automation 2014
 */
package com.inductiveautomation.ignition.examples.ne;

import com.inductiveautomation.ignition.examples.ne.profile.ConsoleNotificationProfileType;
import com.inductiveautomation.ignition.examples.ne.profile.ConsoleNotificationProfileSettings;
import org.apache.log4j.Logger;

import com.inductiveautomation.ignition.alarming.AlarmNotificationContext;
import com.inductiveautomation.ignition.alarming.common.ModuleMeta;
import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.services.ModuleServiceConsumer;
import com.inductiveautomation.ignition.examples.ne.profile.ConsoleLogProperties;

public class GatewayHook extends AbstractGatewayModuleHook implements ModuleServiceConsumer{

	
	public static final String MODULE_ID = "com.inductiveautomation.ignition.examples.ne";

	private final LoggerEx log = new LoggerEx(Logger.getLogger(getClass()));

	private volatile GatewayContext gatewayContext;
	private volatile AlarmNotificationContext notificationContext;
	
	
	@Override
	public void setup(GatewayContext context) {
		this.gatewayContext = context;
		
		//allows you to add keys to properties file 
		BundleUtil.get().addBundle("ConsoleNotification", getClass(), "ConsoleNotification");
		
		context.getModuleServicesManager().subscribe(AlarmNotificationContext.class, this);
		
		//register the contact type for the console type
		context.getUserSourceManager().registerContactType(ConsoleNotificationProfileType.CONSOLE);
		
		//the's AlarmProperty instances can be edited when editing in the alarming section of the 
		//tag edit config
		context.getAlarmManager().registerExtendedConfigProperties(ModuleMeta.MODULE_ID,
		ConsoleLogProperties.CUSTOM_MESSAGE);  
		
		//update the schema for the local db
		try{
			context.getSchemaUpdater().updatePersistentRecords(ConsoleNotificationProfileSettings.META);
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
						new ConsoleNotificationProfileType());
			} catch (Exception e) {
				log.error("Error removing notification profile.", e);
			}
		}

		BundleUtil.get().removeBundle("ConsoleNotification");
	}

	@Override
	public void startup(LicenseState licenseState) {

	}

	@Override
	public void serviceReady(Class<?> serviceClass) {
		if (serviceClass == AlarmNotificationContext.class) {
			//must set our notification context to the current running version of the AlarmNotifcationContext
			notificationContext = gatewayContext.getModuleServicesManager()
				.getService(AlarmNotificationContext.class);

			try {
				//here's where we added our new notification type
				notificationContext.getAlarmNotificationManager().addAlarmNotificationProfileType(
						new ConsoleNotificationProfileType());
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
