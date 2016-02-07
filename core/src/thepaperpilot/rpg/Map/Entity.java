package thepaperpilot.rpg.Map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.math.Vector2;
import thepaperpilot.rpg.Main;

public class Entity extends TextureMapObject {
    public final EntityPrototype prototype;
    public final Area area;
    public Vector2 target;

    public Entity(EntityPrototype prototype, Area area) {
        super(new TextureRegion(Main.getTexture(prototype.image)));

        this.prototype = prototype;
        this.area = area;

        setX(Main.TILE_SIZE * (prototype.x / Main.TILE_SIZE));
        setY(Main.TILE_SIZE * (prototype.y / Main.TILE_SIZE));
        setVisible(prototype.visible);
    }

    public void onTouch() {
        if (isVisible()) prototype.onTouch(this);
    }

    public static class EntityPrototype {
        final String name;
        final String image;
        final boolean visible;
        final int x;
        final int y;

        public EntityPrototype(String name, String image, int x, int y, boolean visible) {
            this.name = name;
            this.image = image;
            this.visible = visible;
            this.x = x;
            this.y = y;
        }

        public void onTouch(Entity entity) {

        }
    }
}
