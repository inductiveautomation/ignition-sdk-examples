/* Filename: FancyPanel.java
 * Created on Jul 14, 2010
 * Author: Kevin Herron
 * Copyright Inductive Automation 2010
 * Project: WeatherModule_Client
 */
package com.inductiveautomation.ignition.examples.wme.client;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import com.inductiveautomation.ignition.client.util.gui.tablelayout.TableLayout;

public class WeatherPanel extends BasePanel {

    public static final Dimension PREFERRED_SIZE = new Dimension(134 + 160 + 134, 134);

    private InfoPanel infoPanel;
    private IconPanel iconPanel;
    private TempPanel tempPanel;

    public WeatherPanel(WeatherComponent weatherComponent) {
        super(weatherComponent);

        setPreferredSize(PREFERRED_SIZE);
    }

    @Override
    protected void initComponents() {
        infoPanel = new InfoPanel(weatherComponent);
        iconPanel = new IconPanel(weatherComponent);
        tempPanel = new TempPanel(weatherComponent);
    }

    @Override
    protected void addComponents() {
        double[][] size = {
                {TableLayout.FILL, TableLayout.FILL, TableLayout.FILL},
                {TableLayout.FILL}
        };

        setLayout(new TableLayout(size));

        add(infoPanel, "0,0");
        add(iconPanel, "1,0");
        add(tempPanel, "2,0");
    }

    public static final Color IA_BLUE = new Color(31, 52, 72);
    public static final Color IA_ORANGE = new Color(247, 144, 30);
    public static final Color LIGHT_BLUE = new Color(80, 108, 138);

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHints(WeatherComponent.RENDERING_HINTS);

        int x = 5;
        int y = 5;
        int width = getWidth() - 10;
        int height = getHeight() - 10;

        g2.setColor(IA_BLUE);
        g2.fillRoundRect(x, y, width, height, 10, 10);

        if (iconPanel != null) {
            int ix = iconPanel.getX();
            int iw = iconPanel.getWidth();
            g2.setColor(LIGHT_BLUE);
            g2.drawLine(ix, y, ix, height + y);
            g2.drawLine(ix + iw, y, ix + iw, height + y);
        }

        g2.setColor(IA_ORANGE);
        g2.setStroke(new BasicStroke(2.0f));
        g2.drawRoundRect(x, y, width, height, 10, 10);
    }
}
