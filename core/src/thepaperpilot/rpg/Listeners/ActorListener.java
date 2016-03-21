package thepaperpilot.rpg.Listeners;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import thepaperpilot.rpg.Components.ActorComponent;
import thepaperpilot.rpg.Util.Mappers;

public class ActorListener implements EntityListener {
    @Override
    public void entityAdded(Entity entity) {
        ActorComponent ac = Mappers.actor.get(entity);

        ac.area.mapActors.addActor(ac.actor);
    }

    @Override
    public void entityRemoved(Entity entity) {
        ActorComponent ac = Mappers.actor.get(entity);

        ac.actor.remove();
    }
}
