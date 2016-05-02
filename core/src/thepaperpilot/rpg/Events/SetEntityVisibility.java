package thepaperpilot.rpg.Events;

import com.badlogic.ashley.core.Entity;
import thepaperpilot.rpg.Components.VisibleComponent;
import thepaperpilot.rpg.Screens.Area;
import thepaperpilot.rpg.Screens.Context;

public class SetEntityVisibility extends Event {
    private String entity = "";
    private boolean visible = false;

    public SetEntityVisibility(String entity, boolean visible) {
        this.entity = entity;
        this.visible = visible;
    }

    @Override
    public void run(Context context) {
        if (!(context instanceof Area)) return;
        Area area = ((Area) context);
        if (!area.entities.containsKey(entity)) return;
        Entity entity = area.entities.get(this.entity);
        if (visible) {
            entity.add(new VisibleComponent());
        } else entity.remove(VisibleComponent.class);
        runNext(context);
    }
}
