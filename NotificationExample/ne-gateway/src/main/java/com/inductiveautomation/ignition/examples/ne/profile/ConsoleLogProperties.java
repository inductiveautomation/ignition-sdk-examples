package com.inductiveautomation.ignition.examples.ne.profile;

import java.util.ArrayList;
import java.util.List;

import com.inductiveautomation.ignition.alarming.common.notification.BasicNotificationProfileProperty;
import com.inductiveautomation.ignition.common.alarming.config.AlarmProperty;
import com.inductiveautomation.ignition.common.alarming.config.BasicAlarmProperty;
import com.inductiveautomation.ignition.common.config.ConfigurationProperty;
import com.inductiveautomation.ignition.common.i18n.LocalizedString;

import static com.inductiveautomation.ignition.common.BundleUtil.i18n;

public class ConsoleLogProperties {

	public static final BasicNotificationProfileProperty<String> MESSAGE = new BasicNotificationProfileProperty<>(
		"message",
		"ConsoleNotification." + "Properties.Message.DisplayName",
		null,
		String.class
	);

	public static final BasicNotificationProfileProperty<String> THROTTLED_MESSAGE =
		new BasicNotificationProfileProperty<>(
			"throttledMessage",
			"ConsoleNotification." + "Properties.ThrottledMessage.DisplayName",
			null,
			String.class
		);

	public static final BasicNotificationProfileProperty<Long> TIME_BETWEEN_NOTIFICATIONS =
		new BasicNotificationProfileProperty<>(
			"delayBetweenContact",
			"ConsoleNotification." + "Properties.TimeBetweenNotifications.DisplayName",
			null,
			Long.class
		);

	public static final BasicNotificationProfileProperty<Boolean> TEST_MODE = new BasicNotificationProfileProperty<>(
		"testMode",
		"ConsoleNotification." + "Properties.TestMode.DisplayName",
		null,
		Boolean.class
	);

	/**
	 * EXTENDED CONFIG - These are different than the properties above, they are registered for each alarm through the
	 * extended config system
	 **/
	public static AlarmProperty<String> CUSTOM_MESSAGE = new BasicAlarmProperty<String>("CustomConsoleMessage",
			String.class, "",
			"ConsoleNotification.Properties.ExtendedConfig.CustomMessage",
			"ConsoleNotification.Properties.ExtendedConfig.Category",
			"ConsoleNotification.Properties.ExtendedConfig.CustomMessage.Desc", true, false);

	static {
		MESSAGE.setExpressionSource(true);
		MESSAGE.setDefaultValue(i18n("ConsoleNotification." + "Properties.Message.DefaultValue"));

		THROTTLED_MESSAGE.setExpressionSource(true);
		THROTTLED_MESSAGE.setDefaultValue(i18n("ConsoleNotification." + "Properties.ThrottledMessage.DefaultValue"));

		TIME_BETWEEN_NOTIFICATIONS.setExpressionSource(true);
		TIME_BETWEEN_NOTIFICATIONS.setDefaultValue(i18n("ConsoleNotification."
				+ "Properties.TimeBetweenNotifications.DefaultValue"));

		TEST_MODE.setDefaultValue(false);
		List<ConfigurationProperty.Option<Boolean>> options = new ArrayList<>();
		options.add(new ConfigurationProperty.Option<>(true, new LocalizedString("words.yes")));
		options.add(new ConfigurationProperty.Option<>(false, new LocalizedString("words.no")));
		TEST_MODE.setOptions(options);
	}

}
