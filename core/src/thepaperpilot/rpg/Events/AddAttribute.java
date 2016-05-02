package thepaperpilot.rpg.Events;

import thepaperpilot.rpg.Screens.Context;
import thepaperpilot.rpg.Util.Player;

public class AddAttribute extends Save {
    private String attribute = "";

    public AddAttribute(String attribute) {
        this.attribute = attribute;
    }

    public void run(Context context) {
        Player.addAttribute(attribute);
        super.run(context);
    }
}
