package thepaperpilot.rpg.Events;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import thepaperpilot.rpg.Area;
import thepaperpilot.rpg.Context;
import thepaperpilot.rpg.Systems.TiledMapSystem;
import thepaperpilot.rpg.Util.Mappers;

public class ReleaseCamera extends Event {
    private boolean instant = false;

    public ReleaseCamera() {

    }

    public ReleaseCamera(boolean instant) {
        this.instant = instant;
    }

    @Override
    public void run(Context context) {
        if (!(context instanceof Area)) return;
        Area area = ((Area) context);
        area.cameraState = TiledMapSystem.CAMERA_STATES.ENTITY;
        area.entityTarget = area.player;
        area.zoomTarget = 1;
        if (instant) {
            OrthographicCamera camera = area.engine.getSystem(TiledMapSystem.class).camera;
            Vector2 position = Mappers.position.get(area.player).position;
            camera.position.set(position.x, position.y, 0);
            camera.zoom = 1;
        }
        runNext(context);
    }
}
