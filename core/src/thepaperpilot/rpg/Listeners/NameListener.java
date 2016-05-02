package thepaperpilot.rpg.Listeners;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import thepaperpilot.rpg.Components.NameComponent;
import thepaperpilot.rpg.Screens.Area;
import thepaperpilot.rpg.Util.Constants;
import thepaperpilot.rpg.Util.Mappers;

public class NameListener implements EntityListener {

    private final Area area;

    public NameListener(Area area) {
        this.area = area;
    }

    @Override
    public void entityAdded(Entity entity) {
        NameComponent nc = Mappers.name.get(entity);

        if (Constants.DEBUG) {
            System.out.println("adding " + nc.name);
        }

        area.entities.put(nc.name, entity);
    }

    @Override
    public void entityRemoved(Entity entity) {
        area.entities.remove(entity);

        if (Constants.DEBUG) {
            System.out.println("removing " + Mappers.name.get(entity).name);
        }
    }
}
