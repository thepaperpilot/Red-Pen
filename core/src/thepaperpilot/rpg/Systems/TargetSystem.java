package thepaperpilot.rpg.Systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import thepaperpilot.rpg.Components.ActorComponent;
import thepaperpilot.rpg.Components.PositionComponent;
import thepaperpilot.rpg.Components.Triggers.TargetComponent;
import thepaperpilot.rpg.Util.Mappers;

public class TargetSystem extends IteratingSystem{
    public TargetSystem() {
        super(Family.all(ActorComponent.class, PositionComponent.class, TargetComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        ActorComponent ac = Mappers.actor.get(entity); // just needed for the area reference
        PositionComponent pc = Mappers.position.get(entity);
        TargetComponent tc = Mappers.target.get(entity);

        Vector2 pos = pc.position;
        if (pos.dst(tc.target) < tc.speed * delta) {
            pos.set(tc.target);
            tc.run(ac.area);
            entity.remove(TargetComponent.class);
        } else {
            pos.add(tc.target.cpy().sub(pos).nor().scl(tc.speed * delta));
        }
    }
}
