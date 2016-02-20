package thepaperpilot.rpg.Areas.ChapterOne;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import thepaperpilot.rpg.Context;
import thepaperpilot.rpg.Event;
import thepaperpilot.rpg.Main;
import thepaperpilot.rpg.Map.Area;
import thepaperpilot.rpg.Map.ParticleEffectActor;
import thepaperpilot.rpg.UI.Dialogue;

public class Falling extends Area {

    public Falling(FallingPrototype prototype) {
        super(prototype);

        new ParticleEffectActor.EnvironmentParticleEffect("falling", this);
    }

    public static class FallingPrototype extends AreaPrototype {
        public FallingPrototype() {
            super("falling");

            /* Adding things to area */
            bgm = "Arpanauts.mp3";
            viewport = new Vector2(15 * Main.TILE_SIZE, 15 * Main.TILE_SIZE);
            playerPosition = new Vector2(7 * Main.TILE_SIZE, 17 * Main.TILE_SIZE);
            mapSize = new Vector2(16, 16);
            tint = new Color(1, .8f, .8f, 1);
        }

        public void init() {
            /* Dialogues */
            Dialogue.Line line1 = new Dialogue.Line("Well, shit...");
            Event movePlayer = new Event(Event.Type.MOVE_ENTITY, "player");
            movePlayer.attributes.put("x", "" + 7 * Main.TILE_SIZE);
            movePlayer.attributes.put("y", "" + -1 * Main.TILE_SIZE);
            movePlayer.next = new Event[]{new Event(Event.Type.CHANGE_CONTEXT, "corridor1")};
            line1.events = new Event[]{movePlayer};
            Dialogue falling = new Dialogue.EntityDialogue("falling", new Dialogue.Line[]{line1}, 2, "player", new Vector2(20, 10), new Vector2(120, 20), false);

            /* Adding things to area */
            dialogues = new Dialogue[]{falling};
        }

        public void loadAssets(AssetManager manager) {
            super.loadAssets(manager);
            manager.load("Arpanauts.mp3", Sound.class);
        }

        public Context getContext(Vector2 start, Vector2 end) {
            Area area = new Falling(this);
            Event movePlayer = new Event(Event.Type.MOVE_ENTITY, "player");
            movePlayer.attributes.put("x", "" + 7 * Main.TILE_SIZE);
            movePlayer.attributes.put("y", "" + 7 * Main.TILE_SIZE);
            movePlayer.run(area);
            new Event(Event.Type.DIALOGUE, "falling", 4).run(area);
            Event stopCamera = new Event(Event.Type.LOCK_CAMERA);
            stopCamera.attributes.put("x", "" + 7.5 * Main.TILE_SIZE);
            stopCamera.attributes.put("y", "" + 7.5 * Main.TILE_SIZE);
            stopCamera.attributes.put("zoom", "" + .5f);
            stopCamera.attributes.put("instant", "true");
            stopCamera.run(area);
            new Event(Event.Type.CUTSCENE).run(area);
            return area;
        }
    }
}
