/* Filename: IconPanel.java
 * Created on Jul 12, 2010
 * Author: Kevin Herron
 * Copyright Inductive Automation 2010
 * Project: WeatherModule_Client
 */
package com.inductiveautomation.ignition.examples.wme.client;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.beans.PropertyChangeEvent;

import javax.swing.ImageIcon;

public class IconPanel extends BasePanel {

    public static final Dimension PREFERRED_SIZE = new Dimension(160, 134);

    private final Object imageLock = new Object();
    private Image currentImage = null;

    public IconPanel(WeatherComponent weatherComponent) {
        super(weatherComponent);

        setPreferredSize(PREFERRED_SIZE);

        // currentImage = new
        // ImageIcon(IconPanel.class.getResource("images/cond038.png")).getImage();

        weatherComponent.addPropertyChangeListener("iconName", this);
    }

    @Override
    protected void initComponents() {

    }

    @Override
    protected void addComponents() {

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHints(WeatherComponent.RENDERING_HINTS);

        synchronized (imageLock) {
            if (currentImage != null) {
                float w = currentImage.getWidth(null);
                float h = currentImage.getHeight(null);
                int myWidth = getWidth() - 10;
                int myHeight = getHeight() - 20;

                // Scale the image...
                if (w > myWidth) {
                    float ratio = w / myWidth;
                    w /= ratio;
                    h /= ratio;
                }

                if (h > myHeight) {
                    float ratio = h / myHeight;
                    w /= ratio;
                    h /= ratio;
                }

                g2.drawImage(currentImage, (int) (getWidth() - w) / 2, (int) (getHeight() - h) / 2, (int) w,
                        (int) h, null);
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("iconName".equalsIgnoreCase(evt.getPropertyName())) {
            String imagePath = String.format("images/%s", evt.getNewValue());

            synchronized (imageLock) {
                try {
                    currentImage = new ImageIcon(IconPanel.class.getResource(imagePath)).getImage();
                } catch (Exception e) {
                    currentImage = null;
                }
            }
            repaint();
        }
    }

}
