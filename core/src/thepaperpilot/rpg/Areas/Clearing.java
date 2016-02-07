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

public class Clearing extends Area.AreaPrototype {
    public Clearing() {
        /* Events */
        final Event.EventPrototype talk = new Event.EventPrototype();
        talk.type = "DIALOGUE";
        talk.attributes.put("target", "talker");

        final Event.EventPrototype move = new Event.EventPrototype();
        move.type = "MOVE_ENTITY";
        move.attributes.put("target", "talker");
        move.attributes.put("x", "" + 20 * Main.TILE_SIZE);
        move.attributes.put("y", "" + 22 * Main.TILE_SIZE);
        move.wait = 4;

        final Event.EventPrototype allPapers = new Event.EventPrototype();
        allPapers.type = "DIALOGUE";
        allPapers.attributes.put("target", "allPapers");

        final Event.EventPrototype lastPaper = new Event.EventPrototype();
        lastPaper.type = "DIALOGUE";
        lastPaper.attributes.put("target", "lastPaper");

        final Event.EventPrototype removePaper = new Event.EventPrototype();
        removePaper.type = "SET_ENTITY_VISIBILITY";
        removePaper.attributes.put("target", "pile");
        removePaper.attributes.put("visible", "false");

        final Event.EventPrototype bossBattle = new Event.EventPrototype();
        bossBattle.type = "COMBAT";
        bossBattle.attributes.put("target", "boss");

        final Event.EventPrototype win = new Event.EventPrototype();
        win.type = "DIALOGUE";
        win.attributes.put("target", "win");

        final Event.EventPrototype removeJoker = new Event.EventPrototype();
        removeJoker.type = "SET_ENTITY_VISIBILITY";
        removeJoker.attributes.put("target", "boss");
        removeJoker.attributes.put("visible", "false");

        Event.EventPrototype release = new Event.EventPrototype();
        release.type = "RELEASE_CAMERA";
        release.wait = 2;

        /* Entities */
        Entity.EntityPrototype talkerEntity = new Entity.EntityPrototype("talker", "talker", 6 * Main.TILE_SIZE, 3 * Main.TILE_SIZE, true) {
            public void onTouch(Entity entity) {
                new Event(talk, entity.area).run();
                new Event(move, entity.area).run();
                Event.EventPrototype look = new Event.EventPrototype();
                look.type = "MOVE_CAMERA";
                look.attributes.put("x", "" + entity.getX());
                look.attributes.put("y", "" + entity.getY());
                look.attributes.put("zoom", ".75f");
                new Event(look, entity.area).run();
            }
        };

        Entity.EntityPrototype pile = new Entity.EntityPrototype("pile", "pile", 24 * Main.TILE_SIZE, 12 * Main.TILE_SIZE, true) {
            int stones = 132;

            public void onTouch(Entity entity) {
                if (stones == 132) {
                    new Event(allPapers, entity.area).run();
                } else if (stones == 1) {
                    new Event(lastPaper, entity.area).run();
                    new Event(removePaper, entity.area).run();
                } else {
                    Dialogue.DialoguePrototype dialoguePrototype = new Dialogue.DialoguePrototype();
                    Dialogue.LinePrototype line = new Dialogue.LinePrototype();
                    line.message = "There are still " + stones + " stones in the pile. Determined, you put another in your pocket.";
                    dialoguePrototype.lines = new Dialogue.LinePrototype[]{line};
                    Dialogue dialogue = new Dialogue(dialoguePrototype, entity.area);
                    entity.area.stage.addActor(dialogue);
                    entity.area.stage.setKeyboardFocus(dialogue);
                }
                stones--;
            }
        };

        Entity.EntityPrototype battle = new Entity.EntityPrototype("boss", "joker", 16 * Main.TILE_SIZE, 30 * Main.TILE_SIZE, true) {
            public void onTouch(Entity entity) {
                new Event(bossBattle, entity.area).run();
            }
        };

        /* Dialogues */
        Dialogue.DialoguePrototype talkerDialogue = new Dialogue.DialoguePrototype();
        talkerDialogue.name = "talker";
        Dialogue.LinePrototype line1 = new Dialogue.LinePrototype();
        line1.message = "wow ur a nerd. I only talk to cool kids. kthxbye";
        line1.name = "ur mum lol";
        Dialogue.LinePrototype line2 = new Dialogue.LinePrototype();
        line2.name = "wew lad";
        line2.message = "I'm, like, literally running away from you. #lol #creep";
        line2.events = new Event.EventPrototype[]{release};
        talkerDialogue.lines = new Dialogue.LinePrototype[]{line1, line2};

        Dialogue.DialoguePrototype welcomeDial = new Dialogue.DialoguePrototype();
        welcomeDial.name = "welcome";
        Dialogue.LinePrototype welcomeLine = new Dialogue.LinePrototype();
        welcomeLine.name = "narrator";
        welcomeLine.face = "narrator";
        welcomeLine.message = "Hi Drew! Please continue not judging too harshly. From now on there will be completely different scenes in each thing I show you. So make sure you've found everything in this one! Press e or enter to interact with things!";
        welcomeDial.lines = new Dialogue.LinePrototype[]{welcomeLine};

        Dialogue.DialoguePrototype allPapersDial = new Dialogue.DialoguePrototype();
        allPapersDial.name = "allPapers";
        line1 = new Dialogue.LinePrototype();
        line1.message = "You see a pile of precisely 132 stones. You pick one up and put it in your pocket.";
        allPapersDial.lines = new Dialogue.LinePrototype[]{line1};

        Dialogue.DialoguePrototype lastPaperDial = new Dialogue.DialoguePrototype();
        lastPaperDial.name = "lastPaper";
        line1 = new Dialogue.LinePrototype();
        line1.message = "There's only one stone left. With a smug face you pick up the last one and put it in your now bulging pockets, congratulating yourself on a job well done.";
        lastPaperDial.lines = new Dialogue.LinePrototype[]{line1};

        Dialogue.DialoguePrototype winDial = new Dialogue.DialoguePrototype();
        winDial.name = "win";
        line1 = new Dialogue.LinePrototype();
        line1.face = "joker";
        line1.name = "joker";
        line1.message = "Congratulations! You really showed me, huhhuh! I hope you had fun beating me up, hyukhyuk!";
        winDial.lines = new Dialogue.LinePrototype[]{line1};

        /* Attacks */
        Attack.AttackPrototype attack = new Attack.AttackPrototype(new String[]{"die", "attack", "knife", "shiv", "murder", "kill", "merciless", "genocide"}, "jingles_SAX16", "knife", Attack.Target.ENEMY, 2, Color.RED, 6, true) {
            int attacks = 3;
            float time = 2;
            public void reset() {
                attacks = 3;
                time = 2;
            }

            @Override
            public Attack.Word[] update(float delta, Attack attack) {
                time += delta;
                if (time > 2 && attacks > 0 && attack.battle.enemies.size() > 0) {
                    attacks--;
                    time -= 2;
                    Attack.Word word = getWord(attack);
                    word.start = new Vector2(attack.battle.playerPos.x + MathUtils.random(50) - 25, attack.battle.playerPos.y - MathUtils.random(50));
                    word.end = word.start.cpy().add(0, 20);
                    if (attacks == 0) attack.done = true;
                    return new Attack.Word[]{word};
                }
                return new Attack.Word[]{};
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
            public Attack.Word[] update(float delta, Attack attack) {
                time += delta;
                if (time > 2 && attacks > 0) {
                    attacks--;
                    time -= 2;
                    Attack.Word word = getWord(attack);
                    word.start = new Vector2(attack.battle.playerPos.x + MathUtils.random(50) - 25, attack.battle.playerPos.y - MathUtils.random(50));
                    word.end = word.start.cpy().add(0, 10);
                    if (attacks == 0) attack.done = true;
                    return new Attack.Word[]{word};
                }
                return new Attack.Word[]{};
            }
        };

        Attack.AttackPrototype run = new Attack.AttackPrototype(new String[]{"help!", "escape...", "run...", "away...", "run away..", "get away.."}, "jingles_SAX03", "run", Attack.Target.OTHER, 0, Color.TEAL, 20, true) {
            @Override
            public Attack.Word[] update(float delta, Attack attack) {
                if (!attack.done) {
                    Attack.Word word = getWord(attack);
                    word.start = new Vector2(attack.battle.playerPos.x + MathUtils.random(50) - 25, attack.battle.playerPos.y - MathUtils.random(50));
                    word.end = word.start.cpy().add(0, 10);
                    attack.done = true;
                    return new Attack.Word[]{word};
                }
                return new Attack.Word[]{};
            }

            public void run(Attack.Word word) {
                word.attack.battle.escape();
                super.run(word);
            }
        };

        Attack.AttackPrototype ball = new Attack.AttackPrototype(new String[]{"fun", "ball", "catch", "juggle", "joy", "happy", "play"}, "jingles_SAX16", "ball", Attack.Target.PLAYER, 1, Color.RED, 10, false) {
            int attacks = 3;
            float time = 2;
            public void reset() {
                attacks = 3;
                time = 2;
            }

            @Override
            public Attack.Word[] update(float delta, Attack attack) {
                time += delta;
                if (time > 2 && attacks > 0 && attack.battle.enemies.size() > 0) {
                    attacks--;
                    time -= 2;
                    Attack.Word word = getWord(attack);
                    word.start = new Vector2(attack.battle.enemies.get(0).getX() + MathUtils.random(50) - 25, attack.battle.enemies.get(0).getY() + MathUtils.random(50) - 25);
                    word.end = new Vector2(attack.battle.playerPos.x, attack.battle.playerPos.y);
                    if (attacks == 0) attack.done = true;
                    return new Attack.Word[]{word};
                }
                return new Attack.Word[]{};
            }
        };

        /* Enemies */
        Enemy.EnemyPrototype bossEnemy = new Enemy.EnemyPrototype("joker", "joker", 40, 200, 4);
        bossEnemy.attacks = new Attack.AttackPrototype[]{ball};

        /* Battles */
        Battle.BattlePrototype boss = new Battle.BattlePrototype("boss", true);
        boss.enemies = new Enemy.EnemyPrototype[]{bossEnemy};
        boss.winEvents = new Event.EventPrototype[]{win, removeJoker};
        boss.bgm = "Sad Descent";

        /* Adding things to area */
        entities = new Entity.EntityPrototype[]{talkerEntity, pile, battle};
        dialogues = new Dialogue.DialoguePrototype[]{talkerDialogue, welcomeDial, allPapersDial, lastPaperDial, winDial};
        battles = new Battle.BattlePrototype[]{boss};
        attacks = new Attack.AttackPrototype[]{attack, heal, run};
        bgm = "Wacky Waiting";
    }

    public void loadAssets(AssetManager manager) {
        manager.load("talker.png", Texture.class);
        manager.load("joker.png", Texture.class);
        manager.load("narrator.png", Texture.class);
        manager.load("pile.png", Texture.class);
        manager.load("Wacky Waiting.ogg", Sound.class);
        manager.load("Sad Descent.ogg", Sound.class);
    }

    public Context getContext() {
        Area area = new Area(this);
        Event.EventPrototype welcome = new Event.EventPrototype();
        welcome.type = "DIALOGUE";
        welcome.attributes.put("target", "welcome");
        new Event(welcome, area).run();
        return area;
    }
}
