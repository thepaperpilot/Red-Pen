package thepaperpilot.rpg;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;
import java.util.Map;

public class Area implements Screen, InputProcessor {
    private final AreaPrototype prototype;

    TiledMap tiledMap;
    OrthographicCamera camera;
    Viewport viewport;
    TiledMapRenderer tiledMapRenderer;
    Texture texture;
    MapLayer objectLayer;

    private TextureMapObject player;
    public Map<String, Entity> entities = new HashMap<String, Entity>();
    public Map<String, Dialogue> dialogues = new HashMap<String, Dialogue>();

    public Stage ui;

    public Direction facing = Direction.UP;
    public boolean capture;
    public Vector3 cameraTarget;
    public float zoomTarget;

    public enum Direction {
        UP(0, 1), DOWN(0, -1), LEFT(-1, 0), RIGHT(1, 0);

        int x;
        int y;

        Direction(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public Area(AreaPrototype prototype) {
        this.prototype = prototype;

        tiledMap = new TmxMapLoader().load(prototype.map + ".tmx");
        tiledMapRenderer = new OrthogonalTiledMapRendererWithSprites(tiledMap);

        camera = new OrthographicCamera();
        viewport = new ExtendViewport(prototype.viewport.x, prototype.viewport.y, camera);
        viewport.apply();
        camera.position.set(camera.viewportWidth / 2,camera.viewportHeight / 2,0);

        tiledMap.getLayers().get("collisions").setVisible(false);
        texture = new Texture(Gdx.files.internal("person7.png"));
        objectLayer = tiledMap.getLayers().get("player");
        TextureRegion textureRegion = new TextureRegion(texture,16,16);
        player = new TextureMapObject(textureRegion);
        player.setX(prototype.playerPosition.x);
        player.setY(prototype.playerPosition.y);
        objectLayer.getObjects().add(player);

        for (int i = 0; i < prototype.entities.length; i++) {
            Entity entity = new Entity(prototype.entities[i], this);
            entities.put(entity.prototype.name, entity);
            objectLayer.getObjects().add(entity);
        }

        for (int i = 0; i < prototype.dialogues.length; i++) {
            Dialogue dialogue = new Dialogue(prototype.dialogues[i], this);
            dialogues.put(dialogue.name, dialogue);
        }

        ui = new Stage(new StretchViewport(640, 360));
    }

    public void talk(String dialogue) {
        ui.addActor(dialogues.get(dialogue));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputMultiplexer(ui, this));
    }

    @Override
    public void render(float delta) {
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
        } else  if (d && !a) {
            xVel = -1;
            facing = Direction.RIGHT;
        }
        if (w && !s) {
            yVel = -1;
            facing = Direction.UP;
        } else  if (s && !w) {
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

        if (capture) {
            if (!camera.position.equals(cameraTarget)) {
                if (camera.position.dst(cameraTarget) < 2 * Main.MOVE_SPEED * delta) {
                    camera.position.set(cameraTarget);
                } else {
                    camera.translate(cameraTarget.cpy().sub(camera.position).nor().scl(2 * Main.MOVE_SPEED * delta));
                }
            }
            if (camera.zoom != zoomTarget) {
                if (Math.abs(camera.zoom - zoomTarget) < delta) {
                    camera.zoom = zoomTarget;
                } else {
                    if (camera.zoom > zoomTarget) camera.zoom -= delta;
                    else camera.zoom += delta;
                }
            }
        } else {
            Vector3 playerPos = new Vector3((int) player.getX(), (int) player.getY(), 0);
            if (!camera.position.equals(playerPos)) {
                if (camera.position.dst(playerPos) < 2 * Main.MOVE_SPEED * delta) {
                    camera.position.set(playerPos);
                } else {
                    camera.translate(playerPos.sub(camera.position).nor().scl(2 * Main.MOVE_SPEED * delta));
                }
            }
            if (camera.zoom != 1) {
                if (Math.abs(camera.zoom - 1) < delta) {
                    camera.zoom = 1;
                } else {
                    if (camera.zoom > 1) camera.zoom -= delta;
                    else camera.zoom += delta;
                }
            }

            if (camera.position.x < viewport.getWorldWidth() / 2f)
                camera.position.x = viewport.getWorldWidth()  / 2f;
            if (camera.position.y < viewport.getWorldHeight() / 2f)
                camera.position.y = viewport.getWorldHeight()  / 2f;
            if (camera.position.x > prototype.mapSize.x * Main.TILE_SIZE - viewport.getWorldWidth() / 2f)
                camera.position.x = prototype.mapSize.x * Main.TILE_SIZE - viewport.getWorldWidth() / 2f;
            if (camera.position.y > prototype.mapSize.y * Main.TILE_SIZE - viewport.getWorldHeight() / 2f)
                camera.position.y = prototype.mapSize.y * Main.TILE_SIZE - viewport.getWorldHeight() / 2f;
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

        camera.update();
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        ui.act();
        ui.draw();
    }

    private boolean walkable(float x, float y) {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (MapObject object : objectLayer.getObjects()) {
                    if (!(object instanceof Entity)) continue;
                    Entity entity = ((Entity) object);
                    if (!entity.isVisible()) continue;
                    if ((int) (entity.getX() / Main.TILE_SIZE) == (int) (i + x / Main.TILE_SIZE) && (int) (entity.getY() / Main.TILE_SIZE) == (int) (j + y / Main.TILE_SIZE)) {
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
        camera.position.set(player.getX(), player.getY(), 0);
        ui.getViewport().update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        tiledMap.dispose();
        ui.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.E || keycode == Input.Keys.ENTER) {
            for (Actor actor : ui.getActors()) {
                if (actor instanceof Dialogue) {
                    ((Dialogue) actor).next();
                    return true;
                }
            }
            for (MapObject object : objectLayer.getObjects()) {
                if (!(object instanceof Entity))
                    continue;
                Entity entity = ((Entity) object);
                if ((int) (entity.getX() / Main.TILE_SIZE) == MathUtils.round(player.getX() / Main.TILE_SIZE) + facing.x && (int) (entity.getY() / Main.TILE_SIZE) == MathUtils.round(player.getY() / Main.TILE_SIZE) + facing.y) {
                    entity.onTouch();
                    return true;
                }
            }
            return true;
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

    public static class AreaPrototype {
        String map = "clearing";
        Vector2 viewport = new Vector2(200, 200);
        Vector2 playerPosition = new Vector2(64, 64);
        Vector2 mapSize = new Vector2(32, 32);
        protected Entity.EntityPrototype[] entities = new Entity.EntityPrototype[]{};
        protected Dialogue.DialoguePrototype[] dialogues = new Dialogue.DialoguePrototype[]{};
    }
}
