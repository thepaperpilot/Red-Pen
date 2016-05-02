package thepaperpilot.rpg.Systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import thepaperpilot.rpg.Components.ActorComponent;
import thepaperpilot.rpg.Components.IdleComponent;
import thepaperpilot.rpg.Components.WalkComponent;
import thepaperpilot.rpg.Util.Mappers;

public class IdleSystem extends IteratingSystem {
    public IdleSystem() {
        super(Family.all(IdleComponent.class, ActorComponent.class).get(), 5);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        IdleComponent ic = Mappers.idle.get(entity);
        ActorComponent ac = Mappers.actor.get(entity);

        if (ic.idle && (!Mappers.walk.has(entity) || Mappers.walk.get(entity).facing == WalkComponent.STILL)) {
            ic.time += deltaTime;
            if (ic.animation.isAnimationFinished(ic.time)) {
                ic.idle = false;
                ic.time = 0;
            }
            TextureRegion frame = ic.animation.getKeyFrame(ic.time);
            ((Image) ac.actor).setDrawable(new TextureRegionDrawable(frame));
            ac.actor.setSize(frame.getRegionWidth(), frame.getRegionHeight());
        } else if (MathUtils.randomBoolean(ic.chance)) {
            ic.idle = true;
        }
    }
}

