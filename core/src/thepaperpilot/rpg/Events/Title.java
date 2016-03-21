package thepaperpilot.rpg.Events;

import thepaperpilot.rpg.Context;
import thepaperpilot.rpg.Main;

public class Title extends Event {
    @Override
    public void run(Context context) {
        Main.changeScreen(new thepaperpilot.rpg.UI.Title());
    }
}
