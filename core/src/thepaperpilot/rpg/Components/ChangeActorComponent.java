package thepaperpilot.rpg.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ChangeActorComponent implements Component {
    public final Actor actor;

    public ChangeActorComponent(Actor actor) {
        this.actor = actor;
    }
}
