package thepaperpilot.rpg.Systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import thepaperpilot.rpg.Components.ActorComponent;
import thepaperpilot.rpg.Components.IdleComponent;
import thepaperpilot.rpg.Components.PositionComponent;
import thepaperpilot.rpg.Components.Triggers.TargetComponent;
import thepaperpilot.rpg.Components.WalkComponent;
import thepaperpilot.rpg.Util.Mappers;

public class WalkSystem extends IteratingSystem {
    public WalkSystem() {
        super(Family.all(WalkComponent.class, PositionComponent.class, ActorComponent.class).get(), 10);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        WalkComponent wc = Mappers.walk.get(entity);
        PositionComponent pc = Mappers.position.get(entity);
        ActorComponent ac = Mappers.actor.get(entity);

        if (Mappers.target.has(entity)) {
            TargetComponent tc = Mappers.target.get(entity);

            Vector2 diff = tc.target.cpy().sub(pc.position);
            updateFacing(entity, diff, deltaTime);
        }

        if (wc.facing != WalkComponent.STILL) {
            TextureRegion frame = wc.getAnimation(wc.facing).getKeyFrame(wc.time);
            ((Image) ac.actor).setDrawable(new TextureRegionDrawable(frame));
        }
    }

    public static void updateFacing(Entity entity, Vector2 diff, float deltaTime) {
        WalkComponent wc = Mappers.walk.get(entity);
        ActorComponent ac = Mappers.actor.get(entity);

        float angle = diff.angle();
        int facing;
        if (diff.isZero())
            facing = WalkComponent.STILL;
        else if (angle > 45 && angle <= 135) {
            facing = WalkComponent.UP;
        } else if (angle > 135 && angle <= 225) {
            facing = WalkComponent.LEFT;
        } else if (angle > 225 && angle <= 315) {
            facing = WalkComponent.DOWN;
        } else
            facing = WalkComponent.RIGHT;

        if (facing == wc.facing && facing != WalkComponent.STILL) {
            wc.time += deltaTime;
        } else {
            wc.time = 0;
            if (facing != WalkComponent.STILL) {
                ac.offset.add(ac.actor.getWidth() / 2, ac.actor.getHeight() / 2);
                TextureRegion frame = wc.getAnimation(facing).getKeyFrame(wc.time);
                ((Image) ac.actor).setDrawable(new TextureRegionDrawable(frame));
                ac.actor.setSize(frame.getRegionWidth(), frame.getRegionHeight());
                ac.offset.sub(ac.actor.getWidth() / 2, ac.actor.getHeight() / 2);
            } else if (facing != wc.facing) {
                ac.offset.add(ac.actor.getWidth() / 2, ac.actor.getHeight() / 2);
                TextureRegion frame = new TextureRegion(wc.still);
                if (wc.facing == WalkComponent.UP || wc.facing == WalkComponent.LEFT) {
                    frame.flip(true, false);
                }
                ((Image) ac.actor).setDrawable(new TextureRegionDrawable(frame));
                ac.actor.setSize(frame.getRegionWidth(), frame.getRegionHeight());
                ac.offset.sub(ac.actor.getWidth() / 2, ac.actor.getHeight() / 2);
            }
            wc.facing = facing;
        }

        if (Mappers.idle.has(entity)) {
            IdleComponent ic = Mappers.idle.get(entity);
            ic.time = 0;
            ic.idle = false;
        }
    }
}
