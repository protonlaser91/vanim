package vanim.directions;

import vanim.core.Applet;
import vanim.util.Mapper;
import vanim.util.Useful;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static vanim.planar.PI;
import static vanim.util.map.MapEase.EASE_IN_OUT;
import static vanim.util.map.MapType.SINUSOIDAL;

public class Directions {
    public static List<Scene> allScenes = new ArrayList<>(); // Order must be preserved!
    public static int destinationInc = 1; // be consistent with mapInc initialization!
    // ^^ Also the reason that destinationInc-1 is used and not +1
    public static final Float[] destinationOnCircle = {-PI, 0f, PI / 4, PI / 2}; // A sad necessity
    // ^^ See if this could become a circular linked list? Along with destinationOnCircleLabel
    public static float incTrack = 0, inc = 0, mapInc = Mapper.map2(inc, 0f, 2f, -destinationOnCircle[0], -destinationOnCircle[1], SINUSOIDAL, EASE_IN_OUT);
    public static float globalIncrementor = 0.01f;
    public static int frameCountTrack = 0;
    // public static Line d = new Line(2);
    //Cosmetics

    public static void init(Applet window) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        File[] files = new File(".\\src\\vanim\\directions\\subscene").listFiles();

        if (files != null)
            for (File file : files) {
                if (file.isFile()) { // Assuming folders?
                    Class<?> c;
                    try {
                        c = Class.forName("vanim.directions.subscene." + Useful.removeExtension(file.getName()));
                    } catch (ClassNotFoundException e) {
                        System.out.println(file.getName() + " is not a java file!");
                        continue;
                    }
                    if (Scene.class.isAssignableFrom(c))
                        allScenes.add((Scene) c.getDeclaredConstructor(Applet.class).newInstance(window));
                }
            }

        System.out.println(allScenes);
    }

    public static final List<String> destinationOnCircleLabel = Arrays.stream(destinationOnCircle).map(x -> {
        if (String.valueOf(x).length() > 3)
            return switch (String.valueOf(x).substring(0, 4)) {
                case "3.14", "-3.1" -> "PI";
                case "1.57" -> "PI/2";
                case "-1.5" -> "-PI/2";
                case "0.78" -> "PI/4";
                case "-0.7" -> "-PI/4";
                default -> String.valueOf(x).substring(0, 4);
            };
        return String.valueOf(x);
    }).collect(Collectors.toList());

    public static void directions() {
        //call the scenes here
        for (Scene s : allScenes) {
            if (!s.execute()) { // if scene hasn't completed
               /* Iterator<CanvasObject> it = s.getOrder().iterator();
                while (it.hasNext()){
                    CanvasObject holder = it.next();
                    if (holder.isRemoved())
                        it.remove();
                    else
                        holder.display();
                } */
                break;
            }

        }

    }
}
