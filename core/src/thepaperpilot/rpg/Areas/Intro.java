package thepaperpilot.rpg.Areas;

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
import thepaperpilot.rpg.Dialogue;
import thepaperpilot.rpg.Event;
import thepaperpilot.rpg.Main;
import thepaperpilot.rpg.Map.Area;
import thepaperpilot.rpg.Map.Entity;

public class Intro extends Area.AreaPrototype {
    public Intro() {
        /* Events */
        Event.EventPrototype next = new Event.EventPrototype();
        next.type = "NEXT_ATTACK";

        Event.EventPrototype startDiscussion = new Event.EventPrototype();
        startDiscussion.type = "DIALOGUE";
        startDiscussion.attributes.put("target", "discussion");

        Event.EventPrototype healPlayer = new Event.EventPrototype();
        healPlayer.type = "HEAL_PLAYER";

        Event.EventPrototype falling = new Event.EventPrototype();
        falling.type = "CHANGE_CONTEXT";
        falling.attributes.put("target", "falling");

        Event.EventPrototype satanAppear = new Event.EventPrototype();
        satanAppear.type = "SET_ENTITY_VISIBILITY";
        satanAppear.attributes.put("target", "satan");
        satanAppear.attributes.put("visible", "true");

        Event.EventPrototype fight = new Event.EventPrototype();
        fight.type = "COMBAT";
        fight.attributes.put("target", "satan");
        fight.wait = 1;

        /* Entities */
        Entity.EntityPrototype satanEntity = new Entity.EntityPrototype("satan", "satan", 3 * Main.TILE_SIZE, 6 * Main.TILE_SIZE, false);

        /* Dialogues */
        final Dialogue.DialoguePrototype tutorial = new Dialogue.DialoguePrototype();
        tutorial.name = "tutorial";
        Dialogue.LinePrototype line1 = new Dialogue.LinePrototype();
        line1.name = "Satan";
        line1.face = "satan";
        line1.message = "Alright! I have given you the power to rewrite the world around you! With limitations, of course. For now let's show you how to fight with this power. ";
        Dialogue.LinePrototype line2 = new Dialogue.LinePrototype();
        line2.name = "Satan";
        line2.face = "satan";
        line2.message = "First you select an attack- for now, you can knife, heal, and run. Then in the attack phase you'll need to type out words as they appear on the screen. Some are from your attack, and others are from the enemy. Type enemy words to stop their attack and type yours to complete them successfully.";
        Dialogue.LinePrototype line3 = new Dialogue.LinePrototype();
        line3.name = "Satan";
        line3.face = "satan";
        line3.message = "The first to 0 health loses! Don't worry, I'll go easy on you... for now.";
        line3.events = new Event.EventPrototype[]{next};
        tutorial.lines = new Dialogue.LinePrototype[]{line1, line2, line3};

        final Dialogue.DialoguePrototype discussion = new Dialogue.DialoguePrototype();
        discussion.name = "discussion";
        line1 = new Dialogue.LinePrototype();
        line1.name = "Satan";
        line1.face = "satan";
        line1.message = "Good fight! It seems you have already gotten the hang of using your new power. Now for my end of the deal.";
        line2 = new Dialogue.LinePrototype();
        line2.name = "Player";
        line2.face = "player";
        line2.message = "What?! I thought the whole point was you get my soul after I die? I'm still alive!";
        line3 = new Dialogue.LinePrototype();
        line3.name = "Satan";
        line3.face = "satan";
        line3.message = "It would seem someone didn't read the contract they were signing, now did they... Your soul is mine. You're in my world, now!";
        line3.events = new Event.EventPrototype[]{falling};
        discussion.lines = new Dialogue.LinePrototype[]{line1, line2, line3};

        final Dialogue.DialoguePrototype welcome = new Dialogue.DialoguePrototype();
        welcome.name = "welcome";
        line1 = new Dialogue.LinePrototype();
        line1.name = "Player";
        line1.face = "player";
        line1.message = "God, writing is hard! This reads like some shitty fan fic. I'll never be good enough to publish.";
        line1.events = new Event.EventPrototype[]{satanAppear};
        line2 = new Dialogue.LinePrototype();
        line2.name = "Satan";
        line2.face = "satan";
        line2.message = "How much would you like to be able to write? hmm?";
        line3 = new Dialogue.LinePrototype();
        line3.name = "Player";
        line3.face = "player";
        line3.message = "Oh wow, that was unexpected! Let me guess, I can sign off my soul in exchange for, like, super awesome writing powers?";
        Dialogue.LinePrototype line4 = new Dialogue.LinePrototype();
        line4.name = "Satan";
        line4.face = "satan";
        line4.message = "Ha, not just super awesome, but better! I can give you the power to write the world the way you see it! Write your story in real life!";
        Dialogue.LinePrototype line5 = new Dialogue.LinePrototype();
        line5.name = "Player";
        line5.face = "player";
        line5.message = "Huh, that is better than super awesome. But is it eternity in hell better? I'm not convinced.";
        Dialogue.LinePrototype line6 = new Dialogue.LinePrototype();
        line6.name = "Satan";
        line6.face = "satan";
        line6.message = "Well you should be, because I'm literally Satan.";
        Dialogue.LinePrototype line7 = new Dialogue.LinePrototype();
        line7.name = "Player";
        line7.face = "player";
        line7.message = "Can't argue with that logic. Where do I sign?";
        line7.events = new Event.EventPrototype[]{fight};
        welcome.lines = new Dialogue.LinePrototype[]{line1, line2, line3, line4, line5, line6, line7};

        /* Attacks */
        Attack.AttackPrototype attack = new Attack.AttackPrototype(new String[]{"die", "attack", "knife", "shiv", "murder", "kill", "merciless", "genocide"}, "jingles_SAX16", "knife", Attack.Target.ENEMY, 2, Color.RED, 6, true) {
            int attacks = 3;
            float time = 2;
            public void reset() {
                attacks = 3;
                time = 2;
            }

            @Override
            public boolean update(float delta, Attack attack) {
                time += delta;
                if (time > 2 && attacks > 0 && attack.battle.enemies.size() > 0) {
                    attacks--;
                    time -= 2;
                    Attack.Word word = getWord(attack);
                    word.start = new Vector2(attack.battle.playerPos.x + MathUtils.random(50) - 25, attack.battle.playerPos.y - MathUtils.random(50));
                    word.end = word.start.cpy().add(0, 20);
                    attack.addWord(word);
                    return attacks == 0;
                }
                return false;
            }
        };

        Attack.AttackPrototype heal = new Attack.AttackPrototype(new String[]{"help", "heal", "magic", "power", "assist", "you matter"}, "jingles_SAX15", "heal", Attack.Target.PLAYER, -5, Color.GREEN, 9, true) {
            int attacks = 2;
            float time;
            public void reset() {
                attacks = 2;
                time = 0;
            }

            @Override
            public boolean update(float delta, Attack attack) {
                time += delta;
                if (time > 2 && attacks > 0) {
                    attacks--;
                    time -= 2;
                    Attack.Word word = getWord(attack);
                    word.start = new Vector2(attack.battle.playerPos.x + MathUtils.random(50) - 25, attack.battle.playerPos.y - MathUtils.random(50));
                    word.end = word.start.cpy().add(0, 10);
                    if (attacks == 0) attack.done = true;
                    attack.addWord(word);
                    return attacks == 0;
                }
                return false;
            }
        };

        Attack.AttackPrototype run = new Attack.AttackPrototype(new String[]{"help!", "escape...", "run...", "away...", "run away..", "get away.."}, "jingles_SAX03", "run", Attack.Target.OTHER, 0, Color.TEAL, 20, true) {
            @Override
            public boolean update(float delta, Attack attack) {
                if (!attack.done) {
                    Attack.Word word = getWord(attack);
                    word.start = new Vector2(attack.battle.playerPos.x + MathUtils.random(50) - 25, attack.battle.playerPos.y - MathUtils.random(50));
                    word.end = word.start.cpy().add(0, 10);
                    attack.done = true;
                    attack.addWord(word);
                    return true;
                }
                return false;
            }

            public void run(Attack.Word word) {
                word.attack.battle.escape();
                super.run(word);
            }
        };

        Attack.AttackPrototype satanAttack = new Attack.AttackPrototype(new String[]{"hell", "satan", "death", "die", "sin", "death", "immoral", "evil", "despicable", "mean", "horrible", "rude", "afterlife", "dead", "never"}, "jingles_SAX16", "satan", Attack.Target.PLAYER, 1, Color.RED, 8, false) {
            int attacks = 2;
            float time = 0;

            public void reset() {
                attacks = 2;
                time = 3;
            }

            @Override
            public boolean update(float delta, Attack attack) {
                time += delta;
                if (time > 6 && attacks > 0 && attack.battle.enemies.size() > 0) {
                    attacks--;
                    time -= 6;
                    for (int i = 0; i < 2; i++) {
                        for (int j = 0; j < 2; j++) {
                            Attack.Word word = getWord(attack);
                            word.start = new Vector2(attack.battle.playerPos.x - 80 + 160 * i, attack.battle.playerPos.y - 80 + 160 * j);
                            word.end = attack.battle.playerPos.cpy();
                            attack.addWord(word);
                        }
                    }
                    return attacks == 0;
                }
                return false;
            }
        };

        /* Enemies */
        Enemy.EnemyPrototype satanEnemy = new Enemy.EnemyPrototype("satan", "satan", 320, 320, 200);
        satanEnemy.attacks = new Attack.AttackPrototype[]{satanAttack};

        /* Battles */
        Battle.BattlePrototype satan = new Battle.BattlePrototype("satan", false) {
            public void start(Battle battle) {
                Event.EventPrototype prototype = new Event.EventPrototype();
                prototype.type = "DIALOGUE";
                prototype.attributes.put("target", "tutorial");
                new Event(prototype, battle).run();
            }
        };
        satan.enemies = new Enemy.EnemyPrototype[]{satanEnemy};
        satan.winEvents = satan.loseEvents = new Event.EventPrototype[]{startDiscussion, healPlayer};
        satan.bgm = "Sad Descent";
        satan.playerPosition = new Vector2(320, 180);

        /* Adding things to area */
        entities = new Entity.EntityPrototype[]{satanEntity};
        dialogues = new Dialogue.DialoguePrototype[]{tutorial, discussion, welcome};
        battles = new Battle.BattlePrototype[]{satan};
        attacks = new Attack.AttackPrototype[]{attack, heal, run};
        bgm = "Wacky Waiting";
        map = "intro";
        viewport = new Vector2(8 * Main.TILE_SIZE, 8 * Main.TILE_SIZE);
        playerPosition = new Vector2(6 * Main.TILE_SIZE, 4 * Main.TILE_SIZE);
        mapSize = new Vector2(8, 8);
        tint = new Color(1, .8f, 1, 1);
    }

    public void loadAssets(AssetManager manager) {
        manager.load("satan.png", Texture.class);
        manager.load("Wacky Waiting.ogg", Sound.class);
        manager.load("Sad Descent.ogg", Sound.class);
    }

    public Context getContext() {
        Area area = new Area(this);
        Event.EventPrototype stopCamera = new Event.EventPrototype();
        stopCamera.type = "MOVE_CAMERA";
        stopCamera.attributes.put("x", "" + 4 * Main.TILE_SIZE);
        stopCamera.attributes.put("y", "" + 4 * Main.TILE_SIZE);
        stopCamera.attributes.put("zoom", "" + 1);
        stopCamera.attributes.put("instant", "true");
        new Event(stopCamera, area).run();
        Event.EventPrototype stopMovement = new Event.EventPrototype();
        stopMovement.type = "CUTSCENE";
        new Event(stopMovement, area).run();
        Event.EventPrototype dialogue = new Event.EventPrototype();
        dialogue.type = "DIALOGUE";
        dialogue.attributes.put("target", "welcome");
        dialogue.wait = 2;
        new Event(dialogue, area).run();
        return area;
    }
}
