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
import thepaperpilot.rpg.Map.ParticleEffectActor;
import thepaperpilot.rpg.Player;
import thepaperpilot.rpg.UI.Dialogue;

import java.util.ArrayList;
import java.util.Collections;

public class Puzzle1 extends Area {

    public Puzzle1(final PuzzlePrototype prototype) {
        super(prototype);
        if (Player.getAttribute("puzzle1")) {
            new Event(Event.Type.SET_ENTITY_VISIBILITY, "rock1").run(this);
            new Event(Event.Type.SET_ENTITY_VISIBILITY, "rock2").run(this);
            new Event(Event.Type.SET_ENTITY_VISIBILITY, "rock3").run(this);
            new Event(Event.Type.SET_ENTITY_VISIBILITY, "rock4").run(this);
            new Event(Event.Type.SET_ENTITY_VISIBILITY, "rock5").run(this);
        }

        new ParticleEffectActor.EnvironmentParticleEffect("hell", this);
    }

    public void render(float delta) {
        super.render(delta);

        if (cutscene) return;

        if (player.getX() > 5 * Main.TILE_SIZE && !Player.getAttribute("puzzle1Explain")) {
            new Event(Event.Type.DIALOGUE, "puzzle").run(this);
            Player.addAttribute("puzzle1Explain");
        } else if (player.getX() < 0) {
            Main.changeContext("corridor1", new Vector2(17 * Main.TILE_SIZE, 5 * Main.TILE_SIZE), new Vector2(15 * Main.TILE_SIZE, 5 * Main.TILE_SIZE));
        } else if (player.getY() > 31 * Main.TILE_SIZE) {
            Main.changeContext("scroll", new Vector2(3 * Main.TILE_SIZE, Main.TILE_SIZE));
        } else if (player.getX() > 31 * Main.TILE_SIZE) {
            // TODO more puzzle scenes
            Main.changeContext("town1", new Vector2(Main.TILE_SIZE, 8 * Main.TILE_SIZE));
        } else if (player.getY() < 27 * Main.TILE_SIZE && !Player.getAttribute("nm1") && Player.getAttribute("nmScroll")) {
            Player.addAttribute("nm1");
            new Event(Event.Type.DIALOGUE, "nm").run(this);
        }
    }

    public static class PuzzlePrototype extends AreaPrototype {
        public PuzzlePrototype() {
            super("puzzle1");

            /* Adding things to area */
            bgm = "Digital Native.mp3";
            viewport = new Vector2(16 * Main.TILE_SIZE, 16 * Main.TILE_SIZE);
            playerPosition = new Vector2(-Main.TILE_SIZE, 15 * Main.TILE_SIZE);
            mapSize = new Vector2(32, 32);
            tint = new Color(1, .8f, .8f, 1);
        }

        public void init() {
            /* Entities */
            ArrayList<Entity> entities = new ArrayList<Entity>();

            entities.add(new Entity("habit", "demonOld", 7 * Main.TILE_SIZE, 15 * Main.TILE_SIZE, !Player.getAttribute("puzzle1Explain"), false));

            entities.add(new Entity("nm", "talker", 13 * Main.TILE_SIZE, 25 * Main.TILE_SIZE, !Player.getAttribute("nm1") && Player.getAttribute("nmScroll"), false));

            Entity[] buttons = new Entity[18];
            final ArrayList<Entity> remainingButtons = new ArrayList<Entity>();
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 4; j++) {
                    buttons[i * 7 + j] = new Entity("button" + (i * 7 + j), "buttonUp", (13 + 2 * j) * Main.TILE_SIZE, (13 + 2 * i) * Main.TILE_SIZE, true, true) {
                        public void onTouch(Area area) {
                            changeTexture("buttonDown");
                            if (remainingButtons.remove(this)) Main.click();
                            if (remainingButtons.isEmpty()) solvePuzzle(area);
                        }
                    };
                }
            }
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 3; j++) {
                    buttons[4 + 7 * i + j] = new Entity("button" + (5 + 7 * i + j), "buttonUp", (14 + 2 * j) * Main.TILE_SIZE, (14 + 2 * i) * Main.TILE_SIZE, true, true) {
                        public void onTouch(Area area) {
                            changeTexture("buttonDown");
                            if (remainingButtons.remove(this)) Main.click();
                            if (remainingButtons.isEmpty()) solvePuzzle(area);
                        }
                    };
                }
            }
            Collections.addAll(entities, buttons);
            Collections.addAll(remainingButtons, buttons);

            entities.add(new Entity("rock1", "rock", 13 * Main.TILE_SIZE, 29 * Main.TILE_SIZE, true, false));
            entities.add(new Entity("rock2", "rock", 14 * Main.TILE_SIZE, 29 * Main.TILE_SIZE, true, false));
            entities.add(new Entity("rock3", "rock", 24 * Main.TILE_SIZE, 16 * Main.TILE_SIZE, true, false));
            entities.add(new Entity("rock4", "rock", 24 * Main.TILE_SIZE, 15 * Main.TILE_SIZE, true, false));
            entities.add(new Entity("rock5", "rock", 24 * Main.TILE_SIZE, 14 * Main.TILE_SIZE, true, false));

            /* Dialogues */
            Dialogue.Line line1 = new Dialogue.Line("Ha! What courage to hear all that and still come forward", "HABIT", "demonOld");
            Dialogue.Line line2 = new Dialogue.Line("What a shame, though, for it also marks you as the fool you are.", "HABIT", "demonOld");
            Dialogue.Line line3 = new Dialogue.Line("Just to prove how foolish you are I've set up the first of many puzzles designed to torment you. You see before you a grid of switches. One of them unlocks the way forward", "HABIT", "demonOld");
            Event zoom1 = new Event(Event.Type.ENTITY_CAMERA, "habit");
            zoom1.attributes.put("zoom", ".75");
            zoom1.attributes.put("instant", "true");
            line3.events = new Event[]{zoom1};
            Dialogue.Line line4 = new Dialogue.Line("The other buttons.", "HABIT", "demonOld");
            Event zoom2 = new Event(Event.Type.ENTITY_CAMERA, "habit");
            zoom2.attributes.put("zoom", "" + .5f);
            zoom2.attributes.put("instant", "true");
            line4.events = new Event[]{zoom2};
            Dialogue.Line line5 = new Dialogue.Line("Will.", "HABIT", "demonOld");
            Event zoom3 = new Event(Event.Type.ENTITY_CAMERA, "habit");
            zoom3.attributes.put("zoom", "" + .25f);
            zoom3.attributes.put("instant", "true");
            line5.events = new Event[]{zoom3};
            Dialogue.Line line6 = new Dialogue.Line("Not!", "HABIT", "demonOld");
            Event zoom4 = new Event(Event.Type.RELEASE_CAMERA);
            zoom4.attributes.put("instant", "true");
            line6.events = new Event[]{zoom4};
            Dialogue.Line line7 = new Dialogue.Line("Hahahaha! Haha! Sometimes my trickery astounds even myself! And the best part is, the correct switch will always be the last one you press! Muahhaa good luck, living one!", "HABIT", "demonOld");
            Event hideDemon = new Event(Event.Type.SET_ENTITY_VISIBILITY, "habit");
            hideDemon.attributes.put("visible", "false");
            line7.events = new Event[]{hideDemon};
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
                    new Dialogue.SmallDialogue("fight", new Dialogue.Line[]{new Dialogue.Line(bank[MathUtils.random(bank.length - 1)])}, 4, new Vector2(nmEnemy.position.x + 120, nmEnemy.position.y + 10), new Vector2(180, 12), true).open(battle);
                }
            };
            nmFight.enemies = new Enemy.EnemyPrototype[]{nmEnemy};
            Event removeNM = new Event(Event.Type.SET_ENTITY_VISIBILITY, "nm");
            removeNM.attributes.put("visible", "false");
            nmFight.winEvents = nmFight.loseEvents = new Event[]{removeNM};
            nmFight.bgm = "Come and Find Me.mp3";

            /* Adding things to Area */
            this.entities = entities.toArray(new Entity[entities.size()]);
            dialogues = new Dialogue[]{puzzle, nmDialogue, nmScroll};
            battles = new Battle.BattlePrototype[]{nmFight};
        }

        private void solvePuzzle(Area area) {
            if (Player.getAttribute("puzzle1")) return;
            Player.addAttribute("puzzle1");
            // I might want to chain these, but I'd want pauses between them anyways, so...
            new Event(Event.Type.CUTSCENE).run(area);
            Event camera = new Event(Event.Type.LOCK_CAMERA);
            camera.attributes.put("x", "" + 24 * Main.TILE_SIZE);
            camera.attributes.put("y", "" + 15 * Main.TILE_SIZE);
            camera.attributes.put("zoom", ".5");
            camera.attributes.put("instant", "true");
            camera.run(area);
            new Event(Event.Type.SET_ENTITY_VISIBILITY, "rock3", 1).run(area);
            new Event(Event.Type.SET_ENTITY_VISIBILITY, "rock4", 1).run(area);
            new Event(Event.Type.SET_ENTITY_VISIBILITY, "rock5", 1).run(area);
            camera = new Event(Event.Type.LOCK_CAMERA, 2);
            camera.attributes.put("x", "" + 13 * Main.TILE_SIZE);
            camera.attributes.put("y", "" + 29 * Main.TILE_SIZE);
            camera.attributes.put("zoom", ".5");
            camera.attributes.put("instant", "true");
            camera.run(area);
            new Event(Event.Type.SET_ENTITY_VISIBILITY, "rock1", 3).run(area);
            new Event(Event.Type.SET_ENTITY_VISIBILITY, "rock2", 3).run(area);
            new Event(Event.Type.RELEASE_CAMERA, 4).run(area);
            new Event(Event.Type.END_CUTSCENE, 4).run(area);
        }

        public void loadAssets(AssetManager manager) {
            super.loadAssets(manager);
            manager.load("Digital Native.mp3", Sound.class);
            manager.load("Come and Find Me.mp3", Sound.class);
            manager.load("demonOld.png", Texture.class);
            manager.load("rock.png", Texture.class);
            manager.load("buttonUp.png", Texture.class);
            manager.load("buttonDown.png", Texture.class);
            manager.load("talker.png", Texture.class);
        }

        public Context getContext(Vector2 start, Vector2 end) {
            Area area = new Puzzle1(this);
            Event.moveEvent(start, end, area);
            return area;
        }
    }
}
