package thepaperpilot.rpg.Systems;

import com.badlogic.ashley.core.EntitySystem;
import thepaperpilot.rpg.Events.Event;
import thepaperpilot.rpg.Screens.Context;
import thepaperpilot.rpg.Util.Constants;

public class EventSystem extends EntitySystem {
    private final Context context;

    public EventSystem(Context context) {
        this.context = context;
    }

    public void update (float delta) {
        for (int i = 0; i < context.events.size();) {
            Event next = context.events.get(i);
            next.delay -= delta;
            if (next.delay <= 0) {
                if (Constants.DEBUG)
                    System.out.println(next.getClass().getName());
                next.run(context);
                context.events.remove(i);
            } else i++;
        }
    }
}
