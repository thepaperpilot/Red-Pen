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
import thepaperpilot.rpg.Components.Triggers.CollisionComponent;
import thepaperpilot.rpg.Components.Triggers.EnterZoneComponent;
import thepaperpilot.rpg.Components.VisibleComponent;
import thepaperpilot.rpg.Context;
import thepaperpilot.rpg.Events.*;
import thepaperpilot.rpg.Events.Shutdown;
import thepaperpilot.rpg.Main;
import thepaperpilot.rpg.Player;
import thepaperpilot.rpg.Util.Constants;

public class Throne extends Area.AreaPrototype {
    public Throne() {
        super("throne");

        /* Adding things to Area */
        bgm = "Come and Find Me.mp3";
        playerStart = new Vector2(7.5f * Constants.TILE_SIZE, -Constants.TILE_SIZE);
        playerEnd = new Vector2(7.5f * Constants.TILE_SIZE, Constants.TILE_SIZE);
        mapSize = new Vector2(16, 32);
        tint = new Color(1, .8f, .8f, 1);
    }

    public void init(Area area) {
        /* Entities */
        Entity battle = new Entity();
        battle.add(new NameComponent("boss"));
        battle.add(new ActorComponent(area, new Image(Main.getTexture("joker"))));
        battle.add(new PositionComponent(8 * Constants.TILE_SIZE, 30 * Constants.TILE_SIZE));
        if (!Player.getAttribute("portal")) battle.add(new VisibleComponent());
        CollisionComponent cc = new CollisionComponent(0, 0, 16, 8);
        cc.events.add(new StartDialogue("joker"));
        battle.add(cc);

        Entity portal = new Entity();
        portal.add(new NameComponent("portal"));
        portal.add(new ActorComponent(area, new Image(Main.getTexture("portal"))));
        portal.add(new PositionComponent(7 * Constants.TILE_SIZE, 30 * Constants.TILE_SIZE));
        portal.add(new VisibleComponent());
        cc = new CollisionComponent(0, 0, 16, 16);
        cc.events.add(new Event() {
            public void run(Context context) {
                // We need to calculate the dialogue to show right before we show it, not when loading the level
                context.events.add(new StartDialogue(Player.getAttribute("portal") ? "activate" : "portal"));
                super.run(context);
            }});
        portal.add(cc);

        Entity town = new Entity();
        EnterZoneComponent ec = new EnterZoneComponent(area);
        ec.bounds.set(0, -Constants.TILE_SIZE, mapSize.x * Constants.TILE_SIZE, Constants.TILE_SIZE);
        ec.events.add(new ChangeContext("town1", new Vector2(8.5f * Constants.TILE_SIZE, 13 * Constants.TILE_SIZE), new Vector2(8.5f * Constants.TILE_SIZE, 12 * Constants.TILE_SIZE)));
        town.add(ec);

        /* Dialogues */
        thepaperpilot.rpg.UI.Dialogue.Line line1 = new thepaperpilot.rpg.UI.Dialogue.Line("Wow, now I realize why I was the first boss! Like, I'm honestly ashamed of myself. Well, you've taken my powers, I hope they serve you well. Go on, activate the portal. You use abilities much like you use actions in battle. Be careful though, these abilities are much more difficult than regular actions!", "joker", "joker");
        thepaperpilot.rpg.UI.Dialogue.Line line2 = new thepaperpilot.rpg.UI.Dialogue.Line("This portal will bring you to the overworld for a short time. You can use it talk to someone back home, if you'd like. Use it carefully, though, as it can't be used very often. Good luck...", "joker", "joker");
        line2.events.add(new SetEntityVisibility("boss", false));
        line2.events.add(new AddAttribute("portal"));
        thepaperpilot.rpg.UI.Dialogue winDial = new thepaperpilot.rpg.UI.Dialogue("win", new thepaperpilot.rpg.UI.Dialogue.Line[]{line1, line2});

        thepaperpilot.rpg.UI.Dialogue portalDialogue = new thepaperpilot.rpg.UI.Dialogue.EntityDialogue("portal", new thepaperpilot.rpg.UI.Dialogue.Line[]{new thepaperpilot.rpg.UI.Dialogue.Line("woah woah woah. What are you trying to do with my portal? You don't have the ability to use it!")}, 4, "boss", new Vector2(20, 0), new Vector2(120, 45), true);

        line1 = new thepaperpilot.rpg.UI.Dialogue.Line("Oh? You're trying to get out of Hell, are you? Well if you hope to do that, you'll need my portal abilities. Unfortunately for you, I won't give them up without a fight.", "joker", "joker");
        line2 = new thepaperpilot.rpg.UI.Dialogue.Line("Well, yeah. You're the boss, that was to be expected. But admittedly, I was expecting someone more... boss-like?", "Player", "player");
        thepaperpilot.rpg.UI.Dialogue.Line line3 = new thepaperpilot.rpg.UI.Dialogue.Line("Trust me, I'm plenty 'boss-like'. Just you wait and see!", "joker", "joker");
        line3.events.add(new StartCombat("boss"));
        thepaperpilot.rpg.UI.Dialogue joker = new thepaperpilot.rpg.UI.Dialogue("joker", new thepaperpilot.rpg.UI.Dialogue.Line[]{line1, line2, line3});

        line1 = new thepaperpilot.rpg.UI.Dialogue.Line("You look at the portal. You can vaguely make out what appears to be your university. Do you wish to enter?");
        thepaperpilot.rpg.UI.Dialogue.Option yes = new thepaperpilot.rpg.UI.Dialogue.Option("yes");
        yes.events.add(new StartCombat("portal"));
        thepaperpilot.rpg.UI.Dialogue.Option no = new thepaperpilot.rpg.UI.Dialogue.Option("no");
        line1.options = new thepaperpilot.rpg.UI.Dialogue.Option[]{yes, no};
        thepaperpilot.rpg.UI.Dialogue activate = new thepaperpilot.rpg.UI.Dialogue.EntityDialogue("activate", new thepaperpilot.rpg.UI.Dialogue.Line[]{line1}, 0, "portal", new Vector2(20, -20), new Vector2(120, 90), true);

        line1 = new thepaperpilot.rpg.UI.Dialogue.Line("I'm gonna give you a head's up before starting this battle. I'm going to be creating portals, which are additional enemies. When dealing with multiple enemies, you can click on the one you want to attack to focus on it.", "joker", "joker");
        line2 = new thepaperpilot.rpg.UI.Dialogue.Line("I won't be attacking directly, but the battle won't end until I'm defeated. Not that a runt like you could actually do such a thing. Well good luck anyways, you'll need it.", "joker", "joker");
        line2.events.add(new NextAttack());
        final thepaperpilot.rpg.UI.Dialogue tutorial = new thepaperpilot.rpg.UI.Dialogue("tutorial", new thepaperpilot.rpg.UI.Dialogue.Line[]{line1, line2});

        /* Enemies */
        final Enemy.EnemyPrototype portalEnemy =  new Enemy.EnemyPrototype("portal", "portal", "a portal", new String[]{"..."}, new Vector2(0, 0), 5, new Attack.AttackPrototype(
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
        Battle.BattlePrototype boss = new Battle.BattlePrototype("boss") {
            public void start(Battle battle) {
                battle.events.add(new StartDialogue("tutorial"));
            }
        };
        boss.enemies = new Enemy.EnemyPrototype[]{jokerEnemy};
        boss.winEvents.add(new StartDialogue("win"));
        boss.bgm = "Searching.mp3";

        Battle.BattlePrototype portalAbility = new Battle.BattlePrototype("portal");
        portalAbility.enemies = new Enemy.EnemyPrototype[]{portalAbilityEnemy};
        // TODO win event to teleport
        portalAbility.winEvents.add(new Shutdown());
        portalAbility.bgm = "Searching.mp3";

        /* Adding things to area */
        entities = new Entity[]{battle, portal, town};
        dialogues = new thepaperpilot.rpg.UI.Dialogue[]{winDial, portalDialogue, joker, activate, tutorial};
        battles = new Battle.BattlePrototype[]{boss, portalAbility};
    }
}
