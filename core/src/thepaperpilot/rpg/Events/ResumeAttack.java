package thepaperpilot.rpg.Events;

import thepaperpilot.rpg.Battles.Battle;
import thepaperpilot.rpg.Context;

public class ResumeAttack extends Event {
    @Override
    public void run(Context context) {
        if (!(context instanceof Battle)) return;
        ((Battle) context).attacking = true;
        runNext(context);
    }
}
