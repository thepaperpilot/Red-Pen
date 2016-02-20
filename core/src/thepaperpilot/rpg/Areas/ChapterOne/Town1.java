package thepaperpilot.rpg.Areas.ChapterOne;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import thepaperpilot.rpg.Battles.Battle;
import thepaperpilot.rpg.Context;
import thepaperpilot.rpg.Event;
import thepaperpilot.rpg.Main;
import thepaperpilot.rpg.Map.Area;
import thepaperpilot.rpg.Map.Entity;
import thepaperpilot.rpg.Player;
import thepaperpilot.rpg.UI.Dialogue;

import java.util.ArrayList;

public class Town1 extends Area {

    public Town1(AreaPrototype prototype) {
        super(prototype);
    }

    public void render(float delta) {
        super.render(delta);

        if (cutscene) return;

        if (player.getX() < 0) {
            Main.changeContext("puzzle1", new Vector2(32 * Main.TILE_SIZE, 15 * Main.TILE_SIZE), new Vector2(30 * Main.TILE_SIZE, 15 * Main.TILE_SIZE));
        }

        if (player.getX() > 8 * Main.TILE_SIZE && player.getX() < 9 * Main.TILE_SIZE && player.getY() > 12.9 * Main.TILE_SIZE) {
            Main.changeContext("throne", new Vector2(7.5f * Main.TILE_SIZE, Main.TILE_SIZE));
        }
    }

    public static class TownPrototype extends AreaPrototype{
        public TownPrototype() {
            super("town1");

            /* Adding things to Area */
            bgm = "Searching.mp3";
            playerPosition = new Vector2(-Main.TILE_SIZE, 8 * Main.TILE_SIZE);
            mapSize = new Vector2(64, 16);
            tint = new Color(1, .8f, 1, 1);
        }

        public void init() {
            /* Entities */
            Entity soldierA = new Entity("soldierA", "soldier", 6 * Main.TILE_SIZE, 10 * Main.TILE_SIZE, true, false);
            Entity soldierB = new Entity("soldierB", "soldier", 6 * Main.TILE_SIZE, 7 * Main.TILE_SIZE, true, false);

            Entity pile = new Entity("pile", "pile", 24 * Main.TILE_SIZE, 12 * Main.TILE_SIZE, true, false) {
                int stones = 132;

                public void onTouch(Area area) {
                    if (stones == 132) {
                        new Event(Event.Type.DIALOGUE, "allPapers").run(area);
                    } else if (stones == 1) {
                        new Event(Event.Type.DIALOGUE, "lastPaper").run(area);
                        Player.addAttribute("pile");
                    } else {
                        new Dialogue("", new Dialogue.Line[]{new Dialogue.Line("There are still " + stones + " stones in the pile. Determined, you put another in your pocket.")}).open(area);
                    }
                    stones--;
                }
            };

            /* Dialogues */
            Dialogue.Line line1 = new Dialogue.Line("Whoah! HABIT said that last puzzle would take anyone at least a millenia!", "Guard", "soldier");
            Dialogue.Line line2 = new Dialogue.Line("Never mind that. You need to come with us!", "Guard", "soldier");
            Event moveSoldierA = new Event(Event.Type.MOVE_ENTITY, "soldierA");
            moveSoldierA.attributes.put("x", "" + Main.TILE_SIZE);
            moveSoldierA.attributes.put("y", "" + 9 * Main.TILE_SIZE);
            Event moveSoldierB = new Event(Event.Type.MOVE_ENTITY, "soldierB");
            moveSoldierB.attributes.put("x", "" + Main.TILE_SIZE);
            moveSoldierB.attributes.put("y", "" + 7 * Main.TILE_SIZE);
            Event moveSoldierA2 = new Event(Event.Type.MOVE_ENTITY, "soldierA");
            moveSoldierA2.attributes.put("x", "" + 8.5 * Main.TILE_SIZE);
            moveSoldierA2.attributes.put("y", "" + 9 * Main.TILE_SIZE);
            Event moveSoldierB2 = new Event(Event.Type.MOVE_ENTITY, "soldierB");
            moveSoldierB2.attributes.put("x", "" + 8.5 * Main.TILE_SIZE);
            moveSoldierB2.attributes.put("y", "" + 7 * Main.TILE_SIZE);
            Event movePlayer = new Event(Event.Type.MOVE_ENTITY, "player");
            movePlayer.attributes.put("x", "" + 8.5 * Main.TILE_SIZE);
            movePlayer.attributes.put("y", "" + 8 * Main.TILE_SIZE);
            Event moveSoldierA3 = new Event(Event.Type.MOVE_ENTITY, "soldierA");
            moveSoldierA3.attributes.put("x", "" + 8.5 * Main.TILE_SIZE);
            moveSoldierA3.attributes.put("y", "" + 14 * Main.TILE_SIZE);
            Event moveSoldierB3 = new Event(Event.Type.MOVE_ENTITY, "soldierB");
            moveSoldierB3.attributes.put("x", "" + 8.5 * Main.TILE_SIZE);
            moveSoldierB3.attributes.put("y", "" + 14 * Main.TILE_SIZE);
            Event movePlayer2 = new Event(Event.Type.MOVE_ENTITY, "player");
            movePlayer2.attributes.put("x", "" + 8.5 * Main.TILE_SIZE);
            movePlayer2.attributes.put("y", "" + 14 * Main.TILE_SIZE);
            moveSoldierA.next = new Event[]{moveSoldierA2, moveSoldierB2, movePlayer};
            moveSoldierA2.next = new Event[]{moveSoldierA3, moveSoldierB3, movePlayer2};
            line2.events = new Event[]{moveSoldierA, moveSoldierB};
            Dialogue capture = new Dialogue("capture", new Dialogue.Line[]{line1, line2});

            Dialogue allPapersDial = new Dialogue("allPapers", new Dialogue.Line[]{new Dialogue.Line("You see a pile of precisely 132 stones. You pick one up and put it in your pocket.")});

            Dialogue.Line line = new Dialogue.Line("There's only one stone left. With a smug face you pick up the last one and put it in your now bulging pockets, congratulating yourself on a job well done.");
            final Event removePaper = new Event(Event.Type.SET_ENTITY_VISIBILITY, "pile");
            removePaper.attributes.put("visible", "false");
            line.events = new Event[]{removePaper};
            Dialogue lastPaperDial = new Dialogue("lastPaper", new Dialogue.Line[]{line});

            /* Adding things to area */
            entities = new Entity[]{soldierA, soldierB, pile};
            dialogues = new Dialogue[]{capture, allPapersDial, lastPaperDial};
            battles = new Battle.BattlePrototype[]{};
        }

        public void loadAssets(AssetManager manager) {
            super.loadAssets(manager);
            manager.load("Searching.mp3", Sound.class);
            manager.load("soldier.png", Texture.class);
            manager.load("pile.png", Texture.class);
        }

        public Context getContext(Vector2 start, Vector2 end) {
            final Area area = new Town1(this);
            ArrayList<Event> events = new ArrayList<Event>();
            if (!Player.getAttribute("captured")) {
                events.add(new Event(Event.Type.CUTSCENE));
                area.stage.addAction(Actions.sequence(Actions.delay(1), Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        Dialogue.alert("soldierA").open(area);
                        Dialogue.alert("soldierB").open(area);
                    }
                }), Actions.delay(2), Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        new Event(Event.Type.DIALOGUE, "capture").run(area);
                    }
                })));
                Player.addAttribute("captured");
            }
            if (Player.getAttribute("pile")) {
                Event vis = new Event(Event.Type.SET_ENTITY_VISIBILITY, "pile");
                vis.attributes.put("visible", "false");
                vis.run(area);
                events.add(vis);
            }
            Event.moveEvent(start, end, area).next = events.toArray(new Event[events.size()]);
            return area;
        }
    }
}
