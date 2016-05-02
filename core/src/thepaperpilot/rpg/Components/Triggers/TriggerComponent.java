package thepaperpilot.rpg.Components.Triggers;

import com.badlogic.ashley.core.Component;
import thepaperpilot.rpg.Events.Event;
import thepaperpilot.rpg.Screens.Context;

import java.util.ArrayList;

public class TriggerComponent implements Component {
    public ArrayList<Event> events = new ArrayList<Event>();

    public void run(Context context) {
        context.events.addAll(events);
    }
}
