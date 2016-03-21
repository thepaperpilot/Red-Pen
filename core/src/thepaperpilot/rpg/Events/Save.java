package thepaperpilot.rpg.Events;

import thepaperpilot.rpg.Area;
import thepaperpilot.rpg.Context;
import thepaperpilot.rpg.Player;

public class Save extends Event {
    @Override
    public void run(Context context) {
        if (context instanceof Area) {
            Player.save((Area) context);
        }
        else Player.save();
        runNext(context);
    }
}
