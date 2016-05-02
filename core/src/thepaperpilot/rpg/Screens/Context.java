package thepaperpilot.rpg.Screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import thepaperpilot.rpg.Components.ActorComponent;
import thepaperpilot.rpg.Components.DialogueComponent;
import thepaperpilot.rpg.Components.IdleComponent;
import thepaperpilot.rpg.Events.Event;
import thepaperpilot.rpg.Listeners.DialogueListener;
import thepaperpilot.rpg.Listeners.IdleListener;
import thepaperpilot.rpg.Main;
import thepaperpilot.rpg.Systems.CleanupSystem;
import thepaperpilot.rpg.Systems.DialogueSystem;
import thepaperpilot.rpg.Systems.EventSystem;
import thepaperpilot.rpg.Systems.IdleSystem;
import thepaperpilot.rpg.Util.Constants;

import java.util.ArrayList;

public class Context implements Screen {

    private final ContextPrototype prototype;

    public final Stage stage;
    public final Engine engine;
    public final ArrayList<Event> events = new ArrayList<Event>();

    public Context(ContextPrototype prototype) {
        this.prototype = prototype;
        stage = new Stage(new StretchViewport(640, 360));
        stage.getBatch().setColor(prototype.tint);
        // I wonder if its possible to reuse engines?
        engine = new Engine();

        stage.setDebugAll(Constants.DEBUG);

        /* Add Systems */
        engine.addSystem(new CleanupSystem());
        engine.addSystem(new EventSystem(this));
        engine.addSystem(new DialogueSystem());
        engine.addSystem(new IdleSystem());

        /* Add Listeners */
        engine.addEntityListener(Family.all(DialogueComponent.class).get(), 10, new DialogueListener(this, engine));
        engine.addEntityListener(Family.all(ActorComponent.class, IdleComponent.class).get(), 10, new IdleListener());
    }

    public void init() {

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
        public String bgm;
        public Color tint = Color.WHITE;

        public Context getContext() {
            Context context = new Context(this);
            context.init();
            return context;
        }
    }
}
