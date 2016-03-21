package thepaperpilot.rpg.Events;

import com.badlogic.gdx.math.Vector2;
import thepaperpilot.rpg.Context;
import thepaperpilot.rpg.Main;
import thepaperpilot.rpg.Player;

public class ChangeContext extends Save {
    private String context = "";
    private Vector2 start;
    private Vector2 end;

    public ChangeContext(String context) {
        this.context = context;
    }

    private ChangeContext(String context, Vector2 start) {
        this(context);
        this.start = start;
    }

    public ChangeContext(String context, Vector2 start, Vector2 end) {
        this(context, start);
        this.end = end;
    }

    public void run(Context context) {
        context.events.add(new StartCutscene());
        Player.setArea(this.context);
        if (start == null && end == null) Main.changeContext(this.context);
        else if (end == null) Main.changeContext(this.context, start);
        else Main.changeContext(this.context, start, end);
        super.run(context);
    }
}
