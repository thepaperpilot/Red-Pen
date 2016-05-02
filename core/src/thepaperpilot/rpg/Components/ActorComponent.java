package thepaperpilot.rpg.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ActorComponent implements Component {
    public Actor actor = new Actor();
    public boolean front = false;
    public Vector2 offset = new Vector2();

    public ActorComponent() {

    }

    public ActorComponent(Actor actor) {
        this.actor = actor;
    }
}
