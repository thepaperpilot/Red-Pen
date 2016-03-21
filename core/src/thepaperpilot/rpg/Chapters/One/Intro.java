package thepaperpilot.rpg.Chapters.One;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import thepaperpilot.rpg.Area;
import thepaperpilot.rpg.Battles.Attack;
import thepaperpilot.rpg.Battles.Battle;
import thepaperpilot.rpg.Battles.Enemy;
import thepaperpilot.rpg.Components.ActorComponent;
import thepaperpilot.rpg.Components.NameComponent;
import thepaperpilot.rpg.Components.PositionComponent;
import thepaperpilot.rpg.Context;
import thepaperpilot.rpg.Events.*;
import thepaperpilot.rpg.Main;
import thepaperpilot.rpg.Util.Constants;

public class Intro extends Area.AreaPrototype {
    public Intro() {
        super("intro");

        /* Adding things to area */
        bgm = "Come and Find Me.mp3";
        viewport = new Vector2(8 * Constants.TILE_SIZE, 8 * Constants.TILE_SIZE);
        playerStart = playerEnd = new Vector2(6 * Constants.TILE_SIZE, 4 * Constants.TILE_SIZE);
        mapSize = new Vector2(8, 8);
        tint = new Color(1, .8f, 1, 1);
    }

    public void init(Area area) {
        /* Entities */
        Entity satanEntity = new Entity();
        satanEntity.add(new NameComponent("satan"));
        satanEntity.add(new ActorComponent(area, new Image(Main.getTexture("satan"))));
        satanEntity.add(new PositionComponent(3 * Constants.TILE_SIZE, 6 * Constants.TILE_SIZE));

        /* Enemies */
        final Enemy.EnemyPrototype satanEnemy = new Enemy.EnemyPrototype("satan", "satan", "Satan", new String[]{"ha! you won't fight? Well neither shall I, it's not my problem if you're going to waste your power"}, new Vector2(320, 320), 100, new Attack.AttackPrototype(
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
        Battle.BattlePrototype satan = new Battle.BattlePrototype("satan") {
            final String[] bank = new String[]{"Prepare to die!", "You can't defeat me", "Hit me as hard as you can!", "Don't test me", "Try harder", "You can't win"};

            public void start(Battle battle) {
                battle.events.add(new StartDialogue("tutorial"));
            }

            public void update(final Battle battle) {
                boolean end = battle.enemies.get(0).health < 95;
                thepaperpilot.rpg.UI.Dialogue.Line line = new thepaperpilot.rpg.UI.Dialogue.Line(end ? "Wow, that was impressive. Let's go ahead and end the fight here." : bank[MathUtils.random(bank.length - 1)]);
                if (end) {
                    line.events.add(new EndBattle());
                    battle.attacking = false;
                }
                new thepaperpilot.rpg.UI.Dialogue.SmallDialogue("fight", new thepaperpilot.rpg.UI.Dialogue.Line[]{line}, end ? 0 : 4, new Vector2(satanEnemy.position.x + 120, satanEnemy.position.y + 10), end ? new Vector2(180, 30) : new Vector2(180, 12), true).open(battle);
            }
        };
        satan.enemies = new Enemy.EnemyPrototype[]{satanEnemy};
        satan.bgm = "Searching.mp3";
        satan.playerPosition = new Vector2(320, 180);

        /* Dialogues */
        thepaperpilot.rpg.UI.Dialogue.Line line1 = new thepaperpilot.rpg.UI.Dialogue.Line("Alright! I have given you the power to rewrite the world around you! With limitations, of course. For now let's show you how to fight with this power.", "Lucifer", "satan");
        thepaperpilot.rpg.UI.Dialogue.Line line2 = new thepaperpilot.rpg.UI.Dialogue.Line("First you select an attack- for now, you can attack with your pencil, heal, or run. Then in the attack phase you'll need to type out words as they appear on the screen. Some are from your attack, and others are from the enemy. Type enemy words to stop their attack and type yours to complete them successfully.", "Lucifer", "satan");
        thepaperpilot.rpg.UI.Dialogue.Line line3 = new thepaperpilot.rpg.UI.Dialogue.Line("The first to 0 health loses! Don't worry, I'll go easy on you... for now.", "Lucifer", "satan");
        line3.events.add(new NextAttack());
        final thepaperpilot.rpg.UI.Dialogue tutorial = new thepaperpilot.rpg.UI.Dialogue("tutorial", new thepaperpilot.rpg.UI.Dialogue.Line[]{line1, line2, line3});

        line1 = new thepaperpilot.rpg.UI.Dialogue.Line("Good fight! It seems you have already gotten the hang of using your new power. Now for my end of the deal.", "Lucifer", "satan");
        line2 = new thepaperpilot.rpg.UI.Dialogue.Line("What?! I thought the whole point was you get my soul after I die? I'm still alive!", "Player", "player");
        line3 = new thepaperpilot.rpg.UI.Dialogue.Line("It would seem someone didn't read the contract they were signing, now did they... Your soul is mine. You're in my world, now!", "Lucifer", "satan");
        line3.events.add(new ChangeContext("falling"));
        final thepaperpilot.rpg.UI.Dialogue discussion = new thepaperpilot.rpg.UI.Dialogue("discussion", new thepaperpilot.rpg.UI.Dialogue.Line[]{line1, line2, line3});

        line1 = new thepaperpilot.rpg.UI.Dialogue.Line("God, writing is hard! This reads like some shitty fan fic. I'll never be good enough to publish.", "Player", "player");
        line1.events.add(new SetEntityVisibility("satan", true));
        line2 = new thepaperpilot.rpg.UI.Dialogue.Line("How much would you like to be able to write? hmm?", "Lucifer", "satan");
        line3 = new thepaperpilot.rpg.UI.Dialogue.Line("Oh wow, that was unexpected! Let me guess, I can sign off my soul in exchange for, like, super awesome writing powers?", "Player", "player");
        thepaperpilot.rpg.UI.Dialogue.Line line4 = new thepaperpilot.rpg.UI.Dialogue.Line("Ha, not just super awesome, but better! I can give you the power to write the world the way you see it! Write your story in real life!", "Lucifer", "satan");
        thepaperpilot.rpg.UI.Dialogue.Line line5 = new thepaperpilot.rpg.UI.Dialogue.Line("Huh, that is better than super awesome. But is it eternity in hell better? I'm not convinced.", "Player", "player");
        thepaperpilot.rpg.UI.Dialogue.Line line6 = new thepaperpilot.rpg.UI.Dialogue.Line("Well you should be, because I'm literally the Devil.", "Lucifer", "satan");
        thepaperpilot.rpg.UI.Dialogue.Line line7 = new thepaperpilot.rpg.UI.Dialogue.Line("Can't argue with that logic. Where do I sign?", "Player", "player");
        StartCombat cc = new StartCombat("satan");
        cc.delay = 1;
        cc.chain.add(new StartDialogue("discussion"));
        cc.chain.add(new HealPlayer());
        line7.events.add(cc);
        final thepaperpilot.rpg.UI.Dialogue welcome = new thepaperpilot.rpg.UI.Dialogue("welcome", new thepaperpilot.rpg.UI.Dialogue.Line[]{line1, line2, line3, line4, line5, line6, line7});

        /* Adding things to Area */
        entities = new Entity[]{satanEntity};
        dialogues = new thepaperpilot.rpg.UI.Dialogue[]{tutorial, discussion, welcome};
        battles = new Battle.BattlePrototype[]{satan};
    }

    public Context getContext(Vector2 start, Vector2 end) {
        Area area = new Area(this);
        area.init();
        Event ec = moveEvent(start, end, area);
        LockCamera lc = new LockCamera(4 * Constants.TILE_SIZE, 4 * Constants.TILE_SIZE, 1, true);
        StartDialogue dc = new StartDialogue("welcome");
        dc.delay = 2;
        ec.chain.add(lc);
        ec.chain.add(new StartCutscene());
        ec.chain.add(dc);
        return area;
    }
}
