package thepaperpilot.rpg.Listeners;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import thepaperpilot.rpg.Components.ActorComponent;
import thepaperpilot.rpg.Components.IdleComponent;
import thepaperpilot.rpg.Util.Mappers;

public class IdleListener implements EntityListener {

    @Override
    public void entityAdded(Entity entity) {
        ActorComponent ac = Mappers.actor.get(entity);
        IdleComponent ic = Mappers.idle.get(entity);

        TextureRegion frame = ic.animation.getKeyFrame(0);
        ((Image) ac.actor).setDrawable(new TextureRegionDrawable(frame));
        ac.actor.setSize(frame.getRegionWidth(), frame.getRegionHeight());

        if (!ac.front) ac.actor.toBack();
    }

    @Override
    public void entityRemoved(Entity entity) {
        ActorComponent ac = Mappers.actor.get(entity);

        ac.actor.remove();
    }
}
