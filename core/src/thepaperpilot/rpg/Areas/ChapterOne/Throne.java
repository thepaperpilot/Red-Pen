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

public class Throne extends Area {

    public Throne(ThronePrototype prototype) {
        super(prototype);
        if (Player.getAttribute("portal")) {
            Event event = new Event(Event.Type.SET_ENTITY_VISIBILITY, "boss");
            event.attributes.put("visible", "false");
            event.run(this);
        }
    }

    public void render(float delta) {
        super.render(delta);

        if (player.getY() < 0) {
            new Event(Event.Type.SHUTDOWN).run(this);
        }
    }

    public static class ThronePrototype extends AreaPrototype {
        public ThronePrototype() {
            super("throne");

            /* Entities */
            Entity pile = new Entity("pile", "pile", 24 * Main.TILE_SIZE, 12 * Main.TILE_SIZE, true, false) {
                int stones = 132;

                public void onTouch(Area area) {
                    if (stones == 132) {
                        new Event(Event.Type.DIALOGUE, "allPapers").run(area);
                    } else if (stones == 1) {
                        new Event(Event.Type.DIALOGUE, "lastPaper").run(area);
                    } else {
                        new Dialogue("", new Dialogue.Line[]{new Dialogue.Line("There are still " + stones + " stones in the pile. Determined, you put another in your pocket.")}).open(area);
                    }
                    stones--;
                }
            };

            Entity battle = new Entity("boss", "joker", 16 * Main.TILE_SIZE, 30 * Main.TILE_SIZE, true, false) {
                public void onTouch(Area area) {
                    new Event(Event.Type.DIALOGUE, "joker").run(area);
                }
            };

            Entity portal = new Entity("portal", "portal", 15 * Main.TILE_SIZE, 30 * Main.TILE_SIZE, true, false) {
                public void onTouch(Area area) {
                    new Event(Event.Type.DIALOGUE, Player.getAttribute("portal") ? "activate" : "portal").run(area);
                }
            };

            /* Dialogues */
            Dialogue allPapersDial = new Dialogue("allPapers", new Dialogue.Line[]{new Dialogue.Line("You see a pile of precisely 132 stones. You pick one up and put it in your pocket.")});

            Dialogue.Line line = new Dialogue.Line("There's only one stone left. With a smug face you pick up the last one and put it in your now bulging pockets, congratulating yourself on a job well done.");
            final Event removePaper = new Event(Event.Type.SET_ENTITY_VISIBILITY, "pile");
            removePaper.attributes.put("visible", "false");
            line.events = new Event[]{removePaper};
            Dialogue lastPaperDial = new Dialogue("lastPaper", new Dialogue.Line[]{line});

            Dialogue.Line line1 = new Dialogue.Line("Wow, now I realize why I was the first boss! Like, I'm honestly ashamed of myself. Well, you've taken my powers, I hope they serve you well. Go on, activate the portal. You use abilities much like you use actions in battle. Be careful though, these abilities are much more difficult than regular actions!", "joker", "joker");
            Dialogue.Line line2 = new Dialogue.Line("This portal will bring you to the overworld for a short time. You can use it talk to someone back home, if you'd like. Use it carefully, though, as it can't be used very often. Good luck...", "joker", "joker");
            final Event removeJoker = new Event(Event.Type.SET_ENTITY_VISIBILITY, "boss");
            removeJoker.attributes.put("visible", "false");
            line2.events = new Event[]{removeJoker, new Event(Event.Type.ADD_ATTRIBUTE, "portal")};
            Dialogue winDial = new Dialogue("win", new Dialogue.Line[]{line1, line2});

            Dialogue portalDialogue = new Dialogue.EntityDialogue("portal", new Dialogue.Line[]{new Dialogue.Line("woah woah woah. What are you trying to do with my portal? You don't have the ability to use it!")}, 4, "boss", new Vector2(20, 0), new Vector2(120, 45), true);

            line1 = new Dialogue.Line("Oh? You're trying to get out of Hell, are you? Well if you hope to do that, you'll need my portal abilities. Unfortunately for you, I won't give them up without a fight.", "joker", "joker");
            line2 = new Dialogue.Line("Well, yeah. You're the boss, that was to be expected. But admittedly, I was expecting someone more... boss-like?", "Player", "player");
            Dialogue.Line line3 = new Dialogue.Line("Trust me, I'm plenty 'boss-like'. Just you wait and see!", "joker", "joker");
            line3.events = new Event[]{new Event(Event.Type.COMBAT, "boss")};
            Dialogue joker = new Dialogue("joker", new Dialogue.Line[]{line1, line2, line3});

            line1 = new Dialogue.Line("You look at the portal. You can vaguely make out what appears to be your university. Do you wish to enter?");
            Dialogue.Option yes = new Dialogue.Option("yes", new Event[]{new Event(Event.Type.COMBAT, "portal")});
            Dialogue.Option no = new Dialogue.Option("no", new Event[]{});
            line1.options = new Dialogue.Option[]{yes, no};
            Dialogue activate = new Dialogue.EntityDialogue("activate", new Dialogue.Line[]{line1}, 0, "portal", new Vector2(20, -20), new Vector2(120, 90), true);

            Dialogue loseDial = new Dialogue.EntityDialogue("lose", new Dialogue.Line[]{new Dialogue.Line("Haha! Told you I wouldn't be so easy! Come try again when you aren't such a joke! Ha")}, 4, "boss", new Vector2(20, -20), new Vector2(120, 45), true);

            line1 = new Dialogue.Line("I'm gonna give you a head's up before starting this battle. I'm going to be creating portals, which are additional enemies. When dealing with multiple enemies, you can click on the one you want to attack to focus on it.", "joker", "joker");
            line2 = new Dialogue.Line("I won't be attacking directly, but the battle won't end until I'm defeated. Not that a runt like you could actually do such a thing. Well good luck anyways, you'll need it.", "joker", "joker");
            line2.events = new Event[]{new Event(Event.Type.NEXT_ATTACK)};
            final Dialogue tutorial = new Dialogue("tutorial", new Dialogue.Line[]{line1, line2});

            /* Enemies */
            final Enemy.EnemyPrototype portalEnemy =  new Enemy.EnemyPrototype("portal", "portal", new Vector2(0, 0), 5, new Attack.AttackPrototype(
                    new String[]{"portal", "magic", "speed", "fast", "swarm", "mystery"},
                    "jingles_SAX16", "portal", Attack.Target.PLAYER, 1, Color.YELLOW, 10, 1, 5, false) {
                @Override
                public void run(Vector2 position, Attack attack) {
                    Attack.Word word = getWord(attack);
                    float y = position.y + MathUtils.random(-50, 50);
                    word.start = new Vector2(position.x, y);
                    word.end = new Vector2(attack.battle.playerPos.x, y);
                    attack.addWord(word);
                }
            });

            Enemy.EnemyPrototype jokerEnemy = new Enemy.EnemyPrototype("joker", "joker", new Vector2(80, 200), 20, new Attack.AttackPrototype(new String[]{},
                    "jingles_SAX16", "portalSpawn", Attack.Target.OTHER, 0, Color.BLACK, 0, 0, 1, false) {
                @Override
                public void run(Vector2 position, Attack attack) {
                    if (attack.battle.turn % 2 == 0) return;
                    Enemy enemy = new Enemy(portalEnemy, attack.battle);
                    enemy.setPosition(position.x + MathUtils.random(50), position.y + MathUtils.randomSign() * MathUtils.random(50, 75));
                    attack.battle.addEnemy(enemy);
                }
            });

            Enemy.EnemyPrototype portalAbilityEnemy = new Enemy.EnemyPrototype("portal", "portal", new Vector2(80, 180), 20, new Attack.AttackPrototype(
                    new String[]{"portal", "magic", "speed", "fast", "swarm", "mystery"},
                    "jingles_SAX16", "portal", Attack.Target.PLAYER, 1, Color.YELLOW, 10, 1.5f, 10, false) {
                @Override
                public void run(Vector2 position, Attack attack) {
                    Attack.Word word = getWord(attack);
                    float y = position.y + MathUtils.random(-50, 50);
                    word.start = new Vector2(position.x, y);
                    word.end = new Vector2(attack.battle.playerPos.x, y);
                    attack.addWord(word);
                }
            });

            /* Battles */
            Battle.BattlePrototype boss = new Battle.BattlePrototype("boss", false) {
                public void start(Battle battle) {
                    new Event(Event.Type.DIALOGUE, "tutorial").run(battle);
                }
            };
            boss.enemies = new Enemy.EnemyPrototype[]{jokerEnemy};
            boss.winEvents = new Event[]{new Event(Event.Type.DIALOGUE, "win")};
            boss.loseEvents = new Event[]{new Event(Event.Type.DIALOGUE, "lose")};
            boss.bgm = "Searching.mp3";

            Battle.BattlePrototype portalAbility = new Battle.BattlePrototype("portal", true);
            portalAbility.enemies = new Enemy.EnemyPrototype[]{portalAbilityEnemy};
            // TODO win event to teleport
            portalAbility.winEvents = new Event[]{new Event(Event.Type.SHUTDOWN)};
            portalAbility.bgm = "Searching.mp3";

            /* Adding things to area */
            entities = new Entity[]{pile, battle, portal};
            dialogues = new Dialogue[]{allPapersDial, lastPaperDial, winDial, loseDial, portalDialogue, joker, activate, tutorial};
            battles = new Battle.BattlePrototype[]{boss, portalAbility};
            bgm = "Come and Find Me.mp3";
            tint = new Color(1, .8f, .8f, 1);
        }

        public void loadAssets(AssetManager manager) {
            manager.load("talker.png", Texture.class);
            manager.load("joker.png", Texture.class);
            manager.load("narrator.png", Texture.class);
            manager.load("pile.png", Texture.class);
            manager.load("portal.png", Texture.class);
            manager.load("Come and Find Me.mp3", Sound.class);
            manager.load("Searching.mp3", Sound.class);
        }

        public Context getContext() {
            return new Throne(this);
        }
    }
}
