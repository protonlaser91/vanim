package vanim.shapes;

import vanim.planes.Plane;
import vanim.root.modular.Graphable;
import vanim.root.vobjects.VObject;
import vanim.storage.Color;
import vanim.storage.vector.FVector;
import vanim.util.Reason;
import vanim.util.Useful;

import java.util.ArrayList;
import java.util.List;

/**
 * @author protonlaser91
 */
public class ClosedShape extends VObject implements Graphable { // Maybe in the far far future when i add 3D call this ClosedShape2D

    protected float incrementTangentLine = 0;
    protected int speed;
    protected float distance; // Soon make this Line tangentLine;
    protected long maxPoints = 630;
    protected double newInterpVal = 0, prevInterpVal = 0;
    protected List<float[]> coords = new ArrayList<>(); //[x,y]

    /**
     * @param p             Plane that is to be drawn on
     * @param pos           The position of the object on that plane (in scaled coordinates, not absolute)
     * @param dimensions    The width, height (and depth) of the object (in scaled coordinates, not absolute)
     * @param speed         How fast the circle should be drawn. 1 is 1/TAU points every tick
     * @param reasonCreated The reason this object was created
     */
    public ClosedShape(Plane p, FVector pos, FVector dimensions, int speed, Reason reasonCreated) {
        super(p, pos, dimensions, new Color(0), reasonCreated); // For now, it does not utilize a color Hue (but does have sat and brightness!)
        this.speed = speed;
    }

    public ClosedShape(Plane p, FVector pos, FVector dimensions, int speed) {
        this(p, pos, dimensions, speed, Reason.USER_CREATED);
    }

    //TODO: Add Predicate type thing to add Points[] to ClosedShape

    public ClosedShape setMaxPoints(long maxPoints) {
        this.maxPoints = maxPoints;
        return this;
    }

    public long getPoints() {
        return this.maxPoints;
    }

    /**
     * @param x X-coordinate of the point to be added to the coordinates list
     * @param y Y-coordinate of the point to be added to the coordinates list
     * @return When the operation has successfully added the required number of points
     */
    @Override
    public boolean addPoint(float x, float y) {
        if (coordsSize < maxPoints) { // Optimization check originally delVal
            coords.add(new float[]{dimensions.getX() * x, -dimensions.getY() * y});
            coordsSize++;
        }
        return coordsSize < maxPoints;
    }

    /**
     * Graph the points in the coordinate array via iteration
     * @return When the operation has completed
     */
    @Override
    public boolean graph() {

        /* Works for all closed shapes
        boolean epsX = coords.get(0)[0] / coords.get(coordsSize - 1)[0] > 0.99f && coords.get(0)[0] / coords.get(coordsSize - 1)[0] < 1.01f; // x does not start/end at 0 !
        boolean epsY = coordsSize > 1 && coords.get(coordsSize - 2)[1] > coords.get(0)[1] && coords.get(coordsSize - 1)[1] <= coords.get(0)[1];


         if (epsX && epsY) {
            optimalDelVal = coordsSize;
            println("Optimal delVal: " + optimalDelVal); //<-- reenable
        } */

        float xMult = scale.getX(), yMult = scale.getY();

        //TODO: Add color class to ClosedShape and its children and have Useful.getColor(i,0,delVal)
        // be a change to the hue and canvas.stroke(color);
        for (int i = 0; i < coordsSize - 1; i++) {
            canvas.stroke(Useful.getColor(i, 0, maxPoints), color.getSaturation().getValue(),
                    color.getBrightness().getValue(), color.getAlpha().getValue());
            canvas.strokeWeight(5);
            canvas.line(coords.get(i)[0] * xMult, coords.get(i)[1] * yMult, coords.get(i + 1)[0] * xMult, coords.get(i + 1)[1] * yMult);
        }

        return coordsSize == maxPoints; // might not be necessary because of addPoints() bool result!
    }

    /**
     * TODO Fix this shit
     *
     * @param obj Varargs to display the object at coordinates
     * @return When the operation has completed showing
     */
    @Override
    public boolean display(Object... obj) {
        return false;
    }
}
