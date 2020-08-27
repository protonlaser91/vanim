package vanim.directions.subscene;

import org.apache.commons.math3.analysis.solvers.BrentSolver;
import vanim.core.Applet;
import vanim.directions.Scene;
import vanim.mfunc.Narrator;
import vanim.planes.CartesianPlane;
import vanim.planes.Plane;
import vanim.root.CanvasObject;
import vanim.root.vobjects.AbsoluteVObject;
import vanim.shapes.Circle;
import vanim.shapes.Line;
import vanim.specific.rational.Fraction;
import vanim.storage.Color;
import vanim.storage.Scale;
import vanim.storage.vector.FVector;
import vanim.storage.vector.IVector;
import vanim.text.LaTeX;
import vanim.util.Mapper;
import vanim.util.Reason;
import vanim.util.Useful;
import vanim.util.function.ParametricFunction;

import java.util.Map;

import static processing.core.PApplet.*;
import static vanim.directions.Directions.*;
import static vanim.util.map.MapEase.EASE_IN_OUT;
import static vanim.util.map.MapType.LINEAR;
import static vanim.util.map.MapType.SINUSOIDAL;

public final class Scene1 extends Scene {

    public static Scale sinus = new Scale(1, 1);
    public static CartesianPlane plane;
    public static Circle b;
    public static Narrator n;
    public static Line l;
    public static boolean changeHue = false;
    public static LaTeX bruh;
    Map.Entry<Long, Long> frac = Fraction.reduce(0, 1);
    BrentSolver solver = new BrentSolver();
    ParametricFunction function = new ParametricFunction(t -> t * Math.cos(t), t -> t * Math.sin(t));
    // Ascending order

    public Scene1(Applet window) {
        super(window);
        plane = new CartesianPlane(window, new FVector(), new IVector(1920, 1080), new FVector(1, 1));
        b = new Circle(plane, new FVector(0, 0), 1, 4);
        n = new Narrator(plane.getCanvas());
        //  l = new Line(plane, new FVector(), new FVector(1, 1), new Color(255, 255, 255));
        bruh = new LaTeX(plane, "\\[ x^2 = sin(9x) \\]", new FVector(0, 0), 120, new Color(190, 255, 255));
        System.out.println("welcome to the game!");
    }

    @Override
    public boolean execute() {

        step[0] = plane.generatePlane();

        /* Maybe think about a MObject[] or ArrayList<MObject> where display can be called on everyone */

        if (step[0]) {
            println(solver.solve(Integer.MAX_VALUE, t -> 4 * plane.getAxes().getX() - function.getParametricX().value(t), -Integer.MAX_VALUE, Integer.MAX_VALUE));
            println(solver.solve(Integer.MAX_VALUE, t -> 4 * plane.getAxes().getX() + function.getParametricX().value(t), -Integer.MAX_VALUE, Integer.MAX_VALUE));

            plane.getColor().getHue().interpolate(changeHue ? 0 : 255, LINEAR, 1);
            if (plane.getColor().getHue().is255())
                changeHue = true;
            else if (plane.getColor().getHue().is0())
                changeHue = false;

            b.scale(sinus);
            bruh.setColor(plane.getColor().invert()); // :) Well this aint g00d
            if (window.frameCount % 20 == 0)
                frac = Fraction.reduce(window.frameCount, 1000);
            bruh.setLaTeX(String.format("\\[ $x^{%d}$ = sin(\\frac{%d}{%d}x) \\]", window.frameCount, frac.getKey(), frac.getValue())); // Change to something
            // That implements Map.Entry and has methods getNumerator and getDenominator (can be Frac class itself?)
            plane.displayText(bruh.scale(sinus));
            step[1] = b.display();
        }

        if (step[1])
            step[2] = b.drawTangentLine(cos(mapInc), sin(mapInc), true);


        if (step[2]) {
            plane.getCanvas().text("Radius: " + b.getRadius(), 300, -330);
            inc += globalIncrementor;
            plane.getCanvas().textSize(40);
            /* Call to waste time
            int[] useless = RandomDebug.generateRandomIntArr(8);
            Arrays.sort(useless);
            canvas.text(Arrays.toString(useless),400,-270); //String.format("(%s) - (%s)",destinationOnCircleLabel.get(destinationInc == 0 ? destinationOnCircle.length - 1 : destinationInc-1),destinationOnCircleLabel.get(destinationInc))
            Call to waste time */
            float min = destinationOnCircle[destinationInc == 0 ? destinationOnCircle.length - 1 : destinationInc - 1];
            float max = destinationOnCircle[destinationInc];
            mapInc = Mapper.map2(inc % 2, 0f, 2f, -min, -max, SINUSOIDAL, EASE_IN_OUT);

            if (inc % 2 > 2 - globalIncrementor) {
                destinationInc = (destinationInc + 1) % destinationOnCircle.length;
                inc = (float) Useful.ceilAny(inc, globalIncrementor);
            }

            if (!step[3])
                step[3] = destinationInc == 0; //Advance to dir[3]
        }

        if (step[3]) {
            //System.out.println("Col: " + plane.getColor() + " tC: " + plane.getTextColor());
            CanvasObject.getAllObjects(AbsoluteVObject.class).parallelStream()
                    .filter(k -> k.getReasonCreated().equals(Reason.USER_CREATED))
                    .forEach(CanvasObject::fadeOut);

            CanvasObject.getAllObjects(Plane.class).parallelStream().forEach(Plane::fadeOut);


            if (incTrack == 0) {
                incTrack = inc;
                frameCountTrack = window.frameCount;
            }

            if (!step[4]) {
                sinus.setAll(Mapper.map2(inc, incTrack, incTrack + 1.5f, 1f, 1.5f, SINUSOIDAL, EASE_IN_OUT));
                step[4] = Math.abs(sinus.getX() - 1.5) < 0.01;
            }
        }

        if (!step[5] && step[4]) { // Stop running once step 5 is true
            sinus.setAll(Mapper.map2(inc, incTrack - 0.5f, incTrack + 1.5f, 0.5f, 1.5f, SINUSOIDAL, EASE_IN_OUT));
            step[5] = window.frameCount - frameCountTrack > 850 && Math.abs(sinus.getX() - 1) < 0.01;
        }

        if (step[5]) {
            sinus.setAll(1);
        }
        plane.display();
        return step[6];

    }
}
