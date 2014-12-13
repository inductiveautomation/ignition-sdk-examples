/* Filename: HelloWorldComponent.java
 * Created by Perry Arellano-Jones on 12/11/14.
 * Copyright Inductive Automation 2014
 */
package com.inductiveautomation.ignition.examples.ce.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.inductiveautomation.vision.api.client.components.model.AbstractVisionComponent;

/**
 * This is the actual component for the Ignition SDK's component example module.
 *
 * @author Carl Gould
 */
public class HelloWorldComponent extends AbstractVisionComponent {

    /**
     * No animation
     */
    public static final int ANIMATION_OFF = 0;
    /**
     * Marquee animation, right to left
     */
    public static final int ANIMATION_RTL = 1;
    /**
     * Marquee animation, left to right
     */
    public static final int ANIMATION_LTR = 2;

    /**
     * The text we'll draw inside our bounds
     */
    private String text = "Hello World";

    private Color fillColor = Color.BLACK;
    private Color strokeColor = Color.BLACK;
    private float strokeWidth = 0f;

    private int animation = ANIMATION_OFF;
    private int animationRate = 30;

    /**
     * The Swing timer used when animation is turned on
     */
    private Timer _timer;
    /**
     * The position for the animation: 0-99
     */
    private int _position = 0;

    /**
     * The listener for the swing timer
     */
    private ActionListener animationListener = new ActionListener() {
        int t = 0;

        /** This gets executed when animation is turned on, to move the animationPosition between 0 and 99 */
        public void actionPerformed(ActionEvent e) {
            t = (t + 1) % 100;
            if (animation == ANIMATION_RTL) {
                _position = 100 - t;
            } else {
                _position = t;
            }
            repaint();
        }
    };

    /**
     * Temp rectangle used to store our component's inner area (compensated for border size)
     */
    private Rectangle _area;

    /**
     * Stores the actual Java2D stroke object to use
     */
    private Stroke _stroke = null;

    /**
     * The constructor for the component. Note that it takes zero arguments. This is important for serialization
     */
    public HelloWorldComponent() {
        setOpaque(false);
        setPreferredSize(new Dimension(130, 45));
        setFont(new Font("Dialog", Font.PLAIN, 24));
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("test");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        f.add(new HelloWorldComponent());

        f.setBounds(100, 100, 300, 300);
        f.setVisible(true);
    }

    /**
     * Overriding paintComponent is how you make a component that has custom graphics. All of the graphics code here
     * would be covered in any Java2D book.
     */
    @Override
    protected void paintComponent(Graphics _g) {
        Graphics2D g = (Graphics2D) _g;

        // Preserve the original transform to roll back to at the end
        AffineTransform originalTx = g.getTransform();

        // Turn on anti-aliasing
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Calculate the inner area, compensating for borders
        _area = SwingUtilities.calculateInnerArea(this, _area);
        // Now translate so that 0,0 is is at the inner origin
        g.translate(_area.x, _area.y);

        // Set the font to our component's font property

        g.setFont(getFont());
        FontMetrics fm = g.getFontMetrics();

        // Calculate the x,y for the String's baseline in order to center it
        int stringWidth = fm.stringWidth(text);
        int stringX = (_area.width - stringWidth) / 2;

        // This is the _easy_ way to draw a string, but that's no fun...
        //		g.drawString(text, stringX, stringY);

        if (_position == 0) {
            // Perfectly centered
            paintTextAt(g, stringX);
        } else {
            // Draw twice to achieve marquee effect
            float dX = _area.width * (_position / 100f);
            paintTextAt(g, stringX + dX);
            paintTextAt(g, stringX - _area.width + dX);
        }

        // Reverse any transforms we made
        g.setTransform(originalTx);
    }

    private void paintTextAt(Graphics2D g, float xPosition) {
        Font font = g.getFont();
        FontRenderContext frc = g.getFontRenderContext();
        GlyphVector vector = font.createGlyphVector(frc, text);

        Rectangle2D bounds = vector.getVisualBounds();

        float yPosition = (float) (_area.getHeight() + bounds.getHeight()) / 2f;

        Shape textShape = vector.getOutline(xPosition, yPosition);

        g.setColor(fillColor);
        g.fill(textShape);

        if (_stroke != null) {
            g.setColor(strokeColor);
            g.setStroke(_stroke);
            g.draw(textShape);
        }
    }

	/*
     * Even though the animation is fun, it is really there to show how to use these startup/shutdown lifecycle methods
	 * to ensure that any long-running logic in your component gets properly shut down.
	 */

    @Override
    protected void onStartup() {
        // Seems like a no-op, but actually will trigger logic to re-start the timer if necessary
        setAnimation(animation);
    }

    @Override
    protected void onShutdown() {
        if (_timer != null && _timer.isRunning()) {
            _timer.stop();
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        // Firing property changes like this is required for any property that has the BOUND_MASK set on it.
        // (See this component's BeanInfo class)
        String old = this.text;
        this.text = text;
        firePropertyChange("text", old, text);

        repaint();
    }

    public int getAnimation() {
        return animation;
    }

    public void setAnimation(int animation) {
        this.animation = animation;
        if (animation == ANIMATION_OFF && _timer != null && _timer.isRunning()) {
            _timer.stop();
            _position = 0;
            repaint();
        }
        if (animation != ANIMATION_OFF) {
            if (_timer == null) {
                _timer = new Timer(animationRate, animationListener);
            }
            if (!_timer.isRunning()) {
                _timer.start();
            }
        }
    }

    public Color getFillColor() {
        return fillColor;
    }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
        repaint();
    }

    public Color getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(Color strokeColor) {
        this.strokeColor = strokeColor;
        repaint();
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        if (strokeWidth > 0) {
            _stroke = new BasicStroke(strokeWidth);
        } else {
            _stroke = null;
        }
        repaint();
    }

    public int getAnimationRate() {
        return animationRate;
    }

    public void setAnimationRate(int animationRate) {
        this.animationRate = Math.max(10, animationRate);
        if (_timer != null) {
            _timer.setDelay(animationRate);
        }
    }

}
