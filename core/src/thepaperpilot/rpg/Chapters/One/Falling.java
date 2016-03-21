package thepaperpilot.rpg.Chapters.One;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import thepaperpilot.rpg.Area;
import thepaperpilot.rpg.Context;
import thepaperpilot.rpg.Events.*;
import thepaperpilot.rpg.Util.Constants;
import thepaperpilot.rpg.Util.ParticleEffectActor;

public class Falling extends Area.AreaPrototype {
    public Falling() {
        super("falling");

        /* Adding things to area */
        bgm = "Arpanauts.mp3";
        viewport = new Vector2(15 * Constants.TILE_SIZE, 15 * Constants.TILE_SIZE);
        playerStart = playerEnd = new Vector2(7 * Constants.TILE_SIZE, 17 * Constants.TILE_SIZE);
        mapSize = new Vector2(16, 16);
        tint = new Color(1, .8f, .8f, 1);
    }

    public void init(Area area) {
        /* Dialogues */
        thepaperpilot.rpg.UI.Dialogue.Line line1 = new thepaperpilot.rpg.UI.Dialogue.Line("Well, shit...");
        MoveEntity mc = new MoveEntity("player", 7 * Constants.TILE_SIZE, -1 * Constants.TILE_SIZE, false);
        mc.chain.add(new ChangeContext("corridor1"));
        line1.events.add(mc);
        thepaperpilot.rpg.UI.Dialogue falling = new thepaperpilot.rpg.UI.Dialogue.EntityDialogue("falling", new thepaperpilot.rpg.UI.Dialogue.Line[]{line1}, 2, "player", new Vector2(20, 10), new Vector2(120, 20), false);

        /* Adding things to area */
        dialogues = new thepaperpilot.rpg.UI.Dialogue[]{falling};

        new ParticleEffectActor.EnvironmentParticleEffect("falling", area);
    }

    public Context getContext(Vector2 start, Vector2 end) {
        Area area = new Area(this);
        area.init();
        StartDialogue dc = new StartDialogue("falling");
        dc.delay = 4;
        area.events.add(dc);
        area.events.add(new MoveEntity("player", 7 * Constants.TILE_SIZE, 7 * Constants.TILE_SIZE, false));
        area.events.add(new LockCamera(7.5f * Constants.TILE_SIZE, 7.5f * Constants.TILE_SIZE, .5f, true));
        area.events.add(new StartCutscene());
        return area;
    }
}
