package thepaperpilot.rpg.Chapters.One;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import thepaperpilot.rpg.Area;
import thepaperpilot.rpg.Battles.Battle;
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

import java.util.ArrayList;

public class Town1 extends Area.AreaPrototype {
    public Town1() {
        super("town1");

        /* Adding things to Area */
        bgm = "Searching.mp3";
        playerStart = new Vector2(-Constants.TILE_SIZE, 8 * Constants.TILE_SIZE);
        playerEnd = new Vector2(Constants.TILE_SIZE, 8 * Constants.TILE_SIZE);
        mapSize = new Vector2(64, 16);
        tint = new Color(1, .8f, 1, 1);
    }

    public void init(final Area area) {
        /* Entities */
        Entity soldierA = new Entity();
        soldierA.add(new NameComponent("soldierA"));
        soldierA.add(new ActorComponent(area, new Image(Main.getTexture("soldier"))));
        soldierA.add(new PositionComponent(6 * Constants.TILE_SIZE, 10 * Constants.TILE_SIZE));
        soldierA.add(new VisibleComponent());
        soldierA.add(new CollisionComponent(0, 0, 16, 8));

        Entity soldierB = new Entity();
        soldierB.add(new NameComponent("soldierB"));
        soldierB.add(new ActorComponent(area, new Image(Main.getTexture("soldier"))));
        soldierB.add(new PositionComponent(6 * Constants.TILE_SIZE, 7 * Constants.TILE_SIZE));
        soldierB.add(new VisibleComponent());
        soldierB.add(new CollisionComponent(0, 0, 16, 8));

        Entity pile = new Entity();
        pile.add(new NameComponent("pile"));
        pile.add(new ActorComponent(area, new Image(Main.getTexture("pile"))));
        pile.add(new PositionComponent(24 * Constants.TILE_SIZE, 12 * Constants.TILE_SIZE));
        if (!Player.getAttribute("pile")) pile.add(new VisibleComponent());
        CollisionComponent cc = new CollisionComponent(0, 0, 16, 16);
        cc.events.add(new Event() {
            int stones = 132;
            public void run(Context context) {
                if (stones == 132) {
                    context.events.add(new StartDialogue("allPapers"));
                } else if (stones == 1) {
                    context.events.add(new StartDialogue("lastPaper"));
                    context.events.add(new AddAttribute("pile"));
                } else {
                    new thepaperpilot.rpg.UI.Dialogue("", new thepaperpilot.rpg.UI.Dialogue.Line[]{new thepaperpilot.rpg.UI.Dialogue.Line("There are still " + stones + " stones in the pile. Determined, you put another in your pocket.")}).open(area);
                }
                stones--;
            }
        });
        pile.add(cc);

        Entity puzzle = new Entity();
        EnterZoneComponent ec = new EnterZoneComponent(area);
        ec.bounds.set(-Constants.TILE_SIZE, 0, Constants.TILE_SIZE, mapSize.y * Constants.TILE_SIZE);
        ec.events.add(new ChangeContext("puzzle1", new Vector2(32 * Constants.TILE_SIZE, 15 * Constants.TILE_SIZE), new Vector2(30 * Constants.TILE_SIZE, 15 * Constants.TILE_SIZE)));
        puzzle.add(ec);

        Entity throne = new Entity();
        ec = new EnterZoneComponent(area);
        ec.bounds.set(8 * Constants.TILE_SIZE, 12.9f * Constants.TILE_SIZE, Constants.TILE_SIZE, mapSize.y * Constants.TILE_SIZE);
        ec.events.add(new ChangeContext("throne"));
        throne.add(ec);

        /* Dialogues */
        thepaperpilot.rpg.UI.Dialogue.Line line1 = new thepaperpilot.rpg.UI.Dialogue.Line("Whoah! HABIT said that last puzzle would take anyone at least a millenia!", "Guard", "soldier");
        thepaperpilot.rpg.UI.Dialogue.Line line2 = new thepaperpilot.rpg.UI.Dialogue.Line("Never mind that. You need to come with us!", "Guard", "soldier");
        MoveEntity a1 = new MoveEntity("soldierA", Constants.TILE_SIZE, 9 * Constants.TILE_SIZE, false);
        MoveEntity b1 = new MoveEntity("soldierB", Constants.TILE_SIZE, 7 * Constants.TILE_SIZE, false);
        MoveEntity a2 = new MoveEntity("soldierA", 8.5f * Constants.TILE_SIZE, 9 * Constants.TILE_SIZE, false);
        MoveEntity b2 = new MoveEntity("soldierB", 8.5f * Constants.TILE_SIZE, 7 * Constants.TILE_SIZE, false);
        MoveEntity p1 = new MoveEntity("player", 8.5f * Constants.TILE_SIZE, 8 * Constants.TILE_SIZE, false);
        MoveEntity a3 = new MoveEntity("soldierA", 8.5f * Constants.TILE_SIZE, 14 * Constants.TILE_SIZE, false);
        MoveEntity b3 = new MoveEntity("soldierB", 8.5f * Constants.TILE_SIZE, 14 * Constants.TILE_SIZE, false);
        MoveEntity p2 = new MoveEntity("player", 8.5f * Constants.TILE_SIZE, 14 * Constants.TILE_SIZE, false);
        a1.chain.add(a2);
        a1.chain.add(b2);
        a1.chain.add(p1);
        a2.chain.add(a3);
        a2.chain.add(b3);
        a2.chain.add(p2);
        a3.chain.add(new ChangeContext("throne"));
        line2.events.add(a1);
        line2.events.add(b1);
        thepaperpilot.rpg.UI.Dialogue capture = new thepaperpilot.rpg.UI.Dialogue("capture", new thepaperpilot.rpg.UI.Dialogue.Line[]{line1, line2});

        thepaperpilot.rpg.UI.Dialogue allPapersDial = new thepaperpilot.rpg.UI.Dialogue("allPapers", new thepaperpilot.rpg.UI.Dialogue.Line[]{new thepaperpilot.rpg.UI.Dialogue.Line("You see a pile of precisely 132 stones. You pick one up and put it in your pocket.")});

        thepaperpilot.rpg.UI.Dialogue.Line line = new thepaperpilot.rpg.UI.Dialogue.Line("There's only one stone left. With a smug face you pick up the last one and put it in your now bulging pockets, congratulating yourself on a job well done.");
        line.events.add(new SetEntityVisibility("pile", false));
        thepaperpilot.rpg.UI.Dialogue lastPaperDial = new thepaperpilot.rpg.UI.Dialogue("lastPaper", new thepaperpilot.rpg.UI.Dialogue.Line[]{line});

        /* Adding things to area */
        entities = new Entity[]{soldierA, soldierB, pile, puzzle, throne};
        dialogues = new thepaperpilot.rpg.UI.Dialogue[]{capture, allPapersDial, lastPaperDial};
        battles = new Battle.BattlePrototype[]{};
    }

    public Context getContext(Vector2 start, Vector2 end) {
        final Area area = new Area(this);
        area.init();
        ArrayList<Event> events = new ArrayList<Event>();
        if (!Player.getAttribute("captured")) {
            events.add(new StartCutscene());
            area.stage.addAction(Actions.sequence(Actions.delay(1), Actions.run(new Runnable() {
                @Override
                public void run() {
                    thepaperpilot.rpg.UI.Dialogue.alert("soldierA").open(area);
                    thepaperpilot.rpg.UI.Dialogue.alert("soldierB").open(area);
                }
            }), Actions.delay(2), Actions.run(new Runnable() {
                @Override
                public void run() {
                    area.events.add(new StartDialogue("capture"));
                }
            })));
            Player.addAttribute("captured");
        }
        moveEvent(start, end, area).chain.addAll(events);
        return area;
    }
}
