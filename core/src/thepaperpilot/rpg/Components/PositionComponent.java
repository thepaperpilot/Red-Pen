package thepaperpilot.rpg.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class PositionComponent implements Component {
    public final Vector2 position = new Vector2();
    public float angle = 180; // down

    public PositionComponent(float x, float y) {
        position.set(x, y);
    }

    public PositionComponent(Vector2 pos) {
        position.set(pos);
    }
}
