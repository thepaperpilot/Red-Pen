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

public class Town1 extends Area {

    public Town1(AreaPrototype prototype) {
        super(prototype);
    }

    public void render(float delta) {
        super.render(delta);

        if (player.getX() < 0) {
            Event movePlayer = new Event(Event.Type.MOVE_PLAYER);
            movePlayer.attributes.put("instant", "true");
            movePlayer.attributes.put("x", "" + 30 * Main.TILE_SIZE);
            movePlayer.attributes.put("y", "" + 15 * Main.TILE_SIZE);
            Event moveCamera = new Event(Event.Type.RELEASE_CAMERA);
            moveCamera.attributes.put("instant", "true");
            Main.changeContext("puzzle1", new Event[]{movePlayer, moveCamera});
        }
    }

    public static class TownPrototype extends AreaPrototype{
        public TownPrototype() {
            super("town1");

            /* Adding things to Area */
            bgm = "Searching.mp3";
            viewport = new Vector2(12 * Main.TILE_SIZE, 12 * Main.TILE_SIZE);
            playerPosition = new Vector2(Main.TILE_SIZE, 8 * Main.TILE_SIZE);
            mapSize = new Vector2(64, 16);
            tint = new Color(1, .8f, 1, 1);
        }

        public void init() {
            /* Entities */
            Entity soldierA = new Entity("soldierA", "soldier", 6 * Main.TILE_SIZE, 10 * Main.TILE_SIZE, true, false);
            Entity soldierB = new Entity("soldierB", "soldier", 6 * Main.TILE_SIZE, 7 * Main.TILE_SIZE, true, false);

            /* Dialogues */
            Dialogue.Line line1 = new Dialogue.Line("Whoah! HABIT said that last puzzle would take anyone at least a millenia!", "Guard", "soldier");
            Dialogue.Line line2 = new Dialogue.Line("Never mind that. You need to come with us!", "Guard", "soldier");
            Event moveSoldierA = new Event(Event.Type.MOVE_ENTITY, "soldierA");
            moveSoldierA.attributes.put("x", "" + Main.TILE_SIZE);
            moveSoldierA.attributes.put("y", "" + 9 * Main.TILE_SIZE);
            Event moveSoldierB = new Event(Event.Type.MOVE_ENTITY, "soldierB");
            moveSoldierB.attributes.put("x", "" + Main.TILE_SIZE);
            moveSoldierB.attributes.put("y", "" + 7 * Main.TILE_SIZE);
            Event moveSoldierA2 = new Event(Event.Type.MOVE_ENTITY, "soldierA", 2);
            moveSoldierA2.attributes.put("x", "" + 8.5 * Main.TILE_SIZE);
            moveSoldierA2.attributes.put("y", "" + 9 * Main.TILE_SIZE);
            Event moveSoldierB2 = new Event(Event.Type.MOVE_ENTITY, "soldierB", 2);
            moveSoldierB2.attributes.put("x", "" + 8.5 * Main.TILE_SIZE);
            moveSoldierB2.attributes.put("y", "" + 7 * Main.TILE_SIZE);
            Event movePlayer = new Event(Event.Type.MOVE_ENTITY, "player", 2);
            movePlayer.attributes.put("x", "" + 8.5 * Main.TILE_SIZE);
            movePlayer.attributes.put("y", "" + 8 * Main.TILE_SIZE);
            Event moveSoldierA3 = new Event(Event.Type.MOVE_ENTITY, "soldierA", 4);
            moveSoldierA3.attributes.put("x", "" + 8.5 * Main.TILE_SIZE);
            moveSoldierA3.attributes.put("y", "" + 14 * Main.TILE_SIZE);
            Event moveSoldierB3 = new Event(Event.Type.MOVE_ENTITY, "soldierB", 4);
            moveSoldierB3.attributes.put("x", "" + 8.5 * Main.TILE_SIZE);
            moveSoldierB3.attributes.put("y", "" + 14 * Main.TILE_SIZE);
            Event movePlayer2 = new Event(Event.Type.MOVE_ENTITY, "player", 4);
            movePlayer2.attributes.put("x", "" + 8.5 * Main.TILE_SIZE);
            movePlayer2.attributes.put("y", "" + 14 * Main.TILE_SIZE);
            Event throne = new Event(Event.Type.CHANGE_CONTEXT, "throne", 6);
            line2.events = new Event[]{moveSoldierA, moveSoldierB, moveSoldierA2, moveSoldierB2, movePlayer, moveSoldierA3, moveSoldierB3, movePlayer2, throne};
            Dialogue capture = new Dialogue("capture", new Dialogue.Line[]{line1, line2});

            /* Adding things to area */
            entities = new Entity[]{soldierA, soldierB};
            dialogues = new Dialogue[]{capture};
            battles = new Battle.BattlePrototype[]{};
        }

        public void loadAssets(AssetManager manager) {
            super.loadAssets(manager);
            manager.load("Searching.mp3", Sound.class);
            manager.load("soldier.png", Texture.class);
        }

        public Context getContext() {
            super.getContext();
            final Area area = new Town1(this);
            if (!Player.getAttribute("captured")) {
                new Event(Event.Type.CUTSCENE).run(area);
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
            return area;
        }
    }
}
