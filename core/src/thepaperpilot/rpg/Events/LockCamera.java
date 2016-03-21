package thepaperpilot.rpg.Events;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import thepaperpilot.rpg.Area;
import thepaperpilot.rpg.Context;
import thepaperpilot.rpg.Systems.TiledMapSystem;

public class LockCamera extends Event {
    private float x = 0;
    private float y = 0;
    private float zoom = 1;
    private boolean instant = false;

    @Override
    public void run(Context context) {
        if (!(context instanceof Area)) return;
        Area area = ((Area) context);
        area.cameraState = TiledMapSystem.CAMERA_STATES.LOCK;
        area.cameraTarget = new Vector3(x, y, 0);
        area.zoomTarget = zoom;
        if (instant) {
            OrthographicCamera camera = area.engine.getSystem(TiledMapSystem.class).camera;
            camera.position.set(area.cameraTarget);
            camera.zoom = area.zoomTarget;
        }
        // Possible to make this only do it once camera stops moving?
        runNext(context);
    }

    public LockCamera(float x, float y, float zoom, boolean instant) {
        this.x = x;
        this.y = y;
        this.zoom = zoom;
        this.instant = instant;
    }
}
