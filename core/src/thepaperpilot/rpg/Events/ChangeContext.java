package thepaperpilot.rpg.Events;

import com.badlogic.gdx.math.Vector2;
import thepaperpilot.rpg.Main;
import thepaperpilot.rpg.Screens.Area;
import thepaperpilot.rpg.Screens.Context;
import thepaperpilot.rpg.Util.Player;

public class ChangeContext extends Event {
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
        if (start == null && end == null) {
            if (Main.contexts.get(this.context) instanceof Area.AreaPrototype) {
                Vector2 pos = ((Area.AreaPrototype) Main.contexts.get(this.context)).playerEnd;
                Player.save(pos.x, pos.y);
            }
            Main.changeContext(this.context);
        } else if (end == null) {
            Player.save(start.x, start.y);
            Main.changeContext(this.context, start);
        } else {
            Player.save(end.x, end.y);
            Main.changeContext(this.context, start, end);
        }
        super.run(context);
    }
}
