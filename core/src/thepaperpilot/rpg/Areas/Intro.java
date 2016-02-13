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
import thepaperpilot.rpg.Event;
import thepaperpilot.rpg.Main;
import thepaperpilot.rpg.Map.Area;
import thepaperpilot.rpg.Map.Entity;
import thepaperpilot.rpg.UI.Dialogue;

public class Intro extends Area.AreaPrototype {
    public Intro() {
        super("intro");

        /* Events */
        Event.EventPrototype satanAppear = new Event.EventPrototype(Event.Type.SET_ENTITY_VISIBILITY, "satan");
        satanAppear.attributes.put("visible", "true");

        Event.EventPrototype fight = new Event.EventPrototype(Event.Type.COMBAT, "satan");
        fight.wait = 1;

        /* Entities */
        Entity.EntityPrototype satanEntity = new Entity.EntityPrototype("satan", "satan", 3 * Main.TILE_SIZE, 6 * Main.TILE_SIZE, false);

        /* Enemies */
        final Enemy.EnemyPrototype satanEnemy = new Enemy.EnemyPrototype("satan", "satan", new Vector2(320, 320), 100, new Attack.AttackPrototype(
                new String[]{"hell", "satan", "death", "die", "sin", "death", "immoral", "evil", "despicable", "mean", "horrible", "rude", "afterlife", "dead", "never"},
                "jingles_SAX16", "satan", Attack.Target.PLAYER, 1, Color.RED, 8, 6, 2, false) {
            @Override
            public void run(Vector2 position, Attack attack) {
                for (int i = 0; i < 2; i++) {
                    for (int j = 0; j < 2; j++) {
                        Attack.Word word = getWord(attack);
                        word.start = new Vector2(attack.battle.playerPos.x - 80 + 160 * i, attack.battle.playerPos.y - 80 + 160 * j);
                        word.end = attack.battle.playerPos.cpy();
                        attack.addWord(word);
                    }
                }
            }
        });

        /* Battles */
        Battle.BattlePrototype satan = new Battle.BattlePrototype("satan", false) {
            String[] bank = new String[]{"Prepare to die!", "You can't defeat me", "Hit me as hard as you can!", "Don't test me", "Try harder", "You can't win"};

            public void start(Battle battle) {
                new Event(new Event.EventPrototype(Event.Type.DIALOGUE, "tutorial"), battle).run();
            }

            public void update(Battle battle) {
                Dialogue.DialoguePrototype fightDialogue = new Dialogue.DialoguePrototype();
                fightDialogue.name = "fight";
                fightDialogue.type = Dialogue.DialougeType.SMALL;
                fightDialogue.position = new Vector2(satanEnemy.position.x + 120, satanEnemy.position.y + 10);
                fightDialogue.smallFont = true;
                Dialogue.LinePrototype line1 = new Dialogue.LinePrototype();

                if (battle.enemies.get(0).health < 75) {
                    fightDialogue.size = new Vector2(180, 30);
                    line1.message = "Wow, that was impressive. Let's go ahead and end the fight here.";
                    line1.events = new Event.EventPrototype[]{new Event.EventPrototype(Event.Type.END_BATTLE)};
                    battle.attacking = false;
                } else {
                    fightDialogue.timer = 4;
                    fightDialogue.size = new Vector2(180, 12);
                    line1.message = bank[MathUtils.random(bank.length - 1)];
                }

                fightDialogue.lines = new Dialogue.LinePrototype[]{line1};
                battle.addDialogue(fightDialogue);
            }
        };
        satan.enemies = new Enemy.EnemyPrototype[]{satanEnemy};
        satan.winEvents = satan.loseEvents = new Event.EventPrototype[]{new Event.EventPrototype(Event.Type.DIALOGUE, "discussion"), new Event.EventPrototype(Event.Type.HEAL_PLAYER)};
        satan.bgm = "Sad Descent";
        satan.playerPosition = new Vector2(320, 180);

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
        line3.events = new Event.EventPrototype[]{new Event.EventPrototype(Event.Type.NEXT_ATTACK)};
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
        line3.events = new Event.EventPrototype[]{new Event.EventPrototype(Event.Type.CHANGE_CONTEXT, "falling")};
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

        /* Adding things to area */
        entities = new Entity.EntityPrototype[]{satanEntity};
        dialogues = new Dialogue.DialoguePrototype[]{tutorial, discussion, welcome};
        battles = new Battle.BattlePrototype[]{satan};
        bgm = "Wacky Waiting";
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
        Event.EventPrototype stopCamera = new Event.EventPrototype(Event.Type.MOVE_CAMERA);
        stopCamera.attributes.put("x", "" + 4 * Main.TILE_SIZE);
        stopCamera.attributes.put("y", "" + 4 * Main.TILE_SIZE);
        stopCamera.attributes.put("zoom", "" + 1);
        stopCamera.attributes.put("instant", "true");
        new Event(stopCamera, area).run();
        new Event(new Event.EventPrototype(Event.Type.CUTSCENE), area).run();
        Event.EventPrototype dialogue = new Event.EventPrototype(Event.Type.DIALOGUE, "welcome");
        dialogue.wait = 2;
        new Event(dialogue, area).run();
        return area;
    }
}
