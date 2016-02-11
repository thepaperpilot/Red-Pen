package thepaperpilot.rpg.Areas;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import thepaperpilot.rpg.Battles.Battle;
import thepaperpilot.rpg.Battles.Enemy;
import thepaperpilot.rpg.*;
import thepaperpilot.rpg.Map.Area;
import thepaperpilot.rpg.Map.Entity;

public class Throne extends Area {

    public Throne(ThronePrototype prototype) {
        super(prototype);
        if (Player.getPortal()) {
            Event.EventPrototype event = new Event.EventPrototype(Event.Type.SET_ENTITY_VISIBILITY, "boss");
            event.attributes.put("visible", "false");
            new Event(event, this).run();
        }
    }

    public void render(float delta) {
        super.render(delta);

        if (player.getY() < 0) {
            new Event(new Event.EventPrototype(Event.Type.SHUTDOWN), this).run();
        }
    }

    public static class ThronePrototype extends AreaPrototype {
        public ThronePrototype() {
            super("throne");

            /* Events */
            final Event.EventPrototype move = new Event.EventPrototype(Event.Type.MOVE_ENTITY, "talker");
            move.attributes.put("x", "" + 20 * Main.TILE_SIZE);
            move.attributes.put("y", "" + 22 * Main.TILE_SIZE);
            move.wait = 4;

            final Event.EventPrototype removePaper = new Event.EventPrototype(Event.Type.SET_ENTITY_VISIBILITY, "pile");
            removePaper.attributes.put("visible", "false");

            final Event.EventPrototype removeJoker = new Event.EventPrototype(Event.Type.SET_ENTITY_VISIBILITY, "boss");
            removeJoker.attributes.put("visible", "false");

            /* Entities */
            Entity.EntityPrototype talkerEntity = new Entity.EntityPrototype("talker", "talker", 6 * Main.TILE_SIZE, 3 * Main.TILE_SIZE, true) {
                public void onTouch(Entity entity) {
                    new Event(new Event.EventPrototype(Event.Type.DIALOGUE, "talker"), entity.area).run();
                    new Event(move, entity.area).run();
                    Event.EventPrototype look = new Event.EventPrototype(Event.Type.MOVE_CAMERA);
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
                        new Event(new Event.EventPrototype(Event.Type.DIALOGUE, "allPapers"), entity.area).run();
                    } else if (stones == 1) {
                        new Event(new Event.EventPrototype(Event.Type.DIALOGUE, "lastPaper"), entity.area).run();
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
                    Event.EventPrototype event = new Event.EventPrototype(Event.Type.DIALOGUE);
                    event.attributes.put("target", "joker");
                    new Event(event, entity.area).run();
                }
            };

            Entity.EntityPrototype portal = new Entity.EntityPrototype("portal", "portal", 15 * Main.TILE_SIZE, 30 * Main.TILE_SIZE, true) {
                public void onTouch(Entity entity) {
                    if (Player.getPortal()) {
                        Event.EventPrototype event = new Event.EventPrototype(Event.Type.DIALOGUE);
                        event.attributes.put("target", "activate");
                        new Event(event, entity.area).run();
                    } else {
                        Event.EventPrototype event = new Event.EventPrototype(Event.Type.DIALOGUE);
                        event.attributes.put("target", "portal");
                        new Event(event, entity.area).run();
                    }
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
            line2.events = new Event.EventPrototype[]{new Event.EventPrototype(Event.Type.RELEASE_CAMERA)};
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
            line1.message = "Wow, now I realize why I was the first boss! Like, I'm honestly ashamed of myself. Well, you've taken my powers, I hope they serve you well. Go on, activate the portal. You use abilities much like you use actions in battle. Be careful though, these abilities are much more difficult than regular actions!";
            line2 = new Dialogue.LinePrototype();
            line2.face = "joker";
            line2.name = "joker";
            line2.message = "This portal will bring you to the overworld for a short time. You can use it talk to someone back home, if you'd like. Use it carefully, though, as it can't be used very often. Good luck...";
            line2.events = new Event.EventPrototype[]{removeJoker, new Event.EventPrototype(Event.Type.ADD_PORTAL)};
            winDial.lines = new Dialogue.LinePrototype[]{line1, line2};

            Dialogue.DialoguePrototype portalDialogue = new Dialogue.DialoguePrototype();
            portalDialogue.name = "portal";
            line1 = new Dialogue.LinePrototype();
            line1.face = "joker";
            line1.name = "joker";
            line1.message = "woah woah woah. What are you trying to do with my portal? You don't have the ability to use it!";
            portalDialogue.lines = new Dialogue.LinePrototype[]{line1};

            Dialogue.DialoguePrototype joker = new Dialogue.DialoguePrototype();
            joker.name = "joker";
            line1 = new Dialogue.LinePrototype();
            line1.face = "joker";
            line1.name = "joker";
            line1.message = "Oh? You're trying to get out of Hell, are you? Well if you hope to do that, you'll need my portal abilities. Unfortunately for you, I won't give them up without a fight.";
            line2 = new Dialogue.LinePrototype();
            line2.face = "player";
            line2.name = "Player";
            line2.message = "Well, yeah. You're the boss, that was to be expected. But admittedly, I was expecting someone more... boss-like?";
            Dialogue.LinePrototype line3 = new Dialogue.LinePrototype();
            line3.face = "joker";
            line3.name = "joker";
            line3.message = "Trust me, I'm plenty 'boss-like'. Just you wait and see!";
            line3.events = new Event.EventPrototype[]{new Event.EventPrototype(Event.Type.COMBAT, "boss")};
            joker.lines = new Dialogue.LinePrototype[]{line1, line2, line3};

            Dialogue.DialoguePrototype activate = new Dialogue.DialoguePrototype();
            activate.name = "activate";
            activate.type = Dialogue.DialougeType.SMALL;
            activate.position = new Vector2(320, 120);
            activate.size = new Vector2(360, 100);
            line1 = new Dialogue.LinePrototype();
            line1.message = "You look at the portal. You can vaguely make out what appears to be your university. Do you wish to enter?";
            Dialogue.OptionPrototype yes = new Dialogue.OptionPrototype();
            yes.message = "yes";
            // TODO portal event
            yes.events = new Event.EventPrototype[]{};
            Dialogue.OptionPrototype no = new Dialogue.OptionPrototype();
            no.message = "no";
            no.events = new Event.EventPrototype[]{};
            line1.options = new Dialogue.OptionPrototype[]{yes, no};
            activate.lines = new Dialogue.LinePrototype[]{line1};

            Dialogue.DialoguePrototype loseDial = new Dialogue.DialoguePrototype();
            loseDial.name = "lose";
            line1 = new Dialogue.LinePrototype();
            line1.face = "joker";
            line1.name = "joker";
            line1.message = "Haha! Told you I wouldn't be so easy! Come try again when you aren't such a joke! Ha";
            loseDial.lines = new Dialogue.LinePrototype[]{line1};

            final Dialogue.DialoguePrototype tutorial = new Dialogue.DialoguePrototype();
            tutorial.name = "tutorial";
            line1 = new Dialogue.LinePrototype();
            line1.name = "joker";
            line1.face = "joker";
            line1.message = "I'm gonna give you a head's up before starting this battle. I'm going to be creating portals, which are additional enemies. When dealing with multiple enemies, you can click on the one you want to attack to focus on it.";
            line2 = new Dialogue.LinePrototype();
            line2.name = "joker";
            line2.face = "joker";
            line2.message = "I won't be attacking directly, but the battle won't end until I'm defeated. Not that a runt like you could actually do such a thing. Well good luck anyways, you'll need it.";
            line2.events = new Event.EventPrototype[]{new Event.EventPrototype(Event.Type.NEXT_ATTACK)};
            tutorial.lines = new Dialogue.LinePrototype[]{line1, line2};

            /* Battles */
            Battle.BattlePrototype boss = new Battle.BattlePrototype("boss", true) {
                public void start(Battle battle) {
                    new Event(new Event.EventPrototype(Event.Type.DIALOGUE, "tutorial"), battle).run();
                }
            };
            boss.enemies = new Enemy.EnemyPrototype[]{Enemy.prototypes.get("joker")};
            boss.winEvents = new Event.EventPrototype[]{new Event.EventPrototype(Event.Type.DIALOGUE, "win")};
            boss.loseEvents = new Event.EventPrototype[]{new Event.EventPrototype(Event.Type.DIALOGUE, "lose")};
            boss.bgm = "Sad Descent";

            /* Adding things to area */
            entities = new Entity.EntityPrototype[]{talkerEntity, pile, battle, portal};
            dialogues = new Dialogue.DialoguePrototype[]{talkerDialogue, allPapersDial, lastPaperDial, winDial, loseDial, portalDialogue, joker, activate, tutorial};
            battles = new Battle.BattlePrototype[]{boss};
            bgm = "Wacky Waiting";
            tint = new Color(1, .8f, .8f, 1);
        }

        public void loadAssets(AssetManager manager) {
            manager.load("talker.png", Texture.class);
            manager.load("joker.png", Texture.class);
            manager.load("narrator.png", Texture.class);
            manager.load("pile.png", Texture.class);
            manager.load("portal.png", Texture.class);
            manager.load("Wacky Waiting.ogg", Sound.class);
            manager.load("Sad Descent.ogg", Sound.class);
        }

        public Context getContext() {
            return new Throne(this);
        }
    }
}
