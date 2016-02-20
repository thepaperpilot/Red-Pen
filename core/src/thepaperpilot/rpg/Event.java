package thepaperpilot.rpg;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import thepaperpilot.rpg.Map.Area;

import java.util.HashMap;
import java.util.Map;

public class Event {
    public final Map<String, String> attributes;
    public final Type type;
    public float wait;
    public Event[] next;

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
                if (context.run(Event.this)) {
                    runNext(context);
                }
            }
        })));
    }

    public void runNext(Context context) {
        if (next == null) return;
        for (Event event : next) {
            context.run(event);
        }
    }

    public enum Type {
        MOVE_ENTITY,
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

    public static Event moveEvent(Vector2 start, Vector2 end, Area area) {
        Event startPlayer = new Event(Event.Type.MOVE_ENTITY, "player");
        startPlayer.attributes.put("x", "" + start.x);
        startPlayer.attributes.put("y", "" + start.y);
        startPlayer.attributes.put("instant", "true");
        Event endPlayer = new Event(Event.Type.MOVE_ENTITY, "player");
        endPlayer.attributes.put("x", "" + end.x);
        endPlayer.attributes.put("y", "" + end.y);
        Event release = new Event(Type.RELEASE_CAMERA);
        release.attributes.put("instant", "true");
        startPlayer.next = new Event[]{endPlayer, release};
        startPlayer.run(area);
        area.cutscene = true;
        Player.setArea(area.prototype.name);
        Player.save(end.x, end.y);
        return endPlayer;
    }
}
