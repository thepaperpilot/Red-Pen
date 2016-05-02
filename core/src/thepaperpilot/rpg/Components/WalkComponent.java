package thepaperpilot.rpg.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;

public class WalkComponent implements Component {
    public float speed;

    public Animation left;
    public Animation right;
    public Animation up;
    public Animation down;
}
