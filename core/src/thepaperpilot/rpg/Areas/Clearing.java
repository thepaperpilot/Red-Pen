package thepaperpilot.rpg.Areas;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
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

public class Clearing extends Area {

    public Clearing(ClearingPrototype prototype) {
        super(prototype);
    }

    public void render(float delta) {
        super.render(delta);

        if (player.getY() < 0) {
            Event.EventPrototype changeArea = new Event.EventPrototype();
            changeArea.type = "SHUTDOWN";
            new Event(changeArea, this).run();
        }
    }

    public static class ClearingPrototype extends AreaPrototype {
        public ClearingPrototype() {
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
                        Dialogue dialogue = dialoguePrototype.getDialogue(entity.area);
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

            /* Enemies */
            Enemy.EnemyPrototype bossEnemy = new Enemy.EnemyPrototype("joker", "joker", new Vector2(40, 200), 4) {
                @Override
                public Attack.AttackPrototype getAttack(Enemy enemy) {
                    return Attack.prototypes.get("ball");
                }
            };

            /* Battles */
            Battle.BattlePrototype boss = new Battle.BattlePrototype("boss", true);
            boss.enemies = new Enemy.EnemyPrototype[]{bossEnemy};
            boss.winEvents = new Event.EventPrototype[]{win, removeJoker};
            boss.bgm = "Sad Descent";

            /* Adding things to area */
            entities = new Entity.EntityPrototype[]{talkerEntity, pile, battle};
            dialogues = new Dialogue.DialoguePrototype[]{talkerDialogue, allPapersDial, lastPaperDial, winDial};
            battles = new Battle.BattlePrototype[]{boss};
            bgm = "Wacky Waiting";
            tint = new Color(1, .8f, .8f, 1);
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
            return new Clearing(this);
        }
    }
}
