package thepaperpilot.rpg.Listeners;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import thepaperpilot.rpg.Area;
import thepaperpilot.rpg.Components.NameComponent;
import thepaperpilot.rpg.Util.Mappers;

public class NameListener implements EntityListener {

    private final Area area;

    public NameListener(Area area) {
        this.area = area;
    }

    @Override
    public void entityAdded(Entity entity) {
        NameComponent nc = Mappers.name.get(entity);

        area.entities.put(nc.name, entity);
    }

    @Override
    public void entityRemoved(Entity entity) {
        area.entities.remove(entity);
    }
}
