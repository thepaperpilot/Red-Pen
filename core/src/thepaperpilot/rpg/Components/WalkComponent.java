package thepaperpilot.rpg.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class WalkComponent implements Component {
    public float speed;
    public int facing;
    public float time;

    public TextureRegion still;
    public Animation left;
    public Animation right;
    public Animation up;
    public Animation down;

    public static final int STILL = 0;
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    public static final int UP = 3;
    public static final int DOWN = 4;

    public Animation getAnimation(int facing) {
        switch (facing) {
            default:case STILL:
                return null;
            case LEFT:
                return left;
            case RIGHT:
                return right;
            case UP:
                return up;
            case DOWN:
                return down;
        }
    }
}
