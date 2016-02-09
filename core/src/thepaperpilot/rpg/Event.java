package thepaperpilot.rpg;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import java.util.HashMap;
import java.util.Map;

public class Event {
    public final Map<String, String> attributes;
    public final Type type;
    private final Context context;
    private final float wait;

    public Event(EventPrototype prototype, Context context) {
        this.context = context;
        type = Type.valueOf(prototype.type);
        attributes = prototype.attributes;
        wait = prototype.wait;
    }

    // You can override this for custom events
    public void run() {
        if (type == Type.DUMMY) return;

        context.stage.addAction(Actions.sequence(Actions.delay(wait), Actions.run(new Runnable() {
            @Override
            public void run() {
                context.run(Event.this);
            }
        })));
    }

    public enum Type {
        MOVE_ENTITY,
        MOVE_PLAYER,
        DIALOGUE,
        MOVE_CAMERA,
        RELEASE_CAMERA,
        COMBAT,
        SET_ENTITY_VISIBILITY,
        SET_ATTACK,
        CHANGE_CONTEXT,
        RESUME_ATTACK,
        NEXT_ATTACK,
        CUTSCENE,
        END_CUTSCENE,
        HEAL_PLAYER,
        SHUTDOWN,
        DUMMY
    }

    public static class EventPrototype {
        public final Map<String, String> attributes = new HashMap<String, String>();
        public String type;
        public float wait = 0;
    }
}
