package thepaperpilot.rpg;

import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;
import java.util.Map;

public class Event {
    private final Area area;
    public Map<String, String> attributes;
    public Type type;

    public Event(EventPrototype prototype, Area area) {
        this.area = area;
        type = Type.valueOf(prototype.type);
        attributes = prototype.attributes;
    }

    // You can override this for custom events
    public void run() {
        switch (type) {
            case MOVE_ENTITY:
                Entity entity = area.entities.get(attributes.get("target"));
                entity.target = new Vector2(Float.valueOf(attributes.get("x")), Float.valueOf(attributes.get("y")));
                break;
            case DIALOGUE:
                area.talk(attributes.get("target"));
                break;
        }
    }

    public enum Type {
        MOVE_ENTITY,
        DIALOGUE,
        DUMMY
    }

    public static class EventPrototype {
        public String type;
        public Map<String, String> attributes = new HashMap<String, String>();
    }
}
