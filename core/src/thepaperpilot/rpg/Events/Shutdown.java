package thepaperpilot.rpg.Events;

import thepaperpilot.rpg.Main;
import thepaperpilot.rpg.Screens.Context;

public class Shutdown extends Event {
    @Override
    public void run(Context context) {
        Main.changeScreen(Main.instance);
    }
}
