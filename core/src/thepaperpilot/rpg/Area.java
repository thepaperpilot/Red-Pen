package thepaperpilot.rpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Area implements Screen, InputProcessor{
    TiledMap tiledMap;
    OrthographicCamera camera;
    Viewport viewport;
    TiledMapRenderer tiledMapRenderer;
    Texture texture;
    MapLayer objectLayer;

    TextureRegion textureRegion;

    public static Area load(String string) {
        return new Area();
    }

    @Override
    public void show() {
        float w = Gdx.graphics.getWidth() / 4f;
        float h = Gdx.graphics.getHeight() / 4f;

        camera = new OrthographicCamera();
        camera.setToOrtho(false,w,h);
        camera.update();
        camera = new OrthographicCamera();
        viewport = new StretchViewport(640,360,camera);
        viewport.apply();

        camera.position.set(camera.viewportWidth/2,camera.viewportHeight/2,0);
        tiledMap = new TmxMapLoader().load("clearing.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRendererWithSprites(tiledMap);
        Gdx.input.setInputProcessor(this);

        texture = new Texture(Gdx.files.internal("person7.png"));

        objectLayer = tiledMap.getLayers().get("player");
        textureRegion = new TextureRegion(texture,16,16);

        TextureMapObject tmo = new TextureMapObject(textureRegion);
        tmo.setX(100);
        tmo.setY(200);
        objectLayer.getObjects().add(tmo);
    }

    @Override
    public void render(float delta) {
        camera.update();
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
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

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Input.Keys.LEFT)
            camera.translate(-32, 0);
        if(keycode == Input.Keys.RIGHT)
            camera.translate(32, 0);
        if(keycode == Input.Keys.UP)
            camera.translate(0, 32);
        if(keycode == Input.Keys.DOWN)
            camera.translate(0, -32);
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 clickCoordinates = new Vector3(screenX,screenY,0);
        Vector3 position = camera.unproject(clickCoordinates);
        TextureMapObject character = (TextureMapObject)tiledMap.getLayers().get("player").getObjects().get(0);
        character.setX(position.x - 8);
        character.setY(position.y - 8);
        return true;
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
}
