package thepaperpilot.rpg.Events;

import thepaperpilot.rpg.Screens.Context;
import thepaperpilot.rpg.Util.Player;

public class HealPlayer extends Event {
    @Override
    public void run(Context context) {
        Player.setHealth(Player.getMaxHealth());
        runNext(context);
    }
}
