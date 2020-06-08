package vanim.shapes;

import static vanim.planar.*;

import processing.core.*;
import vanim.Planes.Plane;
import vanim.storage.Scale;
import vanim.storage.Vector;

/**
 * @author protonlaser91
 */
public class Circle extends Ellipse {

    /**
     *
     * @param p Plane that is to be drawn on
     * @param pos The position of the object on that plane (in scaled coordinates, not absolute)
     * @param radius The radius of the circle (in scaled coordinates)
     * @param speed How fast the circle should be drawn. 1 is 1/TAU points every tick
     * @param delVal How fast the circle should be colored in per tick. After delVal ticks, the color wheel will
     *               have reached the beginning.
     */
    public Circle(Plane p, Vector<Float> pos, float radius, int speed, int delVal) {
        super(p, pos, new Vector<>(radius, radius), speed, delVal);
    }

    public Circle(Plane p, Vector<Float> pos, float radius, int speed) {
        this(p, pos, radius, speed, floor(TAU * 100 / speed + 2));
    }

    /**
     *
     * @return Radius of the circle, in scaled coordinates.
     */
    public float getRadius() {
        //width and height are same in Circle, depends on sX and sY
        return dimensions.getX() / scale.getX();
    }
}

