package thepaperpilot.rpg.Areas;

import thepaperpilot.rpg.Context;
import thepaperpilot.rpg.Map.Area;

public class Intro extends Area.AreaPrototype {
    private static final Intro instance = new Intro();

    public static Context getContext() {
        return new Context(instance);
    }
}
