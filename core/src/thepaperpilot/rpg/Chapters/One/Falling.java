package thepaperpilot.rpg.Chapters.One;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import thepaperpilot.rpg.Components.DialogueComponent;
import thepaperpilot.rpg.Components.FollowComponent;
import thepaperpilot.rpg.Events.*;
import thepaperpilot.rpg.Screens.Area;
import thepaperpilot.rpg.Screens.Context;
import thepaperpilot.rpg.UI.Line;
import thepaperpilot.rpg.UI.ParticleEffectActor;
import thepaperpilot.rpg.Util.Constants;

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
        new ParticleEffectActor.EnvironmentParticleEffect("falling", area);
    }

    public Context getContext(Vector2 start, Vector2 end) {
        final Area area = new Area(this);
        area.init();
        Entity falling = new Entity();
        DialogueComponent dc = new DialogueComponent();
        falling.add(dc);
        FollowComponent fc = new FollowComponent();
        fc.entity = "player";
        fc.offset = new Vector2(-60, 40);
        falling.add(fc);
        Line line = new Line("Well, shit...");
        line.event = "next";
        line.timer = 2;
        dc.lines.put("start", line);
        dc.start = "start";
        dc.events.put("next", new Runnable() {
            @Override
            public void run() {
                MoveEntity mc = new MoveEntity("player", 7 * Constants.TILE_SIZE, -1 * Constants.TILE_SIZE, false);
                mc.chain.add(new ChangeContext("corridor1"));
                area.events.add(mc);
            }
        });
        dc.small = true;
        dc.position = new Rectangle(20, 10, 120, 20);
        StartDialogue sc = new StartDialogue(falling);
        sc.delay = 4;
        area.events.add(sc);
        area.events.add(new MoveEntity("player", 7 * Constants.TILE_SIZE, 7 * Constants.TILE_SIZE, false));
        area.events.add(new LockCamera(7.5f * Constants.TILE_SIZE, 7.5f * Constants.TILE_SIZE, .5f, true));
        area.events.add(new StartCutscene());
        return area;
    }
}
