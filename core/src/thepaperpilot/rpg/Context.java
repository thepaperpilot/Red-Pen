package thepaperpilot.rpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import java.util.HashMap;
import java.util.Map;

public class Context implements Screen {

    private ContextPrototype prototype;
    public final Stage stage;
    public Map<String, Dialogue.DialoguePrototype> dialogues = new HashMap<String, Dialogue.DialoguePrototype>();
    protected boolean cutscene;
    protected AlphaAction transition;

    public Context(ContextPrototype prototype) {
        this.prototype = prototype;
        stage = new Stage(new StretchViewport(640, 360));
        stage.getBatch().setColor(prototype.tint);

        for (Dialogue.DialoguePrototype dialoguePrototype : prototype.dialogues) {
            dialogues.put(dialoguePrototype.name, dialoguePrototype);
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        stage.addAction(Actions.sequence(transition = Actions.fadeIn(1), Actions.run(new Runnable() {
            @Override
            public void run() {
                transition = null;
            }
        })));
        if (prototype.bgm != null)
            Main.changeBGM(prototype.bgm);
    }

    @Override
    public void render(float delta) {
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

    public void run(Event event) {
        switch (event.type) {
            case DIALOGUE:
                Dialogue dialogue = new Dialogue(dialogues.get(event.attributes.get("target")), this);
                stage.addActor(dialogue);
                stage.setKeyboardFocus(dialogue);
                break;
            case CHANGE_CONTEXT:
                Main.changeContext(event.attributes.get("target"));
                break;
            case CUTSCENE:
                Gdx.input.setInputProcessor(stage);
                cutscene = true;
                break;
            case END_CUTSCENE:
                show();
                cutscene = false;
                break;
            case SHUTDOWN:
                Main.target = null;
                Main.changeScreen(Main.instance);
                break;
            case HEAL_PLAYER:
                Main.setHealth(Main.getMaxHealth());
                break;
        }
    }

    public static class ContextPrototype {
        protected Dialogue.DialoguePrototype[] dialogues = new Dialogue.DialoguePrototype[]{};
        public String bgm;
        public Color tint = Color.WHITE;

        public void loadAssets(AssetManager manager) {

        }

        public Context getContext() {
            return new Context(this);
        }
    }

    public void loadAssets(AssetManager manager) {
        prototype.loadAssets(manager);
    }
}
