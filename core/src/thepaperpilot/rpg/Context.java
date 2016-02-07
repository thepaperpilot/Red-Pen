package thepaperpilot.rpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import java.util.HashMap;
import java.util.Map;

public class Context implements Screen {

    private ContextPrototype prototype;
    public final Stage stage;
    public Map<String, Dialogue> dialogues = new HashMap<String, Dialogue>();

    public Context(ContextPrototype prototype) {
        this.prototype = prototype;
        stage = new Stage(new StretchViewport(640, 360));

        for (int i = 0; i < prototype.dialogues.length; i++) {
            Dialogue dialogue = new Dialogue(prototype.dialogues[i], this);
            dialogues.put(dialogue.name, dialogue);
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        stage.addAction(Actions.fadeIn(1));
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
                stage.addActor(dialogues.get(event.attributes.get("target")));
                stage.setKeyboardFocus(dialogues.get(event.attributes.get("target")));
                break;
            case CHANGE_AREA:
                Main.changeArea(event.attributes.get("target"));
                break;
        }
    }

    public static class ContextPrototype {
        protected Dialogue.DialoguePrototype[] dialogues = new Dialogue.DialoguePrototype[]{};
        public String bgm;
    }
}
