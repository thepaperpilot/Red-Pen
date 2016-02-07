package thepaperpilot.rpg.Areas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;
import thepaperpilot.rpg.Context;
import thepaperpilot.rpg.Dialogue;
import thepaperpilot.rpg.Event;
import thepaperpilot.rpg.Main;
import thepaperpilot.rpg.Map.Area;
import thepaperpilot.rpg.Map.ParticleEffectActor;

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
            /* Events */
            Event.EventPrototype movePlayer = new Event.EventPrototype();
            movePlayer.type = "MOVE_PLAYER";
            movePlayer.attributes.put("x", "" + 7 * Main.TILE_SIZE);
            movePlayer.attributes.put("y", "" + -1 * Main.TILE_SIZE);

            Event.EventPrototype nextArea = new Event.EventPrototype();
            nextArea.type = "SHUTDOWN"; // obviously temporary
            nextArea.wait = 3;

            /* Dialogues */
            Dialogue.DialoguePrototype falling = new Dialogue.DialoguePrototype();
            falling.name = "falling";
            Dialogue.LinePrototype line1 = new Dialogue.LinePrototype();
            line1.name = "Player";
            line1.face = "player";
            line1.message = "Well, shit...";
            line1.events = new Event.EventPrototype[]{movePlayer, nextArea};
            falling.lines = new Dialogue.LinePrototype[]{line1};

            /* Adding things to area */
            dialogues = new Dialogue.DialoguePrototype[]{falling};
            bgm = "Sad Descent";
            map = "falling";
            viewport = new Vector2(15 * Main.TILE_SIZE, 15 * Main.TILE_SIZE);
            playerPosition = new Vector2(7 * Main.TILE_SIZE, 17 * Main.TILE_SIZE);
            mapSize = new Vector2(16, 16);
        }

        public void loadAssets(AssetManager manager) {
            manager.load("Sad Descent.ogg", Sound.class);
        }

        public Context getContext() {
            Area area = new Falling(this);
            Event.EventPrototype movePlayer = new Event.EventPrototype();
            movePlayer.type = "MOVE_PLAYER";
            movePlayer.attributes.put("x", "" + 7 * Main.TILE_SIZE);
            movePlayer.attributes.put("y", "" + 7 * Main.TILE_SIZE);
            new Event(movePlayer, area).run();
            Event.EventPrototype welcome = new Event.EventPrototype();
            welcome.type = "DIALOGUE";
            welcome.wait = 4;
            welcome.attributes.put("target", "falling");
            new Event(welcome, area).run();
            Event.EventPrototype stopCamera = new Event.EventPrototype();
            stopCamera.type = "MOVE_CAMERA";
            stopCamera.attributes.put("x", "" + 7.5 * Main.TILE_SIZE);
            stopCamera.attributes.put("y", "" + 7.5 * Main.TILE_SIZE);
            stopCamera.attributes.put("zoom", "" + .5f);
            stopCamera.attributes.put("instant", "true");
            new Event(stopCamera, area).run();
            Event.EventPrototype stopMovement = new Event.EventPrototype();
            stopMovement.type = "CUTSCENE";
            new Event(stopMovement, area).run();
            return area;
        }
    }
}
