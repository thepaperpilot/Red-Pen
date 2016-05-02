package thepaperpilot.rpg.UI;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import thepaperpilot.rpg.Components.DialogueComponent;
import thepaperpilot.rpg.Main;
import thepaperpilot.rpg.Systems.DialogueSystem;
import thepaperpilot.rpg.Util.Mappers;

public class Option {
    public String event = "";
    public String message;
    public String next;
    public transient Label label;

    @SuppressWarnings("unused")
    public Option() {
        this("", false);
    }

    public Option(String message, boolean small) {
        label = new Label("> " + message, Main.skin, small ? "default" : "large");
        this.message = message;
    }

    public void reset(final Entity entity, final DialogueSystem system) {
        // do the actions when this button is clicked
        label.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                select(entity, system);
                event.cancel();
                return true;
            }

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                DialogueComponent dc = Mappers.dialogue.get(entity);

                dc.selected = Option.this;
                DialogueSystem.updateSelected(entity);
            }
        });
    }

    public void select(Entity entity, DialogueSystem system) {
        DialogueComponent dc = Mappers.dialogue.get(entity);

        if (dc.events.containsKey(event))
            dc.events.get(event).run();

        system.next(entity, next);
    }
}
