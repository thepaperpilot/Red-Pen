package thepaperpilot.rpg;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import thepaperpilot.rpg.Events.Event;
import thepaperpilot.rpg.Systems.CleanupSystem;
import thepaperpilot.rpg.Systems.EventSystem;
import thepaperpilot.rpg.UI.Dialogue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Context implements Screen {

    private final ContextPrototype prototype;
    public Map<String, Dialogue> dialogues = new HashMap<String, Dialogue>();

    public final Stage stage;
    public final Engine engine;
    public final ArrayList<Event> events = new ArrayList<Event>();

    public Context(ContextPrototype prototype) {
        this.prototype = prototype;
        stage = new Stage(new StretchViewport(640, 360));
        stage.getBatch().setColor(prototype.tint);
        // I wonder if its possible to reuse engines?
        engine = new Engine();

        /* Add Systems */
        engine.addSystem(new CleanupSystem());
        engine.addSystem(new EventSystem(this));

        /* Add Listeners */

    }

    public void init() {
        for (Dialogue dialogue : prototype.dialogues) {
            dialogues.put(dialogue.name, dialogue);
        }
    }

    @Override
    public void show() {
        setInputProcessor();
        stage.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(1)));
        if (prototype.bgm != null)
            Main.changeBGM(prototype.bgm);
    }

    protected void setInputProcessor() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        engine.update(delta);
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
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
        stage.dispose();
    }

    public static class ContextPrototype {
        protected Dialogue[] dialogues = new Dialogue[]{};
        public String bgm;
        public Color tint = Color.WHITE;

        public Context getContext() {
            Context context = new Context(this);
            context.init();
            return context;
        }
    }
}
