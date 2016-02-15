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
        Event satanAppear = new Event(Event.Type.SET_ENTITY_VISIBILITY, "satan");
        satanAppear.attributes.put("visible", "true");

        /* Entities */
        Entity satanEntity = new Entity("satan", "satan", 3 * Main.TILE_SIZE, 6 * Main.TILE_SIZE, false, true);

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
                new Event(Event.Type.DIALOGUE, "tutorial").run(battle);
            }

            public void update(Battle battle) {
                battle.addDialogue(new Dialogue.SmallDialogue("fight", new Dialogue.Line[]{new Dialogue.Line(battle.enemies.get(0).health < 75 ? "Wow, that was impressive. Let's go ahead and end the fight here." : bank[MathUtils.random(bank.length - 1)])}, battle.enemies.get(0).health < 75 ? 0 : 4, new Vector2(satanEnemy.position.x + 120, satanEnemy.position.y + 10), battle.enemies.get(0).health < 75 ? new Vector2(180, 30) : new Vector2(180, 12), battle.enemies.get(0).health >= 75));
            }
        };
        satan.enemies = new Enemy.EnemyPrototype[]{satanEnemy};
        satan.winEvents = satan.loseEvents = new Event[]{new Event(Event.Type.DIALOGUE, "discussion"), new Event(Event.Type.HEAL_PLAYER)};
        satan.bgm = "Sad Descent";
        satan.playerPosition = new Vector2(320, 180);

        /* Dialogues */
        Dialogue.Line line1 = new Dialogue.Line("Alright! I have given you the power to rewrite the world around you! With limitations, of course. For now let's show you how to fight with this power. ");
        line1.name = "Satan";
        line1.face = "satan";
        Dialogue.Line line2 = new Dialogue.Line("First you select an attack- for now, you can knife, heal, and run. Then in the attack phase you'll need to type out words as they appear on the screen. Some are from your attack, and others are from the enemy. Type enemy words to stop their attack and type yours to complete them successfully.");
        line2.name = "Satan";
        line2.face = "satan";
        Dialogue.Line line3 = new Dialogue.Line("The first to 0 health loses! Don't worry, I'll go easy on you... for now.");
        line3.name = "Satan";
        line3.face = "satan";
        line3.events = new Event[]{new Event(Event.Type.NEXT_ATTACK)};
        final Dialogue tutorial = new Dialogue("tutorial", new Dialogue.Line[]{line1, line2, line3});

        line1 = new Dialogue.Line("Good fight! It seems you have already gotten the hang of using your new power. Now for my end of the deal.");
        line1.name = "Satan";
        line1.face = "satan";
        line2 = new Dialogue.Line("What?! I thought the whole point was you get my soul after I die? I'm still alive!");
        line2.name = "Player";
        line2.face = "player";
        line3 = new Dialogue.Line("It would seem someone didn't read the contract they were signing, now did they... Your soul is mine. You're in my world, now!");
        line3.name = "Satan";
        line3.face = "satan";
        line3.events = new Event[]{new Event(Event.Type.CHANGE_CONTEXT, "falling")};
        final Dialogue discussion = new Dialogue("discussion", new Dialogue.Line[]{line1, line2, line3});

        line1 = new Dialogue.Line("God, writing is hard! This reads like some shitty fan fic. I'll never be good enough to publish.");
        line1.name = "Player";
        line1.face = "player";
        line1.events = new Event[]{satanAppear};
        line2 = new Dialogue.Line("How much would you like to be able to write? hmm?");
        line2.name = "Satan";
        line2.face = "satan";
        line3 = new Dialogue.Line("Oh wow, that was unexpected! Let me guess, I can sign off my soul in exchange for, like, super awesome writing powers?");
        line3.name = "Player";
        line3.face = "player";
        Dialogue.Line line4 = new Dialogue.Line("Ha, not just super awesome, but better! I can give you the power to write the world the way you see it! Write your story in real life!");
        line4.name = "Satan";
        line4.face = "satan";
        Dialogue.Line line5 = new Dialogue.Line("Huh, that is better than super awesome. But is it eternity in hell better? I'm not convinced.");
        line5.name = "Player";
        line5.face = "player";
        Dialogue.Line line6 = new Dialogue.Line("Well you should be, because I'm literally Satan.");
        line6.name = "Satan";
        line6.face = "satan";
        Dialogue.Line line7 = new Dialogue.Line("Can't argue with that logic. Where do I sign?");
        line7.name = "Player";
        line7.face = "player";
        line7.events = new Event[]{new Event(Event.Type.COMBAT, "satan", 1)};
        final Dialogue welcome = new Dialogue("welcome", new Dialogue.Line[]{line1, line2, line3, line4, line5, line6, line7});

        /* Adding things to area */
        entities = new Entity[]{satanEntity};
        dialogues = new Dialogue[]{tutorial, discussion, welcome};
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
        Event stopCamera = new Event(Event.Type.MOVE_CAMERA);
        stopCamera.attributes.put("x", "" + 4 * Main.TILE_SIZE);
        stopCamera.attributes.put("y", "" + 4 * Main.TILE_SIZE);
        stopCamera.attributes.put("zoom", "" + 1);
        stopCamera.attributes.put("instant", "true");
        stopCamera.run(area);
        new Event(Event.Type.CUTSCENE).run(area);
        new Event(Event.Type.DIALOGUE, "welcome", 2).run(area);
        return area;
    }
}
