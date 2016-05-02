package thepaperpilot.rpg.Chapters.One;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import thepaperpilot.rpg.Battles.Attack;
import thepaperpilot.rpg.Battles.Enemy;
import thepaperpilot.rpg.Components.*;
import thepaperpilot.rpg.Events.*;
import thepaperpilot.rpg.Main;
import thepaperpilot.rpg.Screens.Area;
import thepaperpilot.rpg.Screens.Battle;
import thepaperpilot.rpg.Screens.Context;
import thepaperpilot.rpg.UI.Line;
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

    public void init(final Area area) {
        /* Entities */
        Entity satanEntity = new Entity();
        satanEntity.add(new NameComponent("satan"));
        satanEntity.add(new ActorComponent(new Image(Main.getTexture("satan"))));
        satanEntity.add(new PositionComponent(3 * Constants.TILE_SIZE, 6 * Constants.TILE_SIZE));
        satanEntity.add(new AreaComponent(area));

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

            public void start(final Battle battle) {
                DialogueComponent tutorial = DialogueComponent.read("lucifer");
                tutorial.start = "tutorial";
                tutorial.events.put("attack", new Runnable() {
                    @Override
                    public void run() {
                        battle.events.add(new NextAttack());
                    }
                });
                battle.events.add(new StartDialogue(tutorial));
            }

            public void update(final Battle battle) {
                boolean end = battle.enemies.get(0).health < 95;
                Line line = new Line(end ? "Wow, that was impressive. Let's go ahead and end the fight here." : bank[MathUtils.random(bank.length - 1)]);
                if (end) {
                    line.event = "end";
                    battle.attacking = false;
                }
                line.timer = end ? 0 : 4;
                line.fontScale = .5f;
                DialogueComponent dc = new DialogueComponent();
                dc.small = true;
                dc.position = new Rectangle(satanEnemy.position.x + 60, satanEnemy.position.y, 180, end ? 34 : 12);
                dc.events.put("end", new Runnable() {
                    @Override
                    public void run() {
                        battle.events.add(new EndBattle());
                    }
                });
                dc.start = "start";
                dc.lines.put("start", line);
                Entity entity = new Entity();
                entity.add(dc);
                battle.engine.addEntity(entity);
            }
        };
        satan.enemies = new Enemy.EnemyPrototype[]{satanEnemy};
        satan.bgm = "Searching.mp3";
        satan.playerPosition = new Vector2(320, 180);

        /* Adding things to Area */
        entities = new Entity[]{satanEntity};
        battles = new Battle.BattlePrototype[]{satan};
    }

    public Context getContext(Vector2 start, Vector2 end) {
        final Area area = new Area(this);
        area.init();
        Event ec = moveEvent(start, end, area);
        LockCamera lc = new LockCamera(4 * Constants.TILE_SIZE, 4 * Constants.TILE_SIZE, 1, true);
        DialogueComponent intro = DialogueComponent.read("lucifer");
        intro.start = "start";
        intro.events.put("satan", new Runnable() {
            @Override
            public void run() {
                area.events.add(new SetEntityVisibility("satan", true));
            }
        });
        intro.events.put("battle", new Runnable() {
            @Override
            public void run() {
                StartCombat cc = new StartCombat("satan");
                cc.delay = 1;
                DialogueComponent die = DialogueComponent.read("lucifer");
                die.start = "post";
                die.events.put("die", new Runnable() {
                    @Override
                    public void run() {
                        area.events.add(new ChangeContext("falling"));
                    }
                });
                cc.chain.add(new StartDialogue(die));
                cc.chain.add(new HealPlayer());
                area.events.add(cc);
            }
        });
        StartDialogue dc = new StartDialogue(intro);
        dc.delay = 2;
        ec.chain.add(lc);
        ec.chain.add(new StartCutscene());
        ec.chain.add(dc);
        return area;
    }
}
