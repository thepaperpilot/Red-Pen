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
import thepaperpilot.rpg.Components.Triggers.CollisionComponent;
import thepaperpilot.rpg.Components.Triggers.EnterZoneComponent;
import thepaperpilot.rpg.Events.*;
import thepaperpilot.rpg.Events.Shutdown;
import thepaperpilot.rpg.Main;
import thepaperpilot.rpg.Screens.Area;
import thepaperpilot.rpg.Screens.Battle;
import thepaperpilot.rpg.Screens.Context;
import thepaperpilot.rpg.Util.Constants;
import thepaperpilot.rpg.Util.Player;

public class Throne extends Area.AreaPrototype {
    public Throne() {
        super("throne");

        /* Adding things to Area */
        bgm = "Come and Find Me.mp3";
        playerStart = new Vector2(9.5f * Constants.TILE_SIZE, -Constants.TILE_SIZE);
        playerEnd = new Vector2(9.5f * Constants.TILE_SIZE, Constants.TILE_SIZE);
        mapSize = new Vector2(20, 34);
        tint = new Color(1, .8f, .8f, 1);
    }

    public void init(final Area area) {
        /* Dialogues */
        DialogueComponent talk = DialogueComponent.read("joker");
        talk.start = "start";
        talk.events.put("battle", new Runnable() {
            @Override
            public void run() {
                area.events.add(new StartCombat("boss"));
            }
        });

        DialogueComponent win = DialogueComponent.read("joker");
        win.start = "win";
        win.events.put("win", new Runnable() {
            @Override
            public void run() {
                area.events.add(new SetEntityVisibility("boss", false));
                area.events.add(new AddAttribute("portal"));
            }
        });

        final Entity activate = new Entity();
        final DialogueComponent activateDC = DialogueComponent.read("joker");
        activateDC.start = "activate";
        activateDC.small = true;
        activateDC.position = new Rectangle(0, 0, 120, 90);
        activateDC.events.put("portal", new Runnable() {
            @Override
            public void run() {
                area.events.add(new StartCombat("portal"));
            }
        });
        activate.add(activateDC);
        FollowComponent fc = new FollowComponent();
        fc.entity = "portal";
        fc.offset = new Vector2(20, -20);
        activate.add(fc);

        final Entity portalEntity = new Entity();
        final DialogueComponent portalDC = DialogueComponent.read("joker");
        portalDC.start = "portal";
        portalDC.small = true;
        portalDC.position = new Rectangle(0, 0, 120, 45);
        portalDC.events.put("portal", new Runnable() {
            @Override
            public void run() {

            }
        });
        portalEntity.add(portalDC);
        fc = new FollowComponent();
        fc.entity = "boss";
        fc.offset = new Vector2(20, 0);
        portalEntity.add(fc);

        /* Entities */
        Entity battle = new Entity();
        battle.add(new NameComponent("boss"));
        battle.add(new AreaComponent(area));
        battle.add(new ActorComponent(new Image(Main.getTexture("joker"))));
        battle.add(new PositionComponent(10 * Constants.TILE_SIZE, 30 * Constants.TILE_SIZE));
        if (!Player.getAttribute("portal")) battle.add(new VisibleComponent());
        CollisionComponent cc = new CollisionComponent(0, 0, 16, 8);
        cc.events.add(new StartDialogue(talk));
        battle.add(cc);

        Entity portal = new Entity();
        portal.add(new NameComponent("portal"));
        portal.add(new AreaComponent(area));
        portal.add(new ActorComponent(new Image(Main.getTexture("portal"))));
        portal.add(new PositionComponent(9 * Constants.TILE_SIZE, 30 * Constants.TILE_SIZE));
        portal.add(new VisibleComponent());
        cc = new CollisionComponent(0, 0, 16, 16);
        cc.events.add(new Event() {
            public void run(Context context) {
                // We need to calculate the dialogue to show right before we show it, not when loading the level
                context.events.add(new StartDialogue(Player.getAttribute("portal") ? activate : portalEntity));
                super.run(context);
            }});
        portal.add(cc);

        Entity town = new Entity();
        EnterZoneComponent ec = new EnterZoneComponent(area);
        ec.bounds.set(0, -Constants.TILE_SIZE, mapSize.x * Constants.TILE_SIZE, Constants.TILE_SIZE);
        ec.events.add(new ChangeContext("town1", new Vector2(10.5f * Constants.TILE_SIZE, 13 * Constants.TILE_SIZE), new Vector2(10.5f * Constants.TILE_SIZE, 12 * Constants.TILE_SIZE)));
        town.add(ec);

        /* Enemies */
        final Enemy.EnemyPrototype portalEnemy =  new Enemy.EnemyPrototype("portal", "portal", "a portal", new String[]{"..."}, new Vector2(0, 0), 5, new Attack.AttackPrototype(
                new String[]{"portal", "magic", "speed", "fast", "swarm", "mystery", "reflect", "flood"},
                "jingles_SAX16", "portal", Attack.Target.PLAYER, 1, Color.YELLOW, 12, 2, 5, false) {
            @Override
            public void run(Vector2 position, Attack attack) {
                Attack.Word word = getWord(attack);
                float y = position.y + MathUtils.random(-50, 50);
                word.start = new Vector2(position.x, y);
                word.end = new Vector2(attack.battle.playerPos.x, y);
                attack.addWord(word);
            }
        });

        Enemy.EnemyPrototype jokerEnemy = new Enemy.EnemyPrototype("joker", "joker", "the joker", new String[]{"Ha! You're 'sparing' me?", "I'm the boss, I can't accept your mercy", "seriously, stop. It won't work", "alright you called my bluff. It will, eventually", "but only after my filibuster!", "I make jokes for a living, I can come up with them forever!", "So why did the chicken cross the road?", "why, to get to the other side of course!", "hahahahaha!", "alright, that's actually all I got", "and per the rules of filibustering", "once I can't talk anymore", "...", "I have to step down", "I suppose you've 'bested' me"}, new Vector2(80, 200), 20, new Attack.AttackPrototype(new String[]{},
                "jingles_SAX16", "portalSpawn", Attack.Target.OTHER, 0, Color.BLACK, 0, 0, 1, false) {
            @Override
            public void run(Vector2 position, Attack attack) {
                if (attack.battle.turn % 2 == 0) return;
                Enemy enemy = new Enemy(portalEnemy, attack.battle);
                enemy.setPosition(position.x + MathUtils.random(50), position.y + MathUtils.randomSign() * MathUtils.random(50, 75));
                attack.battle.addEnemy(enemy);
            }
        });

        Enemy.EnemyPrototype portalAbilityEnemy = new Enemy.EnemyPrototype("portal", "portal", "their portal spell", new String[]{"alright, so this is an ability. You shouldn't be able to attack it anyways", "sparing an ability makes no sense", "good job casting this, I guess?"}, new Vector2(80, 180), 20, new Attack.AttackPrototype(
                new String[]{"portal", "magic", "speed", "fast", "swarm", "mystery"},
                "jingles_SAX16", "portal", Attack.Target.PLAYER, 1, Color.YELLOW, 12, 2.5f, 10, false) {
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
        Battle.BattlePrototype boss = new Battle.BattlePrototype("boss") {
            public void start(final Battle battle) {
                final DialogueComponent tutorial = DialogueComponent.read("joker");
                tutorial.start = "tutorial";
                tutorial.events.put("attack", new Runnable() {
                    @Override
                    public void run() {
                        battle.events.add(new NextAttack());
                    }
                });
                battle.events.add(new StartDialogue(tutorial));
            }
        };
        boss.enemies = new Enemy.EnemyPrototype[]{jokerEnemy};
        boss.winEvents.add(new StartDialogue(win));
        boss.bgm = "Searching.mp3";

        Battle.BattlePrototype portalAbility = new Battle.BattlePrototype("portal");
        portalAbility.enemies = new Enemy.EnemyPrototype[]{portalAbilityEnemy};
        // TODO win event to teleport
        portalAbility.winEvents.add(new Shutdown());
        portalAbility.bgm = "Searching.mp3";

        /* Adding things to area */
        entities = new Entity[]{battle, portal, town};
        battles = new Battle.BattlePrototype[]{boss, portalAbility};
    }
}
