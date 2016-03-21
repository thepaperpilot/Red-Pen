package thepaperpilot.rpg.Components.Triggers;

import com.badlogic.gdx.math.Rectangle;

public class CollisionComponent extends TriggerComponent {
    public Rectangle bounds = new Rectangle();

    public CollisionComponent() {

    }

    public CollisionComponent(float x, float y, float width, float height) {
        this(new Rectangle(x, y, width, height));
    }

    public CollisionComponent(Rectangle rect) {
        bounds = rect;
    }
}
