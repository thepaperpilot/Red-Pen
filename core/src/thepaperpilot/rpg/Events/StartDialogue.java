package thepaperpilot.rpg.Events;

import com.badlogic.ashley.core.Entity;
import thepaperpilot.rpg.Components.DialogueComponent;
import thepaperpilot.rpg.Screens.Context;

public class StartDialogue extends Event {
    private Entity entity;

    public StartDialogue(String dialogue) {
        this(DialogueComponent.read(dialogue));
    }

    public StartDialogue(DialogueComponent dc) {
        entity = new Entity();
        entity.add(dc);
    }

    public StartDialogue(Entity entity) {
        this.entity = entity;
    }

    public void run(Context context) {
        context.engine.addEntity(entity);
    }
}