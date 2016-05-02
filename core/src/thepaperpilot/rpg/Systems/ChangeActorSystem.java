package thepaperpilot.rpg.Systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import thepaperpilot.rpg.Components.ActorComponent;
import thepaperpilot.rpg.Components.AreaComponent;
import thepaperpilot.rpg.Components.ChangeActorComponent;
import thepaperpilot.rpg.Util.Mappers;

public class ChangeActorSystem extends IteratingSystem{
    public ChangeActorSystem() {
        super(Family.all(ActorComponent.class, ChangeActorComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        ActorComponent ac = Mappers.actor.get(entity);
        AreaComponent area = Mappers.area.get(entity);
        ChangeActorComponent cc = Mappers.changeActor.get(entity);

        ac.actor.remove();
        ac.actor = cc.actor;
        area.area.mapActors.addActor(ac.actor);

        entity.remove(ChangeActorComponent.class);
    }
}
