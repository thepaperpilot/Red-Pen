package thepaperpilot.rpg.Events;

import thepaperpilot.rpg.Screens.Area;
import thepaperpilot.rpg.Screens.Context;
import thepaperpilot.rpg.Util.Player;

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
