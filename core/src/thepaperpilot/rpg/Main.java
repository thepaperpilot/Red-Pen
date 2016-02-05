package thepaperpilot.rpg;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import thepaperpilot.rpg.Areas.Clearing;

import java.util.HashMap;
import java.util.Map;

public class Main extends Game implements Screen {
	public static final AssetManager manager = new AssetManager();
	public static final Map globals = new HashMap<String, String>();
	public static final float MOVE_SPEED = 64;
	public static final int TILE_SIZE = 16;
	public static Skin skin;
	private static Main instance;
	private Stage loadingStage;


	public static void changeScreen(Screen screen) {
		if (screen == null)
			return;
		instance.setScreen(screen);
	}

	@Override
	public void create() {
		// use this so I can make a static changeScreen function
		// it basically makes Main a singleton
		instance = this;

		// start loading all our assets
		manager.load("skin.json", Skin.class);
		manager.load("person1.png", Texture.class);
		manager.load("person2.png", Texture.class);
		manager.load("person3.png", Texture.class);
		manager.load("person4.png", Texture.class);
		manager.load("person5.png", Texture.class);
		manager.load("person6.png", Texture.class);
		manager.load("person7.png", Texture.class);
		manager.load("person8.png", Texture.class);
		manager.load("person9.png", Texture.class);
		manager.load("person10.png", Texture.class);
		manager.load("person11.png", Texture.class);
		manager.load("person12.png", Texture.class);
		manager.load("person13.png", Texture.class);
		manager.load("person14.png", Texture.class);
		manager.load("pile.png", Texture.class);
		manager.load("Wacky Waiting.ogg", Sound.class);
		manager.load("click1.ogg", Sound.class);

		// show this screen while it loads
		setScreen(this);
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
			// set some stuff we need universally, now that their assets are loaded
			skin = manager.get("skin.json", Skin.class);
			skin.getFont("large").getData().setScale(.5f);
			skin.getFont("font").getData().setScale(.25f);
			manager.get("Wacky Waiting.ogg", Sound.class).loop(.5f);

			setScreen(Clearing.getArea());
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
		Gdx.gl.glClearColor(34 / 256f, 34 / 256f, 34 / 256f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		getScreen().render(Gdx.graphics.getDeltaTime());
	}

	public static Texture getTexture(String name) {
		return Main.manager.get(name + ".png", Texture.class);
	}
}
