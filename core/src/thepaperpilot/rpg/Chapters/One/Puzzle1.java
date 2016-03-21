package thepaperpilot.rpg.Chapters.One;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import thepaperpilot.rpg.Area;
import thepaperpilot.rpg.Battles.Attack;
import thepaperpilot.rpg.Battles.Battle;
import thepaperpilot.rpg.Battles.Enemy;
import thepaperpilot.rpg.Components.*;
import thepaperpilot.rpg.Components.Triggers.CollisionComponent;
import thepaperpilot.rpg.Components.Triggers.EnterZoneComponent;
import thepaperpilot.rpg.Components.Triggers.LeaveZoneComponent;
import thepaperpilot.rpg.Context;
import thepaperpilot.rpg.Events.*;
import thepaperpilot.rpg.Main;
import thepaperpilot.rpg.Player;
import thepaperpilot.rpg.Util.Constants;
import thepaperpilot.rpg.Util.ParticleEffectActor;

import java.util.ArrayList;
import java.util.Collections;

public class Puzzle1 extends Area.AreaPrototype {
    private final Rectangle puzzleZoom = new Rectangle(12 * Constants.TILE_SIZE, 12 * Constants.TILE_SIZE, 8 * Constants.TILE_SIZE, 6 * Constants.TILE_SIZE);

    public Puzzle1() {
        super("puzzle1");

        /* Adding things to area */
        bgm = "Digital Native.mp3";
        viewport = new Vector2(16 * Constants.TILE_SIZE, 16 * Constants.TILE_SIZE);
        playerStart = new Vector2(-Constants.TILE_SIZE, 15 * Constants.TILE_SIZE);
        playerEnd = new Vector2(Constants.TILE_SIZE, 15 * Constants.TILE_SIZE);
        mapSize = new Vector2(32, 32);
        tint = new Color(1, .8f, .8f, 1);
    }

    public void init(final Area area) {
        /* Entities */
        ArrayList<Entity> entities = new ArrayList<Entity>();

        Entity habit = new Entity();
        habit.add(new NameComponent("habit"));
        habit.add(new ActorComponent(area, new Image(Main.getTexture("demonOld"))));
        habit.add(new PositionComponent(7 * Constants.TILE_SIZE, 15 * Constants.TILE_SIZE));
        if (!Player.getAttribute("puzzle1Explain")) habit.add(new VisibleComponent());
        entities.add(habit);

        Entity nm = new Entity();
        nm.add(new NameComponent("nm"));
        nm.add(new ActorComponent(area, new Image(Main.getTexture("talker"))));
        nm.add(new PositionComponent(13 * Constants.TILE_SIZE, 25 * Constants.TILE_SIZE));
        if (!Player.getAttribute("nm1") && Player.getAttribute("nmScroll")) nm.add(new VisibleComponent());
        nm.add(new CollisionComponent(0, 0, 16, 8));
        entities.add(nm);

        Entity[] buttons = new Entity[18];
        final ArrayList<Entity> remainingButtons = new ArrayList<Entity>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                buttons[i * 7 + j] = makeButton(area, remainingButtons, 13 + 2 * j, 13 + 2 * i);
            }
        }
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[4 + 7 * i + j] = makeButton(area, remainingButtons, 14 + 2 * j, 14 + 2 * i);
            }
        }
        Collections.addAll(entities, buttons);
        Collections.addAll(remainingButtons, buttons);

        boolean rocks = !Player.getAttribute("puzzle1");
        entities.add(makeRock(1, area, 13, 29, rocks));
        entities.add(makeRock(2, area, 14, 29, rocks));
        entities.add(makeRock(3, area, 24, 16, rocks));
        entities.add(makeRock(4, area, 24, 15, rocks));
        entities.add(makeRock(5, area, 24, 14, rocks));

        Entity zoom = new Entity();
        EnterZoneComponent ec = new EnterZoneComponent(area);
        ec.bounds.set(puzzleZoom);
        ec.events.add(new LockCamera(16.5f * Constants.TILE_SIZE, 15 * Constants.TILE_SIZE, .5f, false));
        ec.repeatable = true;
        zoom.add(ec);
        LeaveZoneComponent lc = new LeaveZoneComponent(area);
        lc.bounds.set(puzzleZoom);
        lc.events.add(new ReleaseCamera());
        lc.repeatable = true;
        zoom.add(lc);
        entities.add(zoom);

        if (!Player.getAttribute("puzzle1Explain")) {
            Entity explain = new Entity();
            ec = new EnterZoneComponent(area);
            ec.bounds.set(5 * Constants.TILE_SIZE, 0, Constants.TILE_SIZE, mapSize.y * Constants.TILE_SIZE);
            ec.events.add(new StartDialogue("puzzle"));
            ec.events.add(new AddAttribute("puzzle1Explain"));
            explain.add(ec);
            entities.add(explain);
        }

        Entity corridor = new Entity();
        ec = new EnterZoneComponent(area);
        ec.bounds.set(-Constants.TILE_SIZE, 0, Constants.TILE_SIZE, mapSize.y * Constants.TILE_SIZE);
        ec.events.add(new ChangeContext("corridor1", new Vector2(23 * Constants.TILE_SIZE, 5 * Constants.TILE_SIZE), new Vector2(21 * Constants.TILE_SIZE, 5 * Constants.TILE_SIZE)));
        corridor.add(ec);
        entities.add(corridor);

        Entity scroll = new Entity();
        ec = new EnterZoneComponent(area);
        ec.bounds.set(0, 31 * Constants.TILE_SIZE, mapSize.x * Constants.TILE_SIZE, Constants.TILE_SIZE);
        ec.events.add(new ChangeContext("scroll"));
        scroll.add(ec);
        entities.add(scroll);

        Entity town = new Entity();
        ec = new EnterZoneComponent(area);
        ec.bounds.set(31 * Constants.TILE_SIZE, 0, Constants.TILE_SIZE, mapSize.y * Constants.TILE_SIZE);
        ec.events.add(new ChangeContext("town1"));
        town.add(ec);
        entities.add(town);

        if (!Player.getAttribute("nm1") && Player.getAttribute("nmScroll")) {
            Entity nmEntity = new Entity();
            ec = new EnterZoneComponent(area);
            ec.bounds.set(0, 26 * Constants.TILE_SIZE, mapSize.x * Constants.TILE_SIZE, Constants.TILE_SIZE);
            ec.events.add(new StartDialogue("nm"));
            ec.events.add(new AddAttribute("nm1"));
            nmEntity.add(ec);
            entities.add(nmEntity);
        }

        /* Dialogues */
        thepaperpilot.rpg.UI.Dialogue.Line line1 = new thepaperpilot.rpg.UI.Dialogue.Line("Ha! What courage to hear all that and still come forward", "HABIT", "demonOld");
        thepaperpilot.rpg.UI.Dialogue.Line line2 = new thepaperpilot.rpg.UI.Dialogue.Line("What a shame, though, for it also marks you as the fool you are.", "HABIT", "demonOld");
        thepaperpilot.rpg.UI.Dialogue.Line line3 = new thepaperpilot.rpg.UI.Dialogue.Line("Just to prove how foolish you are I've set up the first of many puzzles designed to torment you. You see before you a grid of switches. One of them unlocks the way forward", "HABIT", "demonOld");
        line3.events.add(new EntityCamera("habit", .75f, true));
        thepaperpilot.rpg.UI.Dialogue.Line line4 = new thepaperpilot.rpg.UI.Dialogue.Line("The other buttons.", "HABIT", "demonOld");
        line4.events.add(new EntityCamera("habit", .5f, true));
        thepaperpilot.rpg.UI.Dialogue.Line line5 = new thepaperpilot.rpg.UI.Dialogue.Line("Will.", "HABIT", "demonOld");
        line5.events.add(new EntityCamera("habit", .25f, true));
        thepaperpilot.rpg.UI.Dialogue.Line line6 = new thepaperpilot.rpg.UI.Dialogue.Line("Not!", "HABIT", "demonOld");
        line6.events.add(new ReleaseCamera(true));
        thepaperpilot.rpg.UI.Dialogue.Line line7 = new thepaperpilot.rpg.UI.Dialogue.Line("Hahahaha! Haha! Sometimes my trickery astounds even myself! And the best part is, the correct switch will always be the last one you press! Muahhaa good luck, living one!", "HABIT", "demonOld");
        line7.events.add(new SetEntityVisibility("habit", false));
        thepaperpilot.rpg.UI.Dialogue puzzle = new thepaperpilot.rpg.UI.Dialogue("puzzle", new thepaperpilot.rpg.UI.Dialogue.Line[]{line1, line2, line3, line4, line5, line6, line7});

        line1 = new thepaperpilot.rpg.UI.Dialogue.Line("nmnmnnm mnmnmnnmn nmnmnmnm", "nm", "talker");
        line2 = new thepaperpilot.rpg.UI.Dialogue.Line("nmnmn nnmn nmnmnmnm nmnmnnmnmnnmnnm", "nm", "talker");
        line2.events.add(new StartCombat("nm"));
        thepaperpilot.rpg.UI.Dialogue nmDialogue = new thepaperpilot.rpg.UI.Dialogue("nm", new thepaperpilot.rpg.UI.Dialogue.Line[]{line1, line2});

        line1 = new thepaperpilot.rpg.UI.Dialogue.Line("Ah! Thank you! I had been cursed, but it is now gone! Thank you many times, that scroll you have there has been incredibly helpful", "nm", "talker");
        line2 = new thepaperpilot.rpg.UI.Dialogue.Line("Hey, look... There are others like me. And honestly, most are a lot stronger than me. Could you help them out by reversing the curse on them as well? It'd mean a lot to them, and by extension me. They're... confused in that state, and will fight back. It's a lot to ask, so I thank you now.", "nm", "talker");
        line3 = new thepaperpilot.rpg.UI.Dialogue.Line("And I suppose you probably want some sort of reward, for your efforts? Well, here you go. You can use this to attack things, and it should be stronger than what you already have. You can customize what actions you bring into battle in the gear menu by pressing 'ESC'. Just remember you can only select up to 5.", "nm", "talker");
        line3.events.add(new Event() {
            public void run(Context context) {
                Player.addInventory("stick");
                Player.save((Area) context);
            }
        });
        thepaperpilot.rpg.UI.Dialogue nmScroll = new thepaperpilot.rpg.UI.Dialogue("nmScroll", new thepaperpilot.rpg.UI.Dialogue.Line[]{line1, line2, line3});

        /* Enemies */
        final Enemy.EnemyPrototype nmEnemy = new Enemy.EnemyPrototype("nm", "talker", "an nmenemy", new String[]{"nmnmnmn?", "nmnmn nmnmnm nmnmnmn!", "nmnnmn! mnmnmn?!?! mnnnmnmn!", "...", "nmnmnmn."}, new Vector2(80, 180), 20, new Attack.AttackPrototype(
                new String[]{"n", "m"},
                "jingles_SAX16", "nm", Attack.Target.PLAYER, 1, Color.CORAL, 2, .3f, 30, false) {
            @Override
            public void run(Vector2 position, Attack attack) {
                Attack.Word word = getWord(attack);
                word.start = new Vector2(position.x + 10, MathUtils.random(360));
                word.end = attack.battle.playerPos;
                attack.addWord(word);
            }
        });

        /* Battles */
        Battle.BattlePrototype nmFight = new Battle.BattlePrototype("nm") {
            final String[] bank = new String[]{"nmnmnnnmmmmn", "nmnmn nmnmnmnmnmn nmnmn", "nnnnnmmmmmmmm", "nmnmnmnmnm nmnmnmnm"};

            public void update(Battle battle) {
                new thepaperpilot.rpg.UI.Dialogue.SmallDialogue("fight", new thepaperpilot.rpg.UI.Dialogue.Line[]{new thepaperpilot.rpg.UI.Dialogue.Line(bank[MathUtils.random(bank.length - 1)])}, 4, new Vector2(nmEnemy.position.x + 120, nmEnemy.position.y + 10), new Vector2(180, 12), true).open(battle);
            }
        };
        nmFight.enemies = new Enemy.EnemyPrototype[]{nmEnemy};
        nmFight.winEvents.add(new SetEntityVisibility("nm", false));
        nmFight.bgm = "Come and Find Me.mp3";

        /* Adding things to Area */
        this.entities = entities.toArray(new Entity[entities.size()]);
        dialogues = new thepaperpilot.rpg.UI.Dialogue[]{puzzle, nmDialogue, nmScroll};
        battles = new Battle.BattlePrototype[]{nmFight};

        new ParticleEffectActor.EnvironmentParticleEffect("hell", area);
    }

    private Entity makeButton(final Area area, final ArrayList<Entity> remainingButtons, int x, int y) {
        final Entity entity = new Entity();
        final ActorComponent ac = new ActorComponent(area, new Image(Main.getTexture("buttonUp")));
        entity.add(ac);
        entity.add(new PositionComponent(x * Constants.TILE_SIZE, y * Constants.TILE_SIZE));
        entity.add(new VisibleComponent());
        entity.add(new WalkableComponent());
        CollisionComponent cc = new CollisionComponent(1, 1, 9, 9);
        cc.events.add(new Event() {
            @Override
            public void run(Context context) {
                entity.add(new ChangeActorComponent(new Image(Main.getTexture("buttonDown"))));
                if (remainingButtons.remove(entity)) Main.click();
                if (remainingButtons.isEmpty()) solvePuzzle(area);
                entity.remove(CollisionComponent.class);
                super.run(context);
            }
        });
        entity.add(cc);
        return entity;
    }

    private Entity makeRock(int i, Area area, int x, int y, boolean visible) {
        Entity rock = new Entity();
        rock.add(new NameComponent("rock" + i));
        rock.add(new ActorComponent(area, new Image(Main.getTexture("rock"))));
        rock.add(new PositionComponent(x * Constants.TILE_SIZE, y * Constants.TILE_SIZE));
        if (visible) rock.add(new VisibleComponent());
        rock.add(new CollisionComponent(4, 4, 8, 8));
        return rock;
    }

    private void solvePuzzle(Area area) {
        if (Player.getAttribute("puzzle1")) return;
        Player.addAttribute("puzzle1");
        // I might want to chain these, but I'd want pauses between them anyways, so...
        area.events.add(new StartCutscene());
        area.events.add(new LockCamera(24 * Constants.TILE_SIZE, 15 * Constants.TILE_SIZE, .5f, true));
        for (int i = 0; i < 3; i++) {
            Event ec = new SetEntityVisibility("rock" + (i + 3), false);
            ec.delay = 1;
            area.events.add(ec);
        }
        Event ec = new LockCamera(13 * Constants.TILE_SIZE, 29 * Constants.TILE_SIZE, .5f, true);
        ec.delay = 2;
        area.events.add(ec);
        for (int i = 0; i < 2; i++) {
            ec = new SetEntityVisibility("rock" + (i + 1), false);
            ec.delay = 3;
            area.events.add(ec);
        }
        ec = new ReleaseCamera(true);
        ec.delay = 4;
        area.events.add(ec);
        ec = new EndCutscene();
        ec.delay = 4;
        area.events.add(ec);
    }
}
