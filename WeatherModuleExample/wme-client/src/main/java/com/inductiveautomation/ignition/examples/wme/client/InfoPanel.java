package com.inductiveautomation.ignition.examples.wme.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.beans.PropertyChangeEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import com.inductiveautomation.ignition.client.util.gui.tablelayout.TableLayout;
import com.inductiveautomation.ignition.common.TypeUtilities;

/* Filename: InfoPanel.java
 * Created on Jul 12, 2010
 * Author: Kevin Herron
 * Copyright Inductive Automation 2010
 * Project: WeatherModule_Client
 */

public class InfoPanel extends BasePanel {

	public static final Dimension PREFERRED_SIZE = new Dimension(134, 134);

	private static final String degreeSymbol = "\u00B0";

	private JLabel zipLabel;
	private JLabel highLabel;
	private JLabel lowLabel;

	private Font baseFont;

	public InfoPanel(WeatherComponent weatherComponent) {
		super(weatherComponent);

		setPreferredSize(PREFERRED_SIZE);

		weatherComponent.addPropertyChangeListener("font", this);
		weatherComponent.addPropertyChangeListener("zipCode", this);
		weatherComponent.addPropertyChangeListener("lowTemp", this);
		weatherComponent.addPropertyChangeListener("highTemp", this);
	}

	@Override
	protected void initComponents() {
		highLabel = new JLabel(degreeify(null));
		highLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
		highLabel.setForeground(Color.WHITE);

		lowLabel = new JLabel(degreeify(null));
		lowLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		lowLabel.setForeground(Color.WHITE);

		zipLabel = new JLabel("-----");
		// t,l,b,r
		zipLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		zipLabel.setForeground(WeatherPanel.LIGHT_BLUE);

		updateFonts();
	}

	@Override
	protected void addComponents() {
		double[][] size = { { TableLayout.FILL },
				{ TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED } };

		setLayout(new TableLayout(size));

		add(highLabel, "0,0,c,c");
		add(lowLabel, "0,1,c,c");
		add(zipLabel, "0,2,c,c");
	}

	private void removeComponents() {
		remove(highLabel);
		remove(lowLabel);
		remove(zipLabel);
	}

	private void updateFonts() {
		Font font = new Font("arial", Font.PLAIN, 24);
		if (baseFont != null) {
			font = new Font(baseFont.getName(), baseFont.getStyle(), baseFont.getSize() + 12);
		}

		highLabel.setFont(font);
		lowLabel.setFont(font);
		zipLabel.setFont(font);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String name = evt.getPropertyName();

		if ("zipCode".equalsIgnoreCase(name)) {
			String zipCode = (String) TypeUtilities.coerce(evt.getNewValue(), String.class);
			zipLabel.setText(zipCode);
		}

		else if ("lowTemp".equalsIgnoreCase(name)) {
			String lowTemp = (String) TypeUtilities.coerce(evt.getNewValue(), String.class);
			lowLabel.setText("L " + degreeify(lowTemp));
		}

		else if ("highTemp".equalsIgnoreCase(name)) {
			String highTemp = (String) TypeUtilities.coerce(evt.getNewValue(), String.class);
			highLabel.setText("H " + degreeify(highTemp));
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
