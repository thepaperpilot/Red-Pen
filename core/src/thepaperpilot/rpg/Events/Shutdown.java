package thepaperpilot.rpg.Events;

import thepaperpilot.rpg.Context;
import thepaperpilot.rpg.Main;

public class Shutdown extends Event {
    @Override
    public void run(Context context) {
        Main.changeScreen(Main.instance);
    }
}
