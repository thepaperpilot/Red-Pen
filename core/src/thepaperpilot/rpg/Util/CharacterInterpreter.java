package thepaperpilot.rpg.Util;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import thepaperpilot.rpg.Components.*;
import thepaperpilot.rpg.Components.Triggers.CollisionComponent;
import thepaperpilot.rpg.Main;
import thepaperpilot.rpg.Screens.Area;

import java.util.ArrayList;
import java.util.Collections;

public class CharacterInterpreter {
    private static JsonReader json = new JsonReader();

    public static Entity readData(String filename, Area area, Vector2 position) {
        Entity character = new Entity();
        JsonValue value = json.parse(Gdx.files.internal("characters/" + filename + ".json"));

        character.add(new ActorComponent(new Image()));
        character.add(new AreaComponent(area));
        character.add(new PositionComponent(position));
        character.add(new NameComponent(value.getString("name")));
        if (value.getBoolean("visible")) character.add(new VisibleComponent());

        JsonValue components = value.get("components");
        for (int i = 0; i < components.size; i++) {
            character.add(getComponent(components.get(i)));
        }

        return character;
    }

    private static Component getComponent(JsonValue component) {
        // I'm trying to keep java 6, so no switching strings for me
        String type = component.getString("type");
        if (type.equals("player")) {
            return new PlayerComponent();
        } else if (type.equals("idle")) {
            return getIdleComponent(component);
        } else if (type.equals("collision")) {
            return getCollisionComponent(component);
        } else if (type.equals("walk")) {
            return getWalkComponent(component);
        } else {
            return null;
        }
    }

    private static Component getIdleComponent(JsonValue component) {
        IdleComponent idleComponent = new IdleComponent();
        idleComponent.animation = getAnimation(component, Main.getTexture("characters/" + component.getString("file")));
        return idleComponent;
    }

    private static Component getCollisionComponent(JsonValue component) {
        int xoffset = component.getInt("xoffset");
        int yoffset = component.getInt("yoffset");
        int width = component.getInt("width");
        int height = component.getInt("height");
        return new CollisionComponent(xoffset, yoffset, width, height);
    }

    private static Component getWalkComponent(JsonValue component) {
        WalkComponent walkComponent = new WalkComponent();
        walkComponent.speed = component.getFloat("speed");
        JsonValue animations = component.get("animations");
        Texture texture = Main.getTexture("characters/" + component.getString("file"));
        walkComponent.still = new TextureRegion(texture, component.getInt("x"), component.getInt("y"), component.getInt("width"), component.getInt("height"));
        for (int i = 0; i < animations.size; i++) {
            String name = animations.get(i).getString("name");
            if (name.equals("left")) {
                walkComponent.left = getAnimation(animations.get(i), texture);
                walkComponent.left.setPlayMode(Animation.PlayMode.LOOP);
            } else if (name.equals("right")) {
                walkComponent.right = getAnimation(animations.get(i), texture);
                walkComponent.right.setPlayMode(Animation.PlayMode.LOOP);
            } else if (name.equals("up")) {
                walkComponent.up = getAnimation(animations.get(i), texture);
                walkComponent.up.setPlayMode(Animation.PlayMode.LOOP);
            } else if (name.equals("down")) {
                walkComponent.down = getAnimation(animations.get(i), texture);
                walkComponent.down.setPlayMode(Animation.PlayMode.LOOP);
            }
        }
        return walkComponent;
    }

    private static Animation getAnimation(JsonValue value, Texture texture) {
        ArrayList<TextureRegion> frames = new ArrayList<TextureRegion>();
        TextureRegion animSheet = new TextureRegion(texture,
                value.getInt("startx"),
                value.getInt("starty"),
                value.getInt("width") * value.getInt("columns"),
                value.getInt("height") * value.getInt("rows"));
        for (TextureRegion[] row : animSheet.split(value.getInt("width"), value.getInt("height"))) {
            Collections.addAll(frames, row);
        }

        if (value.has("flip") && value.getBoolean("flip")) {
            for (TextureRegion frame : frames) {
                frame.flip(true, false);
            }
        }

        return new Animation(Constants.ANIM_SPEED, frames.toArray(new TextureRegion[frames.size()]));
    }
}
