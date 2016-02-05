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

			// go to the menu screen
			Area.AreaPrototype prototype = new Area.AreaPrototype();
			Entity.EntityPrototype entityPrototype = new Entity.EntityPrototype("talker", "person9", 100, 25, true);
			Event.EventPrototype talk = new Event.EventPrototype();
			talk.type = "DIALOGUE";
			talk.attributes.put("target", "talker");
			Event.EventPrototype move = new Event.EventPrototype();
			move.type = "MOVE_ENTITY";
			move.attributes.put("target", "talker");
			move.attributes.put("x", "" + 20 * TILE_SIZE);
			move.attributes.put("y", "" + 20 * TILE_SIZE);
			entityPrototype.events = new Event.EventPrototype[]{talk, move};
			prototype.entities = new Entity.EntityPrototype[]{entityPrototype};
			Dialogue.DialoguePrototype dialoguePrototype = new Dialogue.DialoguePrototype();
			dialoguePrototype.name = "talker";
			Dialogue.LinePrototype line1 = new Dialogue.LinePrototype();
			line1.message = "Yo bro I finally got something workable! There's no battle system yet, it's still difficult to add new content, and I obviously didn't make any of the assets, but hey, its been, what, a little over 24 hours? Pretty good imo. No jokes or anything, I'm just excited. Woo!";
			line1.name = "ur mum lol";
			Dialogue.LinePrototype line2 = new Dialogue.LinePrototype();
			line2.name = "wew lad";
			line2.message = "in case you missed it, I moved as well. Isn't that fancy?";
			dialoguePrototype.lines = new Dialogue.LinePrototype[]{line1, line2};
			Dialogue.DialoguePrototype welcomeDial = new Dialogue.DialoguePrototype();
			welcomeDial.name = "welcome";
			Dialogue.LinePrototype welcomeLine = new Dialogue.LinePrototype();
			welcomeLine.name = "narrator";
			welcomeLine.face = "person14";
			welcomeLine.message = "Welcome to this really shitty piece of a game! Press e or enter to interact with things!";
			welcomeDial.lines = new Dialogue.LinePrototype[]{welcomeLine};
			prototype.dialogues = new Dialogue.DialoguePrototype[]{dialoguePrototype, welcomeDial};
			Area area = new Area(prototype);
			Event.EventPrototype welcome = new Event.EventPrototype();
			welcome.type = "DIALOGUE";
			welcome.attributes.put("target", "welcome");
			new Event(welcome, area).run();
			setScreen(area);
		}
	}

	public static void renderParticles(float delta) {
		// render the choice particles. Not actually used in the loading screen, but used everywhere else, so why not
		final Matrix4 trans = new Matrix4();
		trans.scale(Gdx.graphics.getWidth() / 640, Gdx.graphics.getHeight() / 360, 1);
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
