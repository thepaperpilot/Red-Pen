package thepaperpilot.rpg.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.scenes.scene2d.Actor;
import thepaperpilot.rpg.Area;

public class ActorComponent implements Component {
    public Actor actor = new Actor();
    public final Area area;

    public ActorComponent(Area area) {
        this.area = area;
    }

    public ActorComponent(Area area, Actor actor) {
        this(area);
        this.actor = actor;
    }
}
