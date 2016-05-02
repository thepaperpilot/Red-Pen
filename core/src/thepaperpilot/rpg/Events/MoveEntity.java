package thepaperpilot.rpg.Events;

import com.badlogic.ashley.core.Entity;
import thepaperpilot.rpg.Components.PositionComponent;
import thepaperpilot.rpg.Components.Triggers.TargetComponent;
import thepaperpilot.rpg.Screens.Area;
import thepaperpilot.rpg.Screens.Context;
import thepaperpilot.rpg.Systems.PlayerSystem;
import thepaperpilot.rpg.Util.Constants;
import thepaperpilot.rpg.Util.Mappers;

public class MoveEntity extends Event {
    private String entity = "";
    private float x = 0;
    private float y = 0;
    private final float speed = Constants.MOVE_SPEED;
    private boolean instant = false;

    public MoveEntity(String entity, float x, float y, boolean instant) {
        this.entity = entity;
        this.x = x;
        this.y = y;
        this.instant = instant;
    }

    public void run(Context context) {
        if (!(context instanceof Area)) return;
        Area area = ((Area) context);
        if (!area.entities.containsKey(entity)) return;
        Entity entity = area.entities.get(this.entity);
        if (!Mappers.position.has(entity)) return;
        PositionComponent pc = Mappers.position.get(entity);
        if (instant) {
            pc.position.set(x, y);
            runNext(context);
        } else {
            TargetComponent tc = new TargetComponent();
            tc.target.set(x, y);
            tc.speed = speed;
            tc.events = chain;
            entity.add(tc);
            context.engine.getSystem(PlayerSystem.class).setProcessing(false);
        }
    }
}
