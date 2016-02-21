package thepaperpilot.rpg.Areas.ChapterOne;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
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

        new ParticleEffectActor.EnvironmentParticleEffect("hell", this);
    }

    public void render(float delta) {
        super.render(delta);

        if (cutscene) return;

        if (player.getX() > 22 * Main.TILE_SIZE) {
            Main.changeContext("puzzle1");
        }
    }

    public static class CorridorPrototype extends AreaPrototype {
        public CorridorPrototype() {
            super("corridor1");

            /* Adding things to area */
            battles = new Battle.BattlePrototype[]{};
            bgm = "Digital Native.mp3";
            viewport = new Vector2(6 * Main.TILE_SIZE, 6 * Main.TILE_SIZE);
            playerStart = playerEnd = new Vector2(3 * Main.TILE_SIZE, 4 * Main.TILE_SIZE);
            mapSize = new Vector2(23, 21);
            tint = new Color(1, .8f, .8f, 1);
        }

        public void init() {
            /* Entities */
            Entity demonOld = new Entity("habit", "demonOld", 16 * Main.TILE_SIZE, 5 * Main.TILE_SIZE, false, false);

            Entity flower = new Entity("flower", "thisiswhyimnotanartist", 17 * Main.TILE_SIZE, 17 * Main.TILE_SIZE, true, false) {
                public void onTouch(Area area) {
                    if (!Player.getAttribute("spare"))
                        new Event(Event.Type.DIALOGUE, "spare").run(area);
                    else new Event(Event.Type.DIALOGUE, "flower").run(area);
                }
            };

            /* Dialogues */
            Dialogue.Line line1 = new Dialogue.Line("Hello Player, welcome to Hell! I'm HABIT, and I'll be your tormentor for your stay.", "HABIT", "demonOld");
            Dialogue.Line line2 = new Dialogue.Line("You can stay here for eternity. Or continue forward, and spend eternity elsewhere. It's nice here, and if you follow me I ...will... torment you. So make sure you choose your next actions wisely.", "HABIT", "demonOld");
            Dialogue.Line line3 = new Dialogue.Line("This place will mess with your head. In that way, it's much like living. But you've left the living, and now will never leave again. In that way, this place is not like living.", "HABIT", "demonOld");
            Dialogue.Line line4 = new Dialogue.Line("Whatever you do now, I hope you regret it. Fortunately, I know you will.", "HABIT", "demonOld");
            Event hideDemon = new Event(Event.Type.SET_ENTITY_VISIBILITY, "habit");
            hideDemon.attributes.put("visible", "false");
            line4.events = new Event[]{hideDemon, new Event(Event.Type.END_CUTSCENE)};
            Dialogue welcome = new Dialogue("welcome", new Dialogue.Line[]{line1, line2, line3, line4});

            line1 = new Dialogue.Line("You see a wilting flower next to a spare and a sign. The sign reads 'Cause of death: Mercy'");
            line2 = new Dialogue.Line("Pick up the spare?");
            line2.options = new Dialogue.Option[]{new Dialogue.Option("Yes", new Event[]{new Event(Event.Type.ADD_ATTRIBUTE, "spare"), new Event(Event.Type.DUMMY) {
                public void run(Context context) {
                    Player.addInventory("spare");
                    Player.save(((Area) context).player.getX(), ((Area) context).player.getY());
                }
            }}), new Dialogue.Option("No", new Event[]{})};
            Dialogue spare = new Dialogue("spare", new Dialogue.Line[]{line1, line2});

            line1 = new Dialogue.Line("You see a wilting flower next to a sign. The sign reads 'Cause of death: Mercy'");
            Dialogue flowerDial = new Dialogue("flower", new Dialogue.Line[]{line1});

            /* Adding things to area */
            entities = new Entity[]{demonOld, flower};
            dialogues = new Dialogue[]{welcome, spare, flowerDial};
        }

        public Context getContext(Vector2 start, Vector2 end) {
            Area area = new Corridor1(this);
            Event event = Event.moveEvent(start, end, area);
            if (!Player.getAttribute("corridor1")) {
                Player.addAttribute("corridor1");
                Event demonShow = new Event(Event.Type.SET_ENTITY_VISIBILITY, "habit");
                demonShow.attributes.put("visible", "true");
                Event move = new Event(Event.Type.MOVE_ENTITY, "habit", 2);
                move.attributes.put("x", "" + 5 * Main.TILE_SIZE);
                move.attributes.put("y", "" + 5 * Main.TILE_SIZE);
                move.next = new Event[]{new Event(Event.Type.DIALOGUE, "welcome")};
                event.next = new Event[]{demonShow, new Event(Event.Type.CUTSCENE), move};
            }
            return area;
        }
    }

}
