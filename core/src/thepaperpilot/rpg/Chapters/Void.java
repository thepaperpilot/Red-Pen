package thepaperpilot.rpg.Chapters;

import com.badlogic.ashley.core.Entity;
import thepaperpilot.rpg.Components.DialogueComponent;
import thepaperpilot.rpg.Main;
import thepaperpilot.rpg.Screens.Context;
import thepaperpilot.rpg.Util.Player;

public class Void extends Context.ContextPrototype {
    public Context getContext() {
        final Context context = new Context(this);
        context.init();
        Entity entity = new Entity();
        DialogueComponent dc = DialogueComponent.read("welcome");
        dc.events.put("end", new Runnable() {
            @Override
            public void run() {
                Player.setArea("intro");
                Main.changeContext("intro");
            }
        });
        entity.add(dc);
        context.engine.addEntity(entity);
        return context;
    }
}
