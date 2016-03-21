package thepaperpilot.rpg.Systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import thepaperpilot.rpg.Components.PositionComponent;
import thepaperpilot.rpg.Components.Triggers.EnterZoneComponent;
import thepaperpilot.rpg.Components.Triggers.LeaveZoneComponent;
import thepaperpilot.rpg.Util.Mappers;

public class ZoneSystem extends IteratingSystem {
    public ZoneSystem() {
        super(Family.one(EnterZoneComponent.class, LeaveZoneComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        if (!getEngine().getSystem(PlayerControlledSystem.class).checkProcessing()) return;
        EnterZoneComponent ec = Mappers.enterZone.get(entity);
        LeaveZoneComponent lc = Mappers.leaveZone.get(entity);
        PositionComponent pc;
        if (ec != null) pc = Mappers.position.get(ec.area.player);
        else if (lc != null) pc = Mappers.position.get(lc.area.player);
        else return;

        if (ec != null) {
            if (ec.bounds.contains(pc.position)) {
                if (!ec.inside) {
                    ec.run(ec.area);
                    if (!ec.repeatable) entity.remove(EnterZoneComponent.class);
                    ec.inside = true;
                }
            } else {
                ec.inside = false;
            }
        }

        if (lc != null) {
            if (lc.bounds.contains(pc.position)) {
                lc.inside = true;
            } else {
                if (lc.inside) {
                    lc.run(lc.area);
                    if (!lc.repeatable) entity.remove(LeaveZoneComponent.class);
                    lc.inside = false;
                }
            }
        }
    }
}
