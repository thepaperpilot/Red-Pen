package thepaperpilot.rpg.Events;

import thepaperpilot.rpg.Screens.Battle;
import thepaperpilot.rpg.Screens.Context;

public class NextAttack extends Event {
    @Override
    public void run(Context context) {
        if (!(context instanceof Battle)) return;
        ((Battle) context).next();
        runNext(context);
    }
}
