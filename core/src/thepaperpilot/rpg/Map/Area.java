package thepaperpilot.rpg.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.BatchTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import thepaperpilot.rpg.Battles.Battle;
import thepaperpilot.rpg.Context;
import thepaperpilot.rpg.Event;
import thepaperpilot.rpg.Main;
import thepaperpilot.rpg.Player;
import thepaperpilot.rpg.UI.Menu;

import java.util.HashMap;
import java.util.Map;

public class Area extends Context implements InputProcessor {
    private final AreaPrototype prototype;
    private final TiledMap tiledMap;
    public final OrthographicCamera camera;
    private final Viewport viewport;
    private final TiledMapRenderer tiledMapRenderer;
    private final MapLayer objectLayer;
    public final Entity player;
    public final Map<String, Entity> entities = new HashMap<String, Entity>();
    private final Map<String, Battle.BattlePrototype> battles = new HashMap<String, Battle.BattlePrototype>();
    private Vector2 playerTarget;
    private Direction facing = Direction.UP;
    private Vector3 cameraTarget;
    private float zoomTarget = 1;
    private Entity entityTarget;
    private CAMERA_STATES cameraState = CAMERA_STATES.ENTITY;

    public Area(AreaPrototype prototype) {
        super(prototype);
        this.prototype = prototype;

        tiledMap = new TmxMapLoader().load(prototype.name + ".tmx");
        tiledMapRenderer = new OrthogonalTiledMapRendererWithSprites(tiledMap);

        camera = new OrthographicCamera();
        viewport = new ExtendViewport(prototype.viewport.x, prototype.viewport.y, camera);
        viewport.apply();
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);

        tiledMap.getLayers().get("collisions").setVisible(false);

        objectLayer = tiledMap.getLayers().get("player");

        for (int i = 0; i < prototype.entities.length; i++) {
            entities.put(prototype.entities[i].name, prototype.entities[i]);
            objectLayer.getObjects().add(prototype.entities[i]);
            prototype.entities[i].init();
        }

        entityTarget = player = new Entity("player", "player", prototype.playerPosition.x, prototype.playerPosition.y, true, true);
        entities.put("player", player);
        objectLayer.getObjects().add(player);
        player.init();

        for (int i = 0; i < prototype.battles.length; i++) {
            battles.put(prototype.battles[i].name, prototype.battles[i]);
        }

        stage.addListener(new InputListener() {
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE && stage.getActors().size == 0 && !cutscene && transition == null) {
                    Menu.open(Area.this);
                }
                return false;
            }
        });
    }

    public void run(final Event event) {
        switch (event.type) {
            case MOVE_ENTITY:
                Entity entity = entities.get(event.attributes.get("target"));
                entity.target = new Vector2(Float.valueOf(event.attributes.get("x")), Float.valueOf(event.attributes.get("y")));
                break;
            case MOVE_PLAYER:
                if (Boolean.valueOf(event.attributes.get("instant"))) {
                    player.setX(Float.valueOf(event.attributes.get("x")));
                    player.setY(Float.valueOf(event.attributes.get("y")));
                } else {
                    playerTarget = new Vector2(Float.valueOf(event.attributes.get("x")), Float.valueOf(event.attributes.get("y")));
                }
                break;
            case LOCK_CAMERA:
                cameraState = CAMERA_STATES.LOCK;
                cameraTarget = new Vector3(Float.valueOf(event.attributes.get("x")), Float.valueOf(event.attributes.get("y")), 0);
                if (event.attributes.containsKey("zoom"))
                    zoomTarget = Float.valueOf(event.attributes.get("zoom"));
                if (Boolean.valueOf(event.attributes.get("instant"))) {
                    camera.position.set(cameraTarget);
                    camera.zoom = zoomTarget;
                }
                break;
            case ENTITY_CAMERA:
                cameraState = CAMERA_STATES.ENTITY;
                entityTarget = entities.get(event.attributes.get("target"));
                if (event.attributes.containsKey("zoom"))
                    zoomTarget = Float.valueOf(event.attributes.get("zoom"));
                if (Boolean.valueOf(event.attributes.get("instant"))) {
                    camera.position.set(entityTarget.getX(), entityTarget.getY(), 0);
                    camera.zoom = zoomTarget;
                }
                break;
            case RELEASE_CAMERA:
                cameraState = CAMERA_STATES.ENTITY;
                entityTarget = entities.get("player");
                zoomTarget = 1;
                if (Boolean.valueOf(event.attributes.get("instant"))) {
                    camera.position.set(entityTarget.getX(), entityTarget.getY(), 0);
                    camera.zoom = zoomTarget;
                }
                break;
            case COMBAT:
                Gdx.input.setInputProcessor(stage);
                stage.addAction(Actions.sequence(transition = Actions.fadeOut(1), Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        transition = null;
                        Main.changeScreen(new Battle(battles.get(event.attributes.get("target")), Area.this));
                    }
                })));
                break;
            case SET_ENTITY_VISIBILITY:
                entity = entities.get(event.attributes.get("target"));
                entity.setVisible(Boolean.valueOf(event.attributes.get("visible")));
                break;
            default:
                super.run(event);
                break;
        }

    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, this));

        Player.setArea(prototype.name);
        Player.save();

        updateCamera(100);
    }

    @Override
    public void render(float delta) {
        if (!cutscene && stage.getKeyboardFocus() == null) {
            final boolean w = Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP);
            final boolean a = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT);
            final boolean s = Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN);
            final boolean d = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);
            float xVel = 0;
            float yVel = 0;
            float sqrt = 1.41421356f; // approximately sqrt(2)
            if (a && !d) {
                xVel = 1;
                facing = Direction.LEFT;
            } else if (d && !a) {
                xVel = -1;
                facing = Direction.RIGHT;
            }
            if (w && !s) {
                yVel = -1;
                facing = Direction.UP;
            } else if (s && !w) {
                yVel = 1;
                facing = Direction.DOWN;
            }
            if (xVel != 0 && yVel != 0) {
                xVel /= sqrt;
                yVel /= sqrt;
            }
            float newX = player.getX() - Main.MOVE_SPEED * xVel * delta;
            float newY = player.getY() - Main.MOVE_SPEED * yVel * delta;
            if (newX != player.getX() && walkable(newX, player.getY())) player.setX(newX);
            if (newY != player.getY() && walkable(player.getX(), newY)) player.setY(newY);
        }

        updateCamera(delta);

        if (camera.zoom != zoomTarget) {
            if (Math.abs(camera.zoom - zoomTarget) < delta) {
                camera.zoom = zoomTarget;
            } else {
                if (camera.zoom > zoomTarget) camera.zoom -= delta;
                else camera.zoom += delta;
            }
        }

        for (Entity entity : entities.values()) {
            Vector2 position = new Vector2(entity.getX(), entity.getY());
            if (entity.target != null && !entity.target.equals(position)) {
                if (position.dst(entity.target) < Main.MOVE_SPEED * delta) {
                    entity.setX(entity.target.x);
                    entity.setY(entity.target.y);
                } else {
                    position.add(entity.target.cpy().sub(position).nor().scl(Main.MOVE_SPEED * delta));
                    entity.setX(position.x);
                    entity.setY(position.y);
                }
            }
        }

        Vector2 position = new Vector2(player.getX(), player.getY());
        if (playerTarget != null && !playerTarget.equals(position)) {
            if (position.dst(playerTarget) < Main.MOVE_SPEED * delta) {
                player.setX(playerTarget.x);
                player.setY(playerTarget.y);
            } else {
                position.add(playerTarget.cpy().sub(position).nor().scl(Main.MOVE_SPEED * delta));
                player.setX(position.x);
                player.setY(position.y);
            }
        }

        if (transition != null && transition.getColor() != null) {
            Color color = prototype.tint;
            color.a = transition.getColor().a;
            ((BatchTiledMapRenderer) tiledMapRenderer).getBatch().setColor(color);
        } else ((BatchTiledMapRenderer) tiledMapRenderer).getBatch().setColor(prototype.tint);

        Gdx.gl.glClearColor(prototype.tint.r == 1 ? .2f : 0, prototype.tint.g == 1 ? .2f : 0, prototype.tint.b == 1 ? .2f : 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        super.render(delta);
    }

    private void updateCamera(float delta) {
        switch (cameraState) {
            default:
            case ENTITY:
                if (entityTarget == null)
                    break;
                moveCameraTowards(new Vector3((int) entityTarget.getX(), (int) entityTarget.getY(), 0), delta);
                if (entityTarget == entities.get("player")) clampCamera();
                break;
            case LOCK:
                if (cameraTarget == null)
                    break;
                moveCameraTowards(cameraTarget, delta);
                break;
        }
    }

    private void moveCameraTowards(Vector3 targetPos, float delta) {
        if (!camera.position.equals(targetPos)) {
            if (camera.position.dst(targetPos) < 2 * Main.MOVE_SPEED * delta) {
                camera.position.set(targetPos);
            } else {
                camera.translate(targetPos.cpy().sub(camera.position).nor().scl(2 * Main.MOVE_SPEED * delta));
            }
        }
    }

    private void clampCamera() {
        if (camera.position.x < viewport.getWorldWidth() / 2f)
            camera.position.x = viewport.getWorldWidth() / 2f;
        if (camera.position.y < viewport.getWorldHeight() / 2f)
            camera.position.y = viewport.getWorldHeight() / 2f;
        if (camera.position.x > prototype.mapSize.x * Main.TILE_SIZE - viewport.getWorldWidth() / 2f)
            camera.position.x = prototype.mapSize.x * Main.TILE_SIZE - viewport.getWorldWidth() / 2f;
        if (camera.position.y > prototype.mapSize.y * Main.TILE_SIZE - viewport.getWorldHeight() / 2f)
            camera.position.y = prototype.mapSize.y * Main.TILE_SIZE - viewport.getWorldHeight() / 2f;
    }

    public enum CAMERA_STATES {
        LOCK,
        ENTITY
    }

    private boolean walkable(float x, float y) {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (MapObject object : objectLayer.getObjects()) {
                    if (!(object instanceof Entity)) continue;
                    Entity entity = ((Entity) object);
                    if (!entity.isVisible()) continue;
                    if ((int) (entity.getX() / Main.TILE_SIZE) == (int) (i + x / Main.TILE_SIZE) && (int) (entity.getY() / Main.TILE_SIZE) == (int) (j + y / Main.TILE_SIZE)) {
                        if (entity.walkable) {
                            entity.onTouch(this);
                            continue;
                        }
                        return false;
                    }
                }
                TiledMapTileLayer.Cell cell = ((TiledMapTileLayer) tiledMap.getLayers().get("collisions")).getCell((int) (x + i * Main.TILE_SIZE) / Main.TILE_SIZE, (int) (y + j * Main.TILE_SIZE) / Main.TILE_SIZE);
                if (cell == null) continue;
                MapProperties properties = cell.getTile().getProperties();
                if (!(properties.containsKey("b") && properties.get("b").equals("1")))
                    return false;
            }
        }
        return true;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        updateCamera(100);
        super.resize(width, height);
    }

    @Override
    public void dispose() {
        tiledMap.dispose();
        super.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.E || keycode == Input.Keys.ENTER) {
            for (MapObject object : objectLayer.getObjects()) {
                if (!(object instanceof Entity))
                    continue;
                Entity entity = ((Entity) object);
                if (entity.walkable) continue;
                if(!entity.isVisible()) continue;
                if ((int) (entity.getX() / Main.TILE_SIZE) == MathUtils.round(player.getX() / Main.TILE_SIZE) + facing.x && (int) (entity.getY() / Main.TILE_SIZE) == MathUtils.round(player.getY() / Main.TILE_SIZE) + facing.y) {
                    entity.onTouch(this);
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    public enum Direction {
        UP(0, 1), DOWN(0, -1), LEFT(-1, 0), RIGHT(1, 0);

        final int x;
        final int y;

        Direction(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static class AreaPrototype extends ContextPrototype {
        protected String name;
        protected Vector2 viewport = new Vector2(200, 200);
        protected Vector2 playerPosition = new Vector2(64, 64);
        protected Vector2 mapSize = new Vector2(32, 32);
        protected Entity[] entities = new Entity[]{};
        protected Battle.BattlePrototype[] battles = new Battle.BattlePrototype[]{};

        public AreaPrototype(String name) {
            this.name = name;
        }
    }
}
