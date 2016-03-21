package thepaperpilot.rpg.Chapters.One;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import thepaperpilot.rpg.Area;
import thepaperpilot.rpg.Components.ActorComponent;
import thepaperpilot.rpg.Components.NameComponent;
import thepaperpilot.rpg.Components.PositionComponent;
import thepaperpilot.rpg.Components.Triggers.CollisionComponent;
import thepaperpilot.rpg.Components.Triggers.EnterZoneComponent;
import thepaperpilot.rpg.Components.VisibleComponent;
import thepaperpilot.rpg.Context;
import thepaperpilot.rpg.Events.*;
import thepaperpilot.rpg.Main;
import thepaperpilot.rpg.Player;
import thepaperpilot.rpg.Util.Constants;
import thepaperpilot.rpg.Util.ParticleEffectActor;

public class Corridor1 extends Area.AreaPrototype {
    public Corridor1() {
        super("corridor1");

        bgm = "Digital Native.mp3";
        viewport = new Vector2(6 * Constants.TILE_SIZE, 6 * Constants.TILE_SIZE);
        playerStart = playerEnd = new Vector2(3 * Constants.TILE_SIZE, 4 * Constants.TILE_SIZE);
        mapSize = new Vector2(23, 21);
        tint = new Color(1, .8f, .8f, 1);
    }

    public void init(final Area area) {
        /* Entities */
        Entity demonOld = new Entity();
        demonOld.add(new NameComponent("habit"));
        demonOld.add(new ActorComponent(area, new Image(Main.getTexture("demonOld"))));
        demonOld.add(new PositionComponent(16 * Constants.TILE_SIZE, 5 * Constants.TILE_SIZE));
        if (!Player.getAttribute("corridor1")) demonOld.add(new VisibleComponent());

        Entity flower = new Entity();
        flower.add(new NameComponent("flower"));
        flower.add(new ActorComponent(area, new Image(Main.getTexture("thisiswhyimnotanartist"))));
        flower.add(new PositionComponent(17 * Constants.TILE_SIZE, 17 * Constants.TILE_SIZE));
        flower.add(new VisibleComponent());
        CollisionComponent cc = new CollisionComponent(0, 0, 16, 8);
        cc.events.add(new Event() {
            public void run(Context context) {
                if (!Player.getAttribute("spare"))
                    new StartDialogue("spare").run(area);
                else new StartDialogue("flower").run(area);
                super.run(context);
            }
        });
        flower.add(cc);

        Entity puzzle1 = new Entity();
        EnterZoneComponent ec = new EnterZoneComponent(area);
        ec.bounds.set(22 * Constants.TILE_SIZE, 0, Constants.TILE_SIZE, mapSize.y * Constants.TILE_SIZE);
        ec.events.add(new ChangeContext("puzzle1"));
        puzzle1.add(ec);

        /* Dialogues */
        thepaperpilot.rpg.UI.Dialogue.Line line1 = new thepaperpilot.rpg.UI.Dialogue.Line("Hello Player, welcome to Hell! I'm HABIT, and I'll be your tormentor for your stay.", "HABIT", "demonOld");
        thepaperpilot.rpg.UI.Dialogue.Line line2 = new thepaperpilot.rpg.UI.Dialogue.Line("You can stay here for eternity. Or continue forward, and spend eternity elsewhere. It's nice here, and if you follow me I ...will... torment you. So make sure you choose your next actions wisely.", "HABIT", "demonOld");
        thepaperpilot.rpg.UI.Dialogue.Line line3 = new thepaperpilot.rpg.UI.Dialogue.Line("This place will mess with your head. In that way, it's much like living. But you've left the living, and now will never leave again. In that way, this place is not like living.", "HABIT", "demonOld");
        thepaperpilot.rpg.UI.Dialogue.Line line4 = new thepaperpilot.rpg.UI.Dialogue.Line("Whatever you do now, I hope you regret it. Fortunately, I know you will.", "HABIT", "demonOld");
        line4.events.add(new SetEntityVisibility("habit", false));
        line4.events.add(new EndCutscene());
        thepaperpilot.rpg.UI.Dialogue welcome = new thepaperpilot.rpg.UI.Dialogue("welcome", new thepaperpilot.rpg.UI.Dialogue.Line[]{line1, line2, line3, line4});

        line1 = new thepaperpilot.rpg.UI.Dialogue.Line("You see a wilting flower next to a spare and a sign. The sign reads 'Cause of death: Mercy'");
        line2 = new thepaperpilot.rpg.UI.Dialogue.Line("Pick up the spare?");
        thepaperpilot.rpg.UI.Dialogue.Option yes = new thepaperpilot.rpg.UI.Dialogue.Option("Yes");
        yes.events.add(new AddAttribute("spare"));
        yes.events.add(new Event() {
            public void run(Context context) {
                Player.addInventory("spare");
                Player.save((Area) context);
            }
        });
        line2.options = new thepaperpilot.rpg.UI.Dialogue.Option[]{yes, new thepaperpilot.rpg.UI.Dialogue.Option("No")};
        thepaperpilot.rpg.UI.Dialogue spare = new thepaperpilot.rpg.UI.Dialogue("spare", new thepaperpilot.rpg.UI.Dialogue.Line[]{line1, line2});

        line1 = new thepaperpilot.rpg.UI.Dialogue.Line("You see a wilting flower next to a sign. The sign reads 'Cause of death: Mercy'");
        thepaperpilot.rpg.UI.Dialogue flowerDial = new thepaperpilot.rpg.UI.Dialogue("flower", new thepaperpilot.rpg.UI.Dialogue.Line[]{line1});

        /* Adding things to area */
        entities = new Entity[]{demonOld, flower, puzzle1};
        dialogues = new thepaperpilot.rpg.UI.Dialogue[]{welcome, spare, flowerDial};

        new ParticleEffectActor.EnvironmentParticleEffect("hell", area);
    }

    public Context getContext(Vector2 start, Vector2 end) {
        Area area = new Area(this);
        area.init();
        Event ec = moveEvent(start, end, area);
        if (!Player.getAttribute("corridor1")) {
            Player.addAttribute("corridor1");
            StartCutscene cc = new StartCutscene();
            MoveEntity mc = new MoveEntity("habit", 5 * Constants.TILE_SIZE, 5 * Constants.TILE_SIZE, false);
            mc.delay = 2;
            ec.chain.add(cc);
            cc.chain.add(mc);
            mc.chain.add(new StartDialogue("welcome"));
        }
        return area;
    }
}
