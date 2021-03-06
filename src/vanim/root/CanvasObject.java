package vanim.root;

import vanim.core.Applet;
import vanim.core.Graphics2D;
import vanim.root.builder.GeometricSpaceBuilder;
import vanim.root.modular.Colorable;
import vanim.root.modular.Displayable;
import vanim.storage.Scale;
import vanim.storage.color.Color;
import vanim.storage.vector.FVector;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static vanim.util.map.MapEase.EASE_IN;

/**
 * @author protonlaser91
 */
public abstract class CanvasObject implements Colorable<CanvasObject>, Displayable {

    private static final Map<Class<? extends CanvasObject>, Set<WeakReference<CanvasObject>>> allObjects = new HashMap<>();
    protected Graphics2D canvas;
    protected Color color;
    protected Scale scale = new Scale(1, 1, 1);
    protected FVector pos;
    protected FVector dimensions; //width height
    protected boolean isRemoved = false;
    public Applet processing;

    /**
     * @param p          The processing instance that will be used to perform operations.
     * @param c          The canvas that will be drawn on
     * @param pos        The position of the object on that plane (in scaled coordinates, not absolute)
     * @param dimensions The width, height (and depth) of the object (in scaled coordinates, not absolute)
     */
    @SuppressWarnings("unchecked")
    // Will work because the maximum superclass that will be reached is CanvasObject itself!
    protected CanvasObject(Applet p, Graphics2D c, FVector pos, FVector dimensions, Color color) {
        processing = p;
        canvas = c;
        this.pos = pos;
        this.dimensions = dimensions;
        this.color = color;

        Class<? extends CanvasObject> originalClass = getClass();
        Class<? super CanvasObject> finalSuperclass = CanvasObject.class.getSuperclass();
        while (!originalClass.equals(finalSuperclass)) {
            allObjects.computeIfAbsent(originalClass,
                    k -> new HashSet<>()).add(new WeakReference<>(this));
            originalClass = (Class<? extends CanvasObject>) originalClass.getSuperclass();
        }
    }

    protected CanvasObject(Applet p, Graphics2D c, FVector pos, FVector dimensions) {
        this(p, c, pos, dimensions, new Color());
    }

    protected CanvasObject(Graphics2D c, FVector pos, FVector dimensions) {
        this(null, c, pos, dimensions);
    }

    public CanvasObject(GeometricSpaceBuilder builder) {
        this(builder.getProcessingInstance(), builder.getProcessingInstance().createGraphics2D(builder.getDimensions()), builder.getPos(), new FVector(builder.getDimensions()), builder.getColor());
    } // Gah this needs to be checked for 3D too!

    /**
     * @return Color object of the selected CanvasObject
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * @return The position of the selected CanvasObject
     */
    public FVector getPos() {
        return this.pos;
    }

    /**
     * @return The dimensions of the selected CanvasObject
     */
    public FVector getDimensions() {
        return this.dimensions;
    }

    /**
     * @return If this CanvasObject has been removed
     */
    public boolean isRemoved() {
        return this.isRemoved;
    }

    /**
     * @param types The class(es) (<T>) of objects of which to be returned
     * @return An immutable Set of all objects that have been created and are <T> Type
     * or a subclass of <T>, where <T> is the Class Type of the types parameter
     */

    @SuppressWarnings("unchecked")
    public static <K> Set<K> getAllObjects(Class<? extends K>... types) {
        Stream<K> stream = Stream.empty();
        for (Class<? extends K> type : types) {
            stream = (Stream<K>) Stream.concat(stream, allObjects.get(type).parallelStream().map(Reference::get));
        }

        return stream.collect(Collectors.toUnmodifiableSet());
    }

    /**
     * @return null if the operation is a success
     */
    public CanvasObject remove() {
        isRemoved = true;
        return null;
    }

    /**
     * @return The scale the plane uses.
     */
    public Scale getScale() {
        return this.scale;
    }

    /**
     * Interpolate alpha to 0 (fade to black)
     *
     * @return When the operation has completed
     */

    @Override
    public boolean fadeOut() {
        return color.getAlpha().interpolate(0, EASE_IN);
    }

    /**
     * Interpolate alpha to 255 (fade in)
     *
     * @return When the operation has completed
     */
    @Override
    public boolean fadeIn() {
        return color.getAlpha().interpolate(255, EASE_IN);
    }

    /**
     * @param color The color to change
     * @return The original CanvasObject
     */
    public CanvasObject setColor(Color color) {
        this.color = color;
        return this;
    }
}
