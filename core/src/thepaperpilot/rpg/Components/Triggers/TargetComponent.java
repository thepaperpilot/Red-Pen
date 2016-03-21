package thepaperpilot.rpg.Components.Triggers;

import com.badlogic.gdx.math.Vector2;
import thepaperpilot.rpg.Util.Constants;

public class TargetComponent extends TriggerComponent {
    public float speed = Constants.MOVE_SPEED;
    public final Vector2 target = new Vector2();
}
