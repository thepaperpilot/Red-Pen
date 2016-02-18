package thepaperpilot.rpg.Map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.math.Vector2;
import thepaperpilot.rpg.Main;

public class Entity extends TextureMapObject {
    public final String name;
    public String image;
    public final boolean walkable;
    public Vector2 target;

    public Entity(String name, String image, float x, float y, boolean visible, boolean walkable) {
        this.name = name;
        this.image = image;
        this.walkable = walkable;

        setX(Main.TILE_SIZE * (x / Main.TILE_SIZE));
        setY(Main.TILE_SIZE * (y / Main.TILE_SIZE));
        setVisible(visible);
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
}
