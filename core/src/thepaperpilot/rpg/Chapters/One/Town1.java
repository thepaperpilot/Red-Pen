package thepaperpilot.rpg.Chapters.One;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import thepaperpilot.rpg.Components.*;
import thepaperpilot.rpg.Components.Triggers.CollisionComponent;
import thepaperpilot.rpg.Components.Triggers.EnterZoneComponent;
import thepaperpilot.rpg.Events.*;
import thepaperpilot.rpg.Main;
import thepaperpilot.rpg.Screens.Area;
import thepaperpilot.rpg.Screens.Battle;
import thepaperpilot.rpg.Screens.Context;
import thepaperpilot.rpg.UI.Line;
import thepaperpilot.rpg.Util.Constants;
import thepaperpilot.rpg.Util.Player;

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
        soldierA.add(new AreaComponent(area));
        soldierA.add(new ActorComponent(new Image(Main.getTexture("soldier"))));
        soldierA.add(new PositionComponent(6 * Constants.TILE_SIZE, 10 * Constants.TILE_SIZE));
        soldierA.add(new VisibleComponent());
        soldierA.add(new CollisionComponent(0, 0, 16, 8));

        Entity soldierB = new Entity();
        soldierB.add(new NameComponent("soldierB"));
        soldierB.add(new AreaComponent(area));
        soldierB.add(new ActorComponent(new Image(Main.getTexture("soldier"))));
        soldierB.add(new PositionComponent(6 * Constants.TILE_SIZE, 7 * Constants.TILE_SIZE));
        soldierB.add(new VisibleComponent());
        soldierB.add(new CollisionComponent(0, 0, 16, 8));

        Entity pile = new Entity();
        pile.add(new NameComponent("pile"));
        pile.add(new AreaComponent(area));
        pile.add(new ActorComponent(new Image(Main.getTexture("pile"))));
        pile.add(new PositionComponent(24 * Constants.TILE_SIZE, 12 * Constants.TILE_SIZE));
        if (!Player.getAttribute("pile")) pile.add(new VisibleComponent());
        CollisionComponent cc = new CollisionComponent(0, 0, 16, 16);
        cc.events.add(new Event() {
            int stones = 132;
            public void run(Context context) {
                if (stones == 132) {
                    Entity entity = new Entity();
                    DialogueComponent dc = DialogueComponent.read("stones");
                    dc.start = "first";
                    entity.add(dc);
                    area.engine.addEntity(entity);
                } else if (stones == 1) {
                    Entity entity = new Entity();
                    DialogueComponent dc = DialogueComponent.read("stones");
                    dc.start = "last";
                    entity.add(dc);
                    area.engine.addEntity(entity);
                    context.events.add(new AddAttribute("pile"));
                    context.events.add(new SetEntityVisibility("pile", false));
                } else {
                    Entity entity = new Entity();
                    DialogueComponent dc = new DialogueComponent();
                    dc.start = "start";
                    dc.lines.put("start", new Line("There are still " + stones + " stones in the pile. Determined, you put another in your pocket."));
                    entity.add(dc);
                    area.engine.addEntity(entity);
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

        /* Adding things to area */
        entities = new Entity[]{soldierA, soldierB, pile, puzzle, throne};
        battles = new Battle.BattlePrototype[]{};
    }

    public Context getContext(Vector2 start, Vector2 end) {
        final Area area = new Area(this);
        area.init();
        ArrayList<Event> events = new ArrayList<Event>();
        if (!Player.getAttribute("captured")) {
            final MoveEntity a1 = new MoveEntity("soldierA", Constants.TILE_SIZE, 9 * Constants.TILE_SIZE, false);
            final MoveEntity b1 = new MoveEntity("soldierB", Constants.TILE_SIZE, 7 * Constants.TILE_SIZE, false);
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

            events.add(new StartCutscene());
            area.stage.addAction(Actions.sequence(Actions.delay(1), Actions.run(new Runnable() {
                @Override
                public void run() {
                    area.engine.addEntity(DialogueComponent.alert("soldierA", new Vector2(4, 20)));
                    area.engine.addEntity(DialogueComponent.alert("soldierB", new Vector2(4, 20)));
                }
            }), Actions.delay(2), Actions.run(new Runnable() {
                @Override
                public void run() {
                    Entity entity = new Entity();
                    DialogueComponent dc = DialogueComponent.read("guard");
                    dc.events.put("move", new Runnable() {
                        @Override
                        public void run() {
                            a1.run(area);
                            b1.run(area);
                        }
                    });
                    entity.add(dc);
                    area.engine.addEntity(entity);
                }
            })));
            Player.addAttribute("captured");
        }
        moveEvent(start, end, area).chain.addAll(events);

        return area;
    }
}
