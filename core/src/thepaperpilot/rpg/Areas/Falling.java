package thepaperpilot.rpg.Areas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
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

        ParticleEffect falling = new ParticleEffect();
        falling.load(Gdx.files.internal("falling.p"), Gdx.files.internal(""));
        falling.scaleEffect(.5f);
        for (int i = 0; i < 100; i++) {
            falling.update(.1f);
        }

        stage.addActor(new ParticleEffectActor(falling, 320, 180));
    }

    public static class FallingPrototype extends AreaPrototype {
        public FallingPrototype() {
            super("falling");

            /* Events */
            Event.EventPrototype movePlayer = new Event.EventPrototype(Event.Type.MOVE_PLAYER);
            movePlayer.attributes.put("x", "" + 7 * Main.TILE_SIZE);
            movePlayer.attributes.put("y", "" + -1 * Main.TILE_SIZE);

            Event.EventPrototype nextArea = new Event.EventPrototype(Event.Type.CHANGE_CONTEXT, "throne");
            nextArea.wait = 3;

            /* Dialogues */
            Dialogue.DialoguePrototype falling = new Dialogue.DialoguePrototype();
            falling.name = "falling";
            falling.type = Dialogue.DialougeType.SMALL;
            falling.position = new Vector2(320, 240);
            falling.size = new Vector2(120, 30);
            Dialogue.LinePrototype line1 = new Dialogue.LinePrototype();
            line1.name = "Player";
            line1.message = "Well, shit...";
            line1.events = new Event.EventPrototype[]{movePlayer, nextArea};
            falling.lines = new Dialogue.LinePrototype[]{line1};

            /* Adding things to area */
            dialogues = new Dialogue.DialoguePrototype[]{falling};
            bgm = "Sad Descent";
            viewport = new Vector2(15 * Main.TILE_SIZE, 15 * Main.TILE_SIZE);
            playerPosition = new Vector2(7 * Main.TILE_SIZE, 17 * Main.TILE_SIZE);
            mapSize = new Vector2(16, 16);
            tint = new Color(1, .8f, .8f, 1);
        }

        public void loadAssets(AssetManager manager) {
            manager.load("Sad Descent.ogg", Sound.class);
        }

        public Context getContext() {
            Area area = new Falling(this);
            Event.EventPrototype movePlayer = new Event.EventPrototype(Event.Type.MOVE_PLAYER);
            movePlayer.attributes.put("x", "" + 7 * Main.TILE_SIZE);
            movePlayer.attributes.put("y", "" + 7 * Main.TILE_SIZE);
            new Event(movePlayer, area).run();
            Event.EventPrototype welcome = new Event.EventPrototype(Event.Type.DIALOGUE, "falling");
            welcome.wait = 4;
            new Event(welcome, area).run();
            Event.EventPrototype stopCamera = new Event.EventPrototype(Event.Type.MOVE_CAMERA);
            stopCamera.attributes.put("x", "" + 7.5 * Main.TILE_SIZE);
            stopCamera.attributes.put("y", "" + 7.5 * Main.TILE_SIZE);
            stopCamera.attributes.put("zoom", "" + .5f);
            stopCamera.attributes.put("instant", "true");
            new Event(stopCamera, area).run();
            new Event(new Event.EventPrototype(Event.Type.CUTSCENE), area).run();
            return area;
        }
    }
}
