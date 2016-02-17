package thepaperpilot.rpg.Areas.ChapterOne;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import thepaperpilot.rpg.Battles.Attack;
import thepaperpilot.rpg.Battles.Battle;
import thepaperpilot.rpg.Battles.Enemy;
import thepaperpilot.rpg.Context;
import thepaperpilot.rpg.Event;
import thepaperpilot.rpg.Main;
import thepaperpilot.rpg.Map.Area;
import thepaperpilot.rpg.Map.Entity;
import thepaperpilot.rpg.Player;
import thepaperpilot.rpg.UI.Dialogue;

public class Puzzle1 extends Area {

    public Puzzle1(PuzzlePrototype prototype) {
        super(prototype);
    }

    public void render(float delta) {
        super.render(delta);

        if (player.getX() > 5 * Main.TILE_SIZE && !Player.getPuzzle1Explain()) {
            new Event(Event.Type.DIALOGUE, "puzzle").run(this);
            Player.setPuzzle1Explain(true);
        } else if (player.getX() < 0) {
            Event movePlayer = new Event(Event.Type.MOVE_PLAYER);
            movePlayer.attributes.put("instant", "true");
            movePlayer.attributes.put("x", "" + 15 * Main.TILE_SIZE);
            movePlayer.attributes.put("y", "" + 5 * Main.TILE_SIZE);
            Event moveCamera = new Event(Event.Type.RELEASE_CAMERA);
            moveCamera.attributes.put("instant", "true");
            Main.changeContext("corridor1", new Event[]{movePlayer, moveCamera});
        } else if (player.getY() > 31 * Main.TILE_SIZE) {
            Main.changeContext("scroll");
        } else if (player.getX() > 31 * Main.TILE_SIZE) {
            // TODO more puzzle scenes, and the town
            Main.changeContext("throne");
        } else if (player.getY() < 27 * Main.TILE_SIZE && !Player.getNM1() && Player.getNMScroll()) {
            Player.setNM1(true);
            new Event(Event.Type.DIALOGUE, "nm").run(this);
        }
    }

    public static class PuzzlePrototype extends AreaPrototype {
        public PuzzlePrototype() {
            super("puzzle1");

            /* Entities */
            Entity habit = new Entity("habit", "demonOld", 7 * Main.TILE_SIZE, 15 * Main.TILE_SIZE, true, false);

            Entity nm = new Entity("nm", "talker", 13 * Main.TILE_SIZE, 25 * Main.TILE_SIZE, false, false);

            /* Dialogues */
            Dialogue.Line line1 = new Dialogue.Line("Ha! What courage to hear all that and still come forward", "HABIT", "demonOld");
            Dialogue.Line line2 = new Dialogue.Line("What a shame, though, for it also marks you as the fool you are.", "HABIT", "demonOld");
            Dialogue.Line line3 = new Dialogue.Line("Just to prove how foolish you are I've set up the first of many puzzles designed to torment you. You see before you a grid of switches. One of them unlocks the way forward", "HABIT", "demonOld");
            Event zoom1 = new Event(Event.Type.ENTITY_CAMERA, "habit");
            zoom1.attributes.put("zoom", "" + .75f);
            zoom1.attributes.put("instant", "" + true);
            line3.events = new Event[]{zoom1};
            Dialogue.Line line4 = new Dialogue.Line("The other buttons.", "HABIT", "demonOld");
            Event zoom2 = new Event(Event.Type.ENTITY_CAMERA, "habit");
            zoom2.attributes.put("zoom", "" + .5f);
            zoom2.attributes.put("instant", "" + true);
            line4.events = new Event[]{zoom2};
            Dialogue.Line line5 = new Dialogue.Line("Will.", "HABIT", "demonOld");
            Event zoom3 = new Event(Event.Type.ENTITY_CAMERA, "habit");
            zoom3.attributes.put("zoom", "" + .25f);
            zoom3.attributes.put("instant", "" + true);
            line5.events = new Event[]{zoom3};
            Dialogue.Line line6 = new Dialogue.Line("Not!", "HABIT", "demonOld");
            Event zoom4 = new Event(Event.Type.ENTITY_CAMERA, "habit");
            zoom4.attributes.put("zoom", "" + 1);
            zoom4.attributes.put("instant", "" + true);
            line6.events = new Event[]{zoom4};
            Dialogue.Line line7 = new Dialogue.Line("Hahahaha! Haha! Sometimes my trickery astounds even myself! And the best part is, the correct switch will always be the last one you press! Muahhaa good luck, living one!", "HABIT", "demonOld");
            Event hideDemon = new Event(Event.Type.SET_ENTITY_VISIBILITY, "habit");
            hideDemon.attributes.put("visible", "false");
            line7.events = new Event[]{hideDemon, new Event(Event.Type.RELEASE_CAMERA)};
            Dialogue puzzle = new Dialogue("puzzle", new Dialogue.Line[]{line1, line2, line3, line4, line5, line6, line7});

            line1 = new Dialogue.Line("nmnmnnm mnmnmnnmn nmnmnmnm", "nm", "talker");
            line2 = new Dialogue.Line("nmnmn nnmn nmnmnmnm nmnmnnmnmnnmnnm", "nm", "talker");
            line2.events = new Event[]{new Event(Event.Type.COMBAT, "nm")};
            Dialogue nmDialogue = new Dialogue("nm", new Dialogue.Line[]{line1, line2});

            line1 = new Dialogue.Line("Ah! Thank you! I had been cursed, but it is now gone! Thank you many times, that scroll you have there has been incredibly helpful", "nm", "talker");
            line2 = new Dialogue.Line("Hey, look... There are others like me. And honestly, most are a lot stronger than me. Could you help them out by reversing the curse on them as well? It'd mean a lot to them, and by extension me. They're... confused in that state, and will fight back. It's a lot to ask, so I thank you now.", "nm", "talker");
            line3 = new Dialogue.Line("And I suppose you probably want some sort of reward, for your efforts? Well, here you go. You can use this to attack things, and it should be stronger than what you already have. You can customize what actions you bring into battle in the gear menu by pressing 'ESC'. Just remember you can only select up to 5.", "nm", "talker");
            line3.events = new Event[]{new Event(Event.Type.DUMMY) {
                public void run(Context context) {
                    Player.addInventory("stick");
                    Player.save();
                }
            }};
            Dialogue nmScroll = new Dialogue("nmScroll", new Dialogue.Line[]{line1, line2, line3});

            /* Enemies */
            final Enemy.EnemyPrototype nmEnemy = new Enemy.EnemyPrototype("nm", "talker", new Vector2(80, 180), 20, new Attack.AttackPrototype(
                    new String[]{"n", "m"},
                    "jingles_SAX16", "nm", Attack.Target.PLAYER, 1, Color.CORAL, 2, .3f, 30, false) {
                @Override
                public void run(Vector2 position, Attack attack) {
                    Attack.Word word = getWord(attack);
                    word.start = new Vector2(position.x + 10, MathUtils.random(360));
                    word.end = attack.battle.playerPos;
                    attack.addWord(word);
                }
            });

            /* Battles */
            Battle.BattlePrototype nmFight = new Battle.BattlePrototype("nm", false) {
                String[] bank = new String[]{"nmnmnnnmmmmn", "nmnmn nmnmnmnmnmn nmnmn", "nnnnnmmmmmmmm", "nmnmnmnmnm nmnmnmnm"};

                public void update(Battle battle) {
                    battle.addDialogue(new Dialogue.SmallDialogue("fight", new Dialogue.Line[]{new Dialogue.Line(bank[MathUtils.random(bank.length - 1)])}, 4, new Vector2(nmEnemy.position.x + 120, nmEnemy.position.y + 10), new Vector2(180, 12), true));
                }
            };
            nmFight.enemies = new Enemy.EnemyPrototype[]{nmEnemy};
            Event removeNM = new Event(Event.Type.SET_ENTITY_VISIBILITY, "nm");
            removeNM.attributes.put("visible", "false");
            nmFight.winEvents = nmFight.loseEvents = new Event[]{removeNM};
            nmFight.bgm = "Come and Find Me.mp3";

            /* Adding things to area */
            entities = new Entity[]{habit, nm};
            dialogues = new Dialogue[]{puzzle, nmDialogue, nmScroll};
            battles = new Battle.BattlePrototype[]{nmFight};
            bgm = "Digital Native.mp3";
            viewport = new Vector2(16 * Main.TILE_SIZE, 16 * Main.TILE_SIZE);
            playerPosition = new Vector2(Main.TILE_SIZE, 15 * Main.TILE_SIZE);
            mapSize = new Vector2(32, 32);
            tint = new Color(1, .8f, .8f, 1);
        }

        public void loadAssets(AssetManager manager) {
            manager.load("Digital Native.mp3", Sound.class);
            manager.load("Come and Find Me.mp3", Sound.class);
            manager.load("demonOld.png", Texture.class);
            manager.load("rock.png", Texture.class);
            manager.load("talker.png", Texture.class);
        }

        public Context getContext() {
            Area area = new Puzzle1(this);
            if (Player.getPuzzle1Explain()) {
                Event demon = new Event(Event.Type.SET_ENTITY_VISIBILITY, "habit");
                demon.attributes.put("visible", "false");
                demon.run(area);
            }
            if (!Player.getNM1() && Player.getNMScroll()) {
                Event nm = new Event(Event.Type.SET_ENTITY_VISIBILITY, "nm");
                nm.attributes.put("visible", "true");
                nm.run(area);
            }
            return area;
        }
    }
}
