/* Filename: BasePanel.java
 * Created on Jul 12, 2010
 * Author: Kevin Herron
 * Copyright Inductive Automation 2010
 * Project: WeatherModule_Client
 */

package com.inductiveautomation.ignition.examples.wme.client;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;

public abstract class BasePanel extends JPanel implements PropertyChangeListener {

	protected WeatherComponent weatherComponent;

	public BasePanel(WeatherComponent weatherComponent) {
		this.weatherComponent = weatherComponent;
		
		setOpaque(false);
		
		initComponents();
		addComponents();
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
				
	}
	
	protected abstract void initComponents();

	protected abstract void addComponents();
	
}
