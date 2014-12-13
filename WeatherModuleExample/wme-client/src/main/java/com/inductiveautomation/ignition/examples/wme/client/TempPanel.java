/* Filename: CurrentPanel.java
 * Created on Jul 14, 2010
 * Author: Kevin Herron
 * Copyright Inductive Automation 2010
 * Project: WeatherModule_Client
 */
package com.inductiveautomation.ignition.examples.wme.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.beans.PropertyChangeEvent;

import javax.swing.JLabel;

import com.inductiveautomation.ignition.client.util.gui.tablelayout.TableLayout;
import com.inductiveautomation.ignition.common.TypeUtilities;

public class TempPanel extends BasePanel {

	public static final Dimension PREFERRED_SIZE = new Dimension(134, 134);

	private static final String degreeSymbol = "\u00B0";

	private Font baseFont;

	private JLabel tempLabel;

	public TempPanel(WeatherComponent weatherComponent) {
		super(weatherComponent);

		setPreferredSize(PREFERRED_SIZE);

		weatherComponent.addPropertyChangeListener("font", this);
		weatherComponent.addPropertyChangeListener("currentTemp", this);
	}

	@Override
	protected void initComponents() {
		tempLabel = new JLabel(degreeify(null));
		tempLabel.setForeground(Color.WHITE);

		updateFonts();
	}

	@Override
	protected void addComponents() {
		double size[][] = { { TableLayout.FILL }, { TableLayout.FILL } };

		setLayout(new TableLayout(size));

		add(tempLabel, "0,0,c,c");
	}

	private void removeComponents() {
		remove(tempLabel);
	}

	private void updateFonts() {
		Font font = new Font("arial", Font.PLAIN, 48);
		if (baseFont != null) {
			font = new Font(baseFont.getName(), baseFont.getStyle(), baseFont.getSize() + 36);
		}

		tempLabel.setFont(font);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String name = evt.getPropertyName();

		if ("currentTemp".equalsIgnoreCase(name)) {
			String currentTemp = (String) TypeUtilities.coerce(evt.getNewValue(), String.class);
			tempLabel.setText(degreeify(currentTemp));
		}

		else if ("font".equalsIgnoreCase(name)) {
			baseFont = (Font) TypeUtilities.coerce(evt.getNewValue(), Font.class);

			removeComponents();
			initComponents();
			addComponents();

			validate();
		}

		repaint();
	}

	private String degreeify(String temp) {
		if (temp == null || temp.length() == 0) {
			temp = "--";
		}
		return String.format("%s%s", temp, degreeSymbol);
	}

}
