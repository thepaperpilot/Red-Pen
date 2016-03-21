package thepaperpilot.rpg.Events;

import com.badlogic.gdx.graphics.OrthographicCamera;
import thepaperpilot.rpg.Area;
import thepaperpilot.rpg.Components.PositionComponent;
import thepaperpilot.rpg.Context;
import thepaperpilot.rpg.Systems.TiledMapSystem;
import thepaperpilot.rpg.Util.Mappers;

public class EntityCamera extends Event {
    private String entity = "";
    private float zoom = 1;
    private boolean instant = false;

    public EntityCamera(String entity, float zoom, boolean instant) {
        this.entity = entity;
        this.zoom = zoom;
        this.instant = instant;
    }

    @Override
    public void run(Context context) {
        if (!(context instanceof Area)) return;
        Area area = ((Area) context);
        if (!area.entities.containsKey(entity)) return;
        area.cameraState = TiledMapSystem.CAMERA_STATES.ENTITY;
        area.entityTarget = area.entities.get(entity);
        area.zoomTarget = zoom;
        if (instant) {
            PositionComponent pc = Mappers.position.get(area.entityTarget);
            OrthographicCamera camera = area.engine.getSystem(TiledMapSystem.class).camera;
            camera.position.set(pc.position.x, pc.position.y, 0);
            camera.zoom = area.zoomTarget;
        }
        // Possible to make this only do it once camera stops moving?
        runNext(context);
    }
}
