package thepaperpilot.rpg;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import thepaperpilot.rpg.Areas.Clearing;
import thepaperpilot.rpg.Areas.Falling;
import thepaperpilot.rpg.Areas.Intro;
import thepaperpilot.rpg.Areas.Void;

import java.util.HashMap;
import java.util.Map;

public class Main extends Game implements Screen {
    public static final AssetManager manager = new AssetManager();
    public static final float MOVE_SPEED = 64;
    public static final int TILE_SIZE = 16;
    public static final float TEXT_SPEED = 25f; //characters per second

    private static final Map<String, Context.ContextPrototype> contexts = new HashMap<String, Context.ContextPrototype>();
    public static Skin skin;
    public static Main instance;
    private static Sound bgm;
    private static long bgmId;
    private static Sound newBGM;
    private static long newId;
    private static float transition = 1;
    private static Stage loadingStage;
    public static Context.ContextPrototype target;
    private static Preferences save;

    public static void changeScreen(Screen screen) {
        if (screen == null)
            return;
        instance.setScreen(screen);
    }

    public static void changeContext(String context) {
        contexts.get(context).loadAssets(manager);
        target = contexts.get(context);
        saveArea(context);
        changeScreen(instance);
    }

    public static Texture getTexture(String name) {
        return Main.manager.get(name + ".png", Texture.class);
    }

    @Override
    public void create() {
        // use this so I can make a static changeScreen function
        // it basically makes Main a singleton
        instance = this;
        save = Gdx.app.getPreferences("thepaperpilot.story.save");

        // start loading all our assets
        manager.load("skin.json", Skin.class);
        manager.load("player.png", Texture.class);
        manager.load("title.png", Texture.class);
        manager.load("click1.ogg", Sound.class);
        manager.load("jingles_SAX03.ogg", Sound.class);
        manager.load("jingles_SAX05.ogg", Sound.class);
        manager.load("jingles_SAX07.ogg", Sound.class);
        manager.load("jingles_SAX15.ogg", Sound.class);
        manager.load("jingles_SAX16.ogg", Sound.class);

        changeScreen(this);
    }

    @Override
    public void show() {
        // show a basic loading screen
        loadingStage = new Stage(new ExtendViewport(200, 200));

        Label loadingLabel = new Label("Loading...", new Skin(Gdx.files.internal("skin.json")));
        loadingLabel.setFillParent(true);
        loadingLabel.setAlignment(Align.center);
        loadingStage.addActor(loadingLabel);

        // basically a sanity check? loadingStage shouldn't have any input listeners
        // but I guess this'll help if the inputprocessor gets set to something it shouldn't
        Gdx.input.setInputProcessor(loadingStage);
    }

    @Override
    public void render(float delta) {
        // render the loading screen
        // act shouldn't do anything, but putting it here is good practice, I guess?
        loadingStage.act();
        loadingStage.draw();

        // continue loading. If complete, do shit
        if (manager.update()) {
            if (skin == null) {
                skin = manager.get("skin.json", Skin.class);
                skin.getFont("large").getData().setScale(.5f);
                skin.getFont("large").getData().markupEnabled = true;
                skin.getFont("font").getData().setScale(.25f);
                skin.getFont("font").getData().markupEnabled = true;

                // create all the contexts
                contexts.put("clearing", new Clearing.ClearingPrototype());
                contexts.put("welcome", new Void());
                contexts.put("intro", new Intro());
                contexts.put("falling", new Falling.FallingPrototype());

                // show this screen while it loads
                changeScreen(new Title());
            } else if (target != null) changeScreen(target.getContext());
        }
    }

    @Override
    public void hide() {
        /// we're a good garbage collector
        loadingStage.dispose();
    }

    @Override
    public void pause() {
        // we're a passthrough!
        if (getScreen() == this) return;
        super.pause();
    }

    @Override
    public void resume() {
        // we're a passthrough!
        if (getScreen() == this) return;
        super.pause();
    }

    @Override
    public void resize(int width, int height) {
        // we're a passthrough!
        if (getScreen() == this) return;
        if (getScreen() != null) {
            getScreen().resize(width, height);
        }
    }

    @Override
    public void dispose() {
        // we're a passthrough!
        if (getScreen() == this) return;
        if (getScreen() != null) {
            getScreen().dispose();
        }
        // also clean up our shit
        manager.dispose();
        skin.dispose();
    }

    @Override
    public void render() {
        // we're a passthrough!
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Transition bgms
        if (transition != 1) {
            if (transition > 1 || bgm == null) {
                transition = 1;
                bgm = newBGM;
                bgmId = newId;
                bgm.setVolume(bgmId, 1);
            } else {
                transition += Gdx.graphics.getDeltaTime();
                bgm.setVolume(bgmId, 1 - transition);
                newBGM.setVolume(newId, transition);
            }
        }

        getScreen().render(Gdx.graphics.getDeltaTime());
    }

    public static void changeBGM(String bgm) {
        newBGM = manager.get(bgm + ".ogg", Sound.class);
        if (Main.bgm != newBGM) {
            transition = 0;
            newId = newBGM.loop();
        }
    }

    public static String loadArea() {
        return save.getString("area", "welcome");
    }

    public static void saveArea(String string) {
        save.putString("area", string);
        save.flush();
    }
}
