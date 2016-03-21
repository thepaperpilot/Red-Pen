package thepaperpilot.rpg.Events;

import thepaperpilot.rpg.Context;

import java.util.ArrayList;

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
