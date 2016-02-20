package thepaperpilot.rpg.Areas.ChapterOne;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import thepaperpilot.rpg.Battles.Attack;
import thepaperpilot.rpg.Context;
import thepaperpilot.rpg.Event;
import thepaperpilot.rpg.Main;
import thepaperpilot.rpg.Map.Area;
import thepaperpilot.rpg.Map.Entity;
import thepaperpilot.rpg.Map.ParticleEffectActor;
import thepaperpilot.rpg.Player;
import thepaperpilot.rpg.UI.Dialogue;

public class ScrollRoom extends Area {

    public ScrollRoom(ScrollPrototype prototype) {
        super(prototype);

        new ParticleEffectActor.EnvironmentParticleEffect("scroll", this);
    }

    public void render(float delta) {
        super.render(delta);

        if (cutscene) return;

        if (player.getY() < 0) {
            Main.changeContext("puzzle1", new Vector2(13.5f * Main.TILE_SIZE, 32 * Main.TILE_SIZE), new Vector2(13.5f * Main.TILE_SIZE, 30 * Main.TILE_SIZE));
        }
    }

    public static class ScrollPrototype extends AreaPrototype{
        public ScrollPrototype() {
            super("scroll");

            /* Adding things to area */
            bgm = "Arpanauts.mp3";
            viewport = new Vector2(4 * Main.TILE_SIZE, 4 * Main.TILE_SIZE);
            playerPosition = new Vector2(3 * Main.TILE_SIZE, -Main.TILE_SIZE);
            mapSize = new Vector2(7, 8);
            tint = new Color(1, .8f, 1, 1);
        }

        public void init() {
/* Entities */
            Entity scroll = new Entity("scroll", "scroll", 3 * Main.TILE_SIZE, 5 * Main.TILE_SIZE, true, false) {
                public void onTouch(Area area) {
                    new Event(Event.Type.DIALOGUE, "scroll").run(area);
                }
            };

            /* Dialogues */
            Dialogue.Line line1 = new Dialogue.Line("You pick up the scroll");
            Dialogue.Line line2 = new Dialogue.Line("The scroll reads 'HOW TO SPELL AWAY THE NMNMNMs'");
            Event addAttack = new Event(Event.Type.DUMMY) {
                public void run(Context context) {
                    Attack attack = new Attack(Attack.prototypes.get("nmScroll"));
                    Player.addInventory(attack);
                    Player.addAttack(attack);
                    Player.addAttribute("nmScroll");
                    Player.save();
                }
            };
            Event hideScroll = new Event(Event.Type.SET_ENTITY_VISIBILITY, "scroll");
            hideScroll.attributes.put("visible", "false");
            line2.events = new Event[]{addAttack, hideScroll};
            Dialogue scrollDialogue = new Dialogue("scroll", new Dialogue.Line[]{line1, line2});

            /* Adding things to Area */
            entities = new Entity[]{scroll};
            dialogues = new Dialogue[]{scrollDialogue};
        }

        public void loadAssets(AssetManager manager) {
            super.loadAssets(manager);
            manager.load("Arpanauts.mp3", Sound.class);
            manager.load("scroll.png", Texture.class);
        }

        public Context getContext(Vector2 start, Vector2 end) {
            Area area = new ScrollRoom(this);
            Event endPlayer = Event.moveEvent(start, end, area);
            if (Player.getAttribute("nmScroll")) {
                Event scroll = new Event(Event.Type.SET_ENTITY_VISIBILITY, "scroll");
                scroll.attributes.put("visible", "false");
                endPlayer.next = new Event[]{scroll, new Event(Event.Type.END_CUTSCENE)};
            }
            return area;
        }
    }
}
