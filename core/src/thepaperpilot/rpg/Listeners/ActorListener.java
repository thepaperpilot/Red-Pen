package thepaperpilot.rpg.Listeners;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import thepaperpilot.rpg.Components.ActorComponent;
import thepaperpilot.rpg.Util.Mappers;

public class ActorListener implements EntityListener {

    private Stage stage;

    public ActorListener(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void entityAdded(Entity entity) {
        ActorComponent ac = Mappers.actor.get(entity);

        stage.addActor(ac.actor);

        if (!ac.front) ac.actor.toBack();
    }

    @Override
    public void entityRemoved(Entity entity) {
        ActorComponent ac = Mappers.actor.get(entity);

        ac.actor.remove();
    }
}
