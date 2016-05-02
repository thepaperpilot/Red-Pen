package thepaperpilot.rpg.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import thepaperpilot.rpg.Util.Constants;

public class IdleComponent implements Component{
    public boolean idle;
    public Animation animation;
    public float time;
    public float chance = Constants.IDLE_CHANCE;
}
