package thepaperpilot.rpg.Events;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import thepaperpilot.rpg.Area;
import thepaperpilot.rpg.Battles.Battle;
import thepaperpilot.rpg.Context;
import thepaperpilot.rpg.Main;

public class StartCombat extends Event {
    private String combat = "";

    public StartCombat(String combat) {
        this.combat = combat;
    }

    public void run(Context context) {
        if (!(context instanceof Area)) return;
        final Area area = ((Area) context);
        if (!area.battles.containsKey(combat)) return;
        context.stage.addAction(Actions.sequence(Actions.fadeOut(1), Actions.run(new Runnable() {
            @Override
            public void run() {
                Main.changeScreen(new Battle(area.battles.get(combat), area));
                StartCombat.this.runNext(area);
            }
        })));
    }
}
