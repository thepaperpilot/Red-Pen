package thepaperpilot.rpg.Events;

import thepaperpilot.rpg.Context;
import thepaperpilot.rpg.Player;

public class HealPlayer extends Event {
    @Override
    public void run(Context context) {
        Player.setHealth(Player.getMaxHealth());
        runNext(context);
    }
}
