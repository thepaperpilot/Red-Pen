package thepaperpilot.rpg.Map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.math.Vector2;
import thepaperpilot.rpg.Event;
import thepaperpilot.rpg.Main;

public class Entity extends TextureMapObject {
    public final String name;
    public String image;
    public final boolean walkable;
    public Vector2 target;
    public Vector2 position;
    public Event[] targetEvents;

    public Entity(String name, String image, float x, float y, boolean visible, boolean walkable) {
        this.name = name;
        this.image = image;
        this.walkable = walkable;

        setX(Main.TILE_SIZE * (x / Main.TILE_SIZE));
        setY(Main.TILE_SIZE * (y / Main.TILE_SIZE));
        setVisible(visible);
        position = new Vector2(getX(), getY());
    }

    public void init() {
        setTextureRegion(new TextureRegion(Main.getTexture(image)));
    }

    public void onTouch(Area area) {

    }

    public void changeTexture(String image) {
        this.image = image;
        init();
    }

    public void act(float delta, Area area) {
        if (target != null) {
            if (position.dst(target) < Main.MOVE_SPEED * delta) {
                setX(target.x);
                setY(target.y);
                position.set(target);

                target = null;
                Event[] temp = targetEvents;
                targetEvents = null;
                if(temp != null && temp.length != 0) {
                    for (Event event : temp)
                        event.run(area);
                } else area.cutscene = false;
            } else {
                position.add(target.cpy().sub(position).nor().scl(Main.MOVE_SPEED * delta));
                setX(position.x);
                setY(position.y);
            }
        }
    }
}
