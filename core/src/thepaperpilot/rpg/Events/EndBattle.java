package thepaperpilot.rpg.Events;

import thepaperpilot.rpg.Battles.Battle;
import thepaperpilot.rpg.Context;

public class EndBattle extends Event {
    @Override
    public void run(Context context) {
        if (!(context instanceof Battle)) return;
        ((Battle) context).exit();
        runNext(context);
    }
}
