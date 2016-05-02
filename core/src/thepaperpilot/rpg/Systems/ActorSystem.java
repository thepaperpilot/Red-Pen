package thepaperpilot.rpg.Systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import thepaperpilot.rpg.Components.ActorComponent;
import thepaperpilot.rpg.Components.PositionComponent;
import thepaperpilot.rpg.Util.Mappers;

import java.util.Comparator;

public class ActorSystem extends IteratingSystem {
    private final Stage stage;
    private static final Comparator<Actor> comparator = new Comparator<Actor>() {
        @Override
        public int compare(Actor actor, Actor t1) {
            return MathUtils.round(t1.getY() - actor.getY());
        }
    };

    public ActorSystem(Stage stage) {
        super(Family.all(ActorComponent.class, PositionComponent.class).get(), 12);
        this.stage = stage;
    }

    @Override
    public void update(float deltaTime) {
        stage.getActors().sort(comparator);
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        ActorComponent ac = Mappers.actor.get(entity);
        PositionComponent pc = Mappers.position.get(entity);

        ac.actor.setPosition(pc.position.x + ac.offset.x, pc.position.y + ac.offset.y);
        ac.actor.getColor().a = Mappers.visible.has(entity) ? 1: 0;

        if (Mappers.walkable.has(entity)) ac.actor.toBack();
    }
}
