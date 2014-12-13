/* Filename: WeatherComponent.java
 * Created on Jul 9, 2010
 * Author: Kevin Herron
 * Copyright Inductive Automation 2010
 * Project: WeatherModule_Client
 */
package com.inductiveautomation.ignition.examples.wme.client;

import java.awt.Dimension;
import java.awt.RenderingHints;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import com.inductiveautomation.ignition.client.util.gui.tablelayout.TableLayout;
import com.inductiveautomation.vision.api.client.components.model.AbstractVisionPanel;


public class WeatherComponent extends AbstractVisionPanel implements PropertyChangeListener {

	public static Map<RenderingHints.Key, Object> RENDERING_HINTS = new HashMap<RenderingHints.Key, Object>();
	static {
		RENDERING_HINTS.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		RENDERING_HINTS.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		RENDERING_HINTS.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		RENDERING_HINTS.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		RENDERING_HINTS.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	}

	private String zipCode;
	private String currentTemp;
	private String lowTemp;
	private String highTemp;
	private String iconName;

	private WeatherPanel weatherPanel;

	public WeatherComponent() {
		setPreferredSize(new Dimension(WeatherPanel.PREFERRED_SIZE.width + 10, WeatherPanel.PREFERRED_SIZE.height + 10));
		setOpaque(false);

		initComponents();
		addComponents();
	}

	private void initComponents() {
		weatherPanel = new WeatherPanel(this);
	}

	private void addComponents() {
		double[][] size = { { TableLayout.FILL, TableLayout.PREFERRED, TableLayout.FILL },
				{ TableLayout.FILL, TableLayout.PREFERRED, TableLayout.FILL } };

		setLayout(new TableLayout(size));

		add(weatherPanel, "1,1,c,c");
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

	}

	public String getCurrentTemp() {
		return currentTemp;
	}

	public void setCurrentTemp(String currentTemp) {
		String old = this.currentTemp;
		this.currentTemp = currentTemp;
		firePropertyChange("currentTemp", old, this.currentTemp);
	}

	public String getLowTemp() {
		return lowTemp;
	}

	public void setLowTemp(String lowTemp) {
		String old = this.lowTemp;
		this.lowTemp = lowTemp;
		firePropertyChange("lowTemp", old, this.lowTemp);
	}

	public String getHighTemp() {
		return highTemp;
	}

	public void setHighTemp(String highTemp) {
		String old = this.highTemp;
		this.highTemp = highTemp;
		firePropertyChange("highTemp", old, this.highTemp);
	}

	public String getIconName() {
		return iconName;
	}

	public void setIconName(String iconName) {
		String old = this.iconName;
		this.iconName = iconName;
		firePropertyChange("iconName", old, this.iconName);
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		String old = this.zipCode;
		this.zipCode = zipCode;
		firePropertyChange("zipCode", old, this.zipCode);
	}

}
