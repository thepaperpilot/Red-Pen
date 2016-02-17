package thepaperpilot.rpg;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import java.util.HashMap;
import java.util.Map;

public class Event {
    public final Map<String, String> attributes;
    public final Type type;
    private float wait;

    public Event(Type type) {
        this.type = type;
        attributes = new HashMap<String, String>();
    }

    public Event(Type type, String target) {
        this(type);
        attributes.put("target", target);
    }

    public Event(Type type, int time) {
        this(type);
        wait = time;
    }

    public Event(Type type, String target, int time) {
        this(type, target);
        wait = time;
    }

    // You can override this for custom events
    public void run(final Context context) {
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
        LOCK_CAMERA,
        ENTITY_CAMERA,
        RELEASE_CAMERA,
        COMBAT,
        SET_ENTITY_VISIBILITY,
        SET_ATTACK,
        END_BATTLE,
        CHANGE_CONTEXT,
        RESUME_ATTACK,
        NEXT_ATTACK,
        CUTSCENE,
        END_CUTSCENE,
        HEAL_PLAYER,
        ADD_ATTRIBUTE,
        SAVE,
        TITLE,
        SHUTDOWN,
        DUMMY
    }
}
