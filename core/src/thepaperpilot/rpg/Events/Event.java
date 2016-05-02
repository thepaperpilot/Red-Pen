package thepaperpilot.rpg.Events;

import thepaperpilot.rpg.Screens.Context;

import java.util.ArrayList;

// TODO remove events completely
public class Event {
    public float delay = 0;
    public final ArrayList<Event> chain = new ArrayList<Event>();

    public void run(Context context) {
        runNext(context);
    }

    void runNext(Context context) {
        context.events.addAll(chain);
    }
}
