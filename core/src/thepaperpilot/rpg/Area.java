package thepaperpilot.rpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Area implements Screen {
    private final AreaPrototype prototype;

    TiledMap tiledMap;
    OrthographicCamera camera;
    Viewport viewport;
    TiledMapRenderer tiledMapRenderer;
    Texture texture;
    MapLayer objectLayer;

    private TextureMapObject player;

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
    }

    @Override
    public void show() {

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
        } else  if (d && !a) {
            xVel = -1;
        }
        if (w && !s) {
            yVel = -1;
        } else  if (s && !w) {
            yVel = 1;
        }
        if (xVel != 0 && yVel != 0) {
            xVel /= sqrt;
            yVel /= sqrt;
        }
        float newX = player.getX() - Main.MOVE_SPEED * xVel * delta;
        float newY = player.getY() - Main.MOVE_SPEED * yVel * delta;
        if (walkable(newX, player.getY())) player.setX(newX);
        if (walkable(player.getX(), newY)) player.setY(newY);

        Vector3 playerPos = new Vector3(player.getX(), player.getY(), 0);
        if (!camera.position.equals(playerPos)) {
            if (camera.position.dst(playerPos) < Main.MOVE_SPEED) {
                camera.position.set(playerPos);
            } else {
                camera.translate(playerPos.sub(camera.position).nor().scl(Main.MOVE_SPEED));
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

        camera.update();
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
    }

    private boolean walkable(float x, float y) {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
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

    }

    public static class AreaPrototype {
        String map = "clearing";
        Vector2 viewport = new Vector2(200, 200);
        Vector2 playerPosition = new Vector2(64, 64);
        Vector2 mapSize = new Vector2(32, 32);
    }
}
