package com.inductiveautomation.reporting.examples.component.common.shapes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import com.inductiveautomation.reporting.common.api.shape.KeyBase;
import com.inductiveautomation.reporting.common.api.shape.ShapeMeta;
import com.inductiveautomation.reporting.common.api.shape.ShapeProperty;
import com.inductiveautomation.rm.archiver.RXArchiver;
import com.inductiveautomation.rm.archiver.RXElement;
import com.inductiveautomation.rm.shape.RMShape;
import com.inductiveautomation.rm.shape.ReportOwner;
import com.inductiveautomation.rm.shape.j2dshapes.AbstractJ2DShape;

/**
 * This class extends AbstractJ2DShape (itself extending {@link RMShape}) to provide an easy way to add
 * simple shapes to the Report Design Palette.
 *
 * Use {@link ShapeMeta} to declare what category of the palette we want our component to be in, and then give the path
 * to our component's icon resource, which has a base path of 'images/'.
 * @author Perry Arellano-Jones
 */
@ShapeMeta(category = "reporting.Category.Shapes", iconPath = "smiley.png")
@KeyBase("component.Shape.Smiley")  // the base resource path to our bundle containing property terms for this component
public class Smiley extends AbstractJ2DShape {
    public static final String ARCHIVE_NAME = "report-smiley-component";   // unique id of serialization archive, needs
                                                                           // to be registered in gateway hook along with
                                                                           // Smile.class
    public static final int ORIGIN = 0;
    private boolean isHappy = true;
    private Color background = Color.YELLOW;
    private Color foreground = Color.BLACK;


    protected void render(Graphics2D g, int width, int height) {
        // don't draw if superclass visibility is disabled
        if (isVisible()) {

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            // draw an outline
            g.setColor(foreground);
            g.drawOval(ORIGIN, ORIGIN, width - 1, height - 1);

            // fill in with background color
            g.setColor(background);
            g.fillOval(ORIGIN + 1, ORIGIN + 1, width - 2, height - 2);


            // eyes
            g.setColor(foreground);
            g.fillOval((int) (width * 0.25), (int) (height * 0.3), width / 6, height / 6);
            g.fillOval((int) (width * 0.60), (int) (height * 0.3), width / 6, height / 6);


            // values for a happy face
            int arc = -180;
            int mouthY = (int) (height * 0.28);
            int mouthYBottom = (int) (height * 0.23);
            int mouthWidth = (int) (width * 0.7);
            int mouthHeight = (int) (height * 0.6);
            int mouthX = (int) (width * .15);

            // alter mouth values for sad face
            if (!isHappy) {
                arc = 180;
                int temp = mouthY;
                mouthY = mouthYBottom + (int) (height * .4);
                mouthYBottom = temp + (int) (height * .4);
                mouthWidth = (int) (mouthWidth * .8);
                mouthX = (int) (mouthX * 1.5);
                mouthHeight = (int) (mouthHeight * .6);
            }

            g.fillArc(mouthX, mouthY, mouthWidth, mouthHeight, 0, arc);
            // draw a second/offset arc for a better mouth appearance
            g.setColor(background);
            g.fillArc(mouthX, mouthYBottom, mouthWidth, mouthHeight, 0, arc);
        }

    }

    /* The ShapeProperty annotations work in concert with the KeyBase annotation on the class.  By giving this
     * method the annotation (and assuming we have both getter and setter), we enable this field to be added
     * to the property table when designing/editing our component.  So (keybase.Background, or in this case
     * 'component.Shape.Smiley.Background' will be checked for a .Name, .Description and .Category to fill in
     * values.  Take a look at common/src/main/resources/component.properties to see examples.
     */
    @ShapeProperty("Background")
    public Color getBackground() {
        return background;
    }

    public void setBackground(Color background) {
        if (!this.background.equals(background)) {
            this.background = background;
            setDirty();
        }
    }

    @ShapeProperty("Foreground")
    public Color getForeground() {
        return foreground;
    }

    public void setForeground(Color foreground) {
        if (!this.foreground.equals(foreground)) {
            this.foreground = foreground;
            setDirty();
        }
    }

    @ShapeProperty("Happy")
    public boolean isHappy() {
        return isHappy;
    }

    public void setHappy(boolean isHappy) {
        this.isHappy = isHappy;
        setDirty();
    }

    /* override to add setDirty() call to trigger the render() when changed */
    @Override
    public void setVisible(boolean aValue) {
        super.setVisible(aValue);
        setDirty();
    }

    /**
     * Shapes and components go through an XML serialization process.  To add things to the archiver, use
     * {@link RXElement} and add(String, name), where the String is a key name you will use to pull the value
     * from the archiver when deserializing.
     */
    @Override
    public RXElement toXML(RXArchiver anArchiver) {
        RXElement e = super.toXML(anArchiver);

        e.setName(ARCHIVE_NAME);   // set a unique name for this archive

        e.add("isHappy", isHappy);
        e.add("background", background);
        e.add("foreground", foreground);
        return e;
    }

    /**
     *  Pull data out of serialization by using the various RXElement.get() calls for your types.
     */
    @Override
    public Object fromXML(RXArchiver archiver, RXElement e) {
        super.fromXML(archiver, e);

        background = e.getAttributeColorValue("background", Color.YELLOW);
        foreground = e.getAttributeColorValue("foreground", Color.BLACK);
        setHappy(e.getAttributeBooleanValue("isHappy", true));

        return this;
    }

    /**
     * Called when the report is generated.  If our shape were to require data to create/populate itself, we would do so
     * in this method by building a dataset and instantiating a clone() of our shape with the associated dataset.
     * In this case, we aren't doing anything computational, so our RPG can just return a clone;
     */
    @Override
    protected RMShape rpgShape(ReportOwner owner, RMShape shape){
        Smiley rpg = (Smiley) clone();

        if (rpg == null) {
            rpg = new Smiley();
        }
        rpg.setHappy(isHappy);
        rpg.setBackground(background);
        rpg.setForeground(foreground);
        rpg.setVisible(isVisible());
        return rpg;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this){
            return true;
        }
        if (!super.equals(obj)){
            return false;
        }

        Smiley other = (Smiley) obj;
        return !(!other.background.equals(background)
                || other.isHappy != isHappy
                || other.foreground.equals(foreground));
    }
}