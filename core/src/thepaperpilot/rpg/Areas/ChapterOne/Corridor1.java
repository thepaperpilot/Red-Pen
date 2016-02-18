package thepaperpilot.rpg.Areas.ChapterOne;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import thepaperpilot.rpg.Battles.Battle;
import thepaperpilot.rpg.Context;
import thepaperpilot.rpg.Event;
import thepaperpilot.rpg.Main;
import thepaperpilot.rpg.Map.Area;
import thepaperpilot.rpg.Map.Entity;
import thepaperpilot.rpg.Map.ParticleEffectActor;
import thepaperpilot.rpg.Player;
import thepaperpilot.rpg.UI.Dialogue;

public class Corridor1 extends Area {

    public Corridor1(CorridorPrototype prototype) {
        super(prototype);

        ParticleEffect hell = new ParticleEffect();
        hell.load(Gdx.files.internal("hell.p"), Gdx.files.internal(""));
        hell.scaleEffect(Main.TILE_SIZE / Math.max(prototype.mapSize.x, prototype.mapSize.y));
        for (int i = 0; i < 100; i++) {
            hell.update(.1f);
        }

        stage.addActor(new ParticleEffectActor(hell, 320, 180) {
            public void act(float delta) {
                super.act(delta);
                Vector3 pos = camera.position;
                effect.setPosition(-pos.x + 320, -pos.y + 180);
                effect.getEmitters().first().getXOffsetValue().setLow(pos.x);
                effect.getEmitters().first().getYOffsetValue().setLow(pos.y);
            }
        });
    }

    public void render(float delta) {
        super.render(delta);

        if (player.getX() > 16 * Main.TILE_SIZE) {
            new Event(Event.Type.CHANGE_CONTEXT, "puzzle1").run(this);
        }
    }

    public static class CorridorPrototype extends AreaPrototype {
        public CorridorPrototype() {
            super("corridor1");

            /* Entities */
            Entity demonOld = new Entity("habit", "demonOld", 16 * Main.TILE_SIZE, 5 * Main.TILE_SIZE, false, false);

            /* Dialogues */
            Dialogue.Line line1 = new Dialogue.Line("Hello Player, welcome to Hell! I'm HABIT, and I'll be your tormentor for your stay.", "HABIT", "demonOld");
            Dialogue.Line line2 = new Dialogue.Line("You can stay here for eternity. Or continue forward, and spend eternity elsewhere. It's nice here, and if you follow me I ...will... torment you. So make sure you choose your next actions wisely.", "HABIT", "demonOld");
            Dialogue.Line line3 = new Dialogue.Line("This place will mess with your head. In that way, it's much like living. But you've left the living, and now will never leave again. In that way, this place is not like living.", "HABIT", "demonOld");
            Dialogue.Line line4 = new Dialogue.Line("Whatever you do now, I hope you regret it. Fortunately, I know you will.", "HABIT", "demonOld");
            Event hideDemon = new Event(Event.Type.SET_ENTITY_VISIBILITY, "habit");
            hideDemon.attributes.put("visible", "false");
            line4.events = new Event[]{hideDemon, new Event(Event.Type.END_CUTSCENE)};
            Dialogue welcome = new Dialogue("welcome", new Dialogue.Line[]{line1, line2, line3, line4});

            /* Adding things to area */
            entities = new Entity[]{demonOld};
            dialogues = new Dialogue[]{welcome};
            battles = new Battle.BattlePrototype[]{};
            bgm = "Digital Native.mp3";
            viewport = new Vector2(6 * Main.TILE_SIZE, 6 * Main.TILE_SIZE);
            playerPosition = new Vector2(3 * Main.TILE_SIZE, 4 * Main.TILE_SIZE);
            mapSize = new Vector2(17, 10);
            tint = new Color(1, .8f, .8f, 1);
        }

        public void loadAssets(AssetManager manager) {
            super.loadAssets(manager);
            manager.load("demonOld.png", Texture.class);
            manager.load("Digital Native.mp3", Sound.class);
        }

        public Context getContext() {
            Area area = new Corridor1(this);
            if (!Player.getAttribute("corridor1")) {
                Player.addAttribute("corridor1");
                Event demonShow = new Event(Event.Type.SET_ENTITY_VISIBILITY, "habit");
                demonShow.attributes.put("visible", "true");
                demonShow.run(area);
                new Event(Event.Type.CUTSCENE).run(area);
                Event move = new Event(Event.Type.MOVE_ENTITY, "habit", 2);
                move.attributes.put("x", "" + 5 * Main.TILE_SIZE);
                move.attributes.put("y", "" + 5 * Main.TILE_SIZE);
                move.run(area);
                new Event(Event.Type.DIALOGUE, "welcome", 5).run(area);
            }
            return area;
        }
    }

}
