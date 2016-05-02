package thepaperpilot.rpg.Systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.BatchTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import thepaperpilot.rpg.Screens.Area;
import thepaperpilot.rpg.Util.Constants;
import thepaperpilot.rpg.Util.Mappers;

public class TiledMapSystem extends EntitySystem {
    private final Area area;

    public TiledMap tiledMap;
    public Viewport viewport;
    private TiledMapRenderer tiledMapRenderer;
    public OrthographicCamera camera;

    public TiledMapSystem(Area area) {
        super(15);
        this.area = area;
    }

    @Override
    public void addedToEngine(Engine engine) {
        tiledMap = new TmxMapLoader().load("maps/" + area.prototype.name + ".tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        camera = new OrthographicCamera();
        viewport = new ExtendViewport(area.prototype.viewport.x, area.prototype.viewport.y, camera);
        viewport.apply();
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);

        tiledMap.getLayers().get("collisions").setVisible(false);
    }

    public void update(float delta) {
        updateCamera(delta);

        if (camera.zoom != area.zoomTarget) {
            if (Math.abs(camera.zoom - area.zoomTarget) < delta) {
                camera.zoom = area.zoomTarget;
            } else {
                if (camera.zoom > area.zoomTarget) camera.zoom -= delta;
                else camera.zoom += delta;
            }
        }

        area.prototype.tint.a = area.mapActors.getRoot().getColor().a = area.stage.getRoot().getColor().a;
        ((BatchTiledMapRenderer) tiledMapRenderer).getBatch().setColor(area.prototype.tint);

        Gdx.gl.glClearColor(area.prototype.tint.r == 1 ? .2f : 0, area.prototype.tint.g == 1 ? .2f : 0, area.prototype.tint.b == 1 ? .2f : 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        tiledMapRenderer.setView(camera);
        area.mapActors.setViewport(viewport);

        ((BatchTiledMapRenderer) tiledMapRenderer).getBatch().begin();
        for (MapLayer mapLayer : tiledMap.getLayers()) {
            if (mapLayer.getName().toLowerCase().equals("collisions") && !getEngine().getSystem(DebugSystem.class).checkProcessing()) continue;
            if (mapLayer.getName().toLowerCase().equals("player")) {
                ((BatchTiledMapRenderer) tiledMapRenderer).getBatch().end();
                area.mapActors.act();
                area.mapActors.draw();
                ((BatchTiledMapRenderer) tiledMapRenderer).getBatch().begin();
            }
            else if (mapLayer instanceof TiledMapTileLayer) tiledMapRenderer.renderTileLayer((TiledMapTileLayer) mapLayer);
            else if (mapLayer instanceof TiledMapImageLayer) tiledMapRenderer.renderImageLayer((TiledMapImageLayer) mapLayer);
        }
        ((BatchTiledMapRenderer) tiledMapRenderer).getBatch().end();
    }

    public void updateCamera(float delta) {
        switch (area.cameraState) {
            default:
            case ENTITY:
                if (area.entityTarget == null || !Mappers.position.has(area.entityTarget))
                    break;
                Vector2 pos = Mappers.position.get(area.entityTarget).position;
                moveCameraTowards(new Vector3(pos.x, pos.y, 0), delta);
                if (area.entityTarget == area.entities.get("player")) clampCamera();
                break;
            case LOCK:
                if (area.cameraTarget == null)
                    break;
                moveCameraTowards(area.cameraTarget, delta);
                break;
        }
    }

    private void moveCameraTowards(Vector3 targetPos, float delta) {
        if (!camera.position.equals(targetPos)) {
            if (camera.position.dst(targetPos) < 2 * Constants.MOVE_SPEED * delta) {
                camera.position.set(targetPos);
            } else {
                camera.translate(targetPos.cpy().sub(camera.position).nor().scl(2 * Constants.MOVE_SPEED * delta));
            }
        }
    }

    private void clampCamera() {
        if (camera.position.x < viewport.getWorldWidth() / 2f)
            camera.position.x = viewport.getWorldWidth() / 2f;
        if (camera.position.y < viewport.getWorldHeight() / 2f)
            camera.position.y = viewport.getWorldHeight() / 2f;
        if (camera.position.x > area.prototype.mapSize.x * Constants.TILE_SIZE - viewport.getWorldWidth() / 2f)
            camera.position.x = area.prototype.mapSize.x * Constants.TILE_SIZE - viewport.getWorldWidth() / 2f;
        if (camera.position.y > area.prototype.mapSize.y * Constants.TILE_SIZE - viewport.getWorldHeight() / 2f)
            camera.position.y = area.prototype.mapSize.y * Constants.TILE_SIZE - viewport.getWorldHeight() / 2f;
    }

    public enum CAMERA_STATES {
        LOCK,
        ENTITY
    }
}
