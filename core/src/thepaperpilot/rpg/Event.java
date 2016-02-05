package thepaperpilot.rpg;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import java.util.HashMap;
import java.util.Map;

public class Event {
    private final Area area;
    public Map<String, String> attributes;
    public Type type;
    public float wait;

    public Event(EventPrototype prototype, Area area) {
        this.area = area;
        type = Type.valueOf(prototype.type);
        attributes = prototype.attributes;
        wait = prototype.wait;
    }

    // You can override this for custom events
    public void run() {
        if (type == Type.DUMMY) return;

        area.ui.addAction(Actions.sequence(Actions.delay(wait), Actions.run(new Runnable() {
            @Override
            public void run() {
                switch (type) {
                    case MOVE_ENTITY:
                        Entity entity = area.entities.get(attributes.get("target"));
                        entity.target = new Vector2(Float.valueOf(attributes.get("x")), Float.valueOf(attributes.get("y")));
                        break;
                    case DIALOGUE:
                        area.talk(attributes.get("target"));
                        break;
                    case MOVE_CAMERA:
                        area.capture = true;
                        area.cameraTarget = new Vector3(Float.valueOf(attributes.get("x")), Float.valueOf(attributes.get("y")), 0);
                        area.zoomTarget = Float.valueOf(attributes.get("zoom"));
                        break;
                    case RELEASE_CAMERA:
                        area.capture = false;
                        break;
                }
            }
        })));
    }

    public enum Type {
        MOVE_ENTITY,
        DIALOGUE,
        MOVE_CAMERA,
        RELEASE_CAMERA,
        DUMMY
    }

    public static class EventPrototype {
        public String type;
        public Map<String, String> attributes = new HashMap<String, String>();
        public float wait = 0;
    }
}
