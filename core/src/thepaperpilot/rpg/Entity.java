package thepaperpilot.rpg;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;
import java.util.Map;

public class Entity extends TextureMapObject {
    public final Map<String, String> attributes;
    Event[] events;
    String name;
    boolean visible;
    public Vector2 target;

    public Entity(EntityPrototype prototype, Area area) {
        super(new TextureRegion(Main.getTexture(prototype.image)));
        // create the entity from the prototype
        name = prototype.name;
        this.visible = prototype.visible;
        attributes = prototype.attributes;

        // load the effects for when entity touched
        if (prototype.events != null) {
            events = new Event[prototype.events.length];
            for (int i = 0; i < prototype.events.length; i++) {
                events[i] = new Event(prototype.events[i], area);
            }
        } else events = new Event[]{};

        setX(Main.TILE_SIZE * (prototype.x / Main.TILE_SIZE));
        setY(Main.TILE_SIZE * (prototype.y / Main.TILE_SIZE));
        setVisible(visible);
    }

    public void onTouch() {
        for (Event event : events) {
            event.run();
        }
    }

    public static class EntityPrototype {
        public Map<String, String> attributes = new HashMap<String, String>();
        String name;
        String image;
        boolean visible;
        int x;
        int y;
        Event.EventPrototype[] events;

        public EntityPrototype() {

        }

        public EntityPrototype(String name, String image, int x, int y, boolean visible) {
            this.name = name;
            this.image = image;
            this.visible = visible;
            this.x = x;
            this.y = y;
        }
    }
}
