package thepaperpilot.rpg.Systems;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

// Cleans up empty entities
public class CleanupSystem extends IteratingSystem {

    public CleanupSystem() {
        // this doesn't work, unfortunately
        super(Family.exclude(Component.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (entity.getComponents().size() == 0)
            getEngine().removeEntity(entity);
    }
}
