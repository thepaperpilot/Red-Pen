package thepaperpilot.rpg.Chapters.One;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import thepaperpilot.rpg.Area;
import thepaperpilot.rpg.Battles.Attack;
import thepaperpilot.rpg.Components.ActorComponent;
import thepaperpilot.rpg.Components.NameComponent;
import thepaperpilot.rpg.Components.PositionComponent;
import thepaperpilot.rpg.Components.Triggers.CollisionComponent;
import thepaperpilot.rpg.Components.Triggers.EnterZoneComponent;
import thepaperpilot.rpg.Components.VisibleComponent;
import thepaperpilot.rpg.Context;
import thepaperpilot.rpg.Events.ChangeContext;
import thepaperpilot.rpg.Events.Event;
import thepaperpilot.rpg.Events.SetEntityVisibility;
import thepaperpilot.rpg.Events.StartDialogue;
import thepaperpilot.rpg.Main;
import thepaperpilot.rpg.Player;
import thepaperpilot.rpg.Util.Constants;
import thepaperpilot.rpg.Util.ParticleEffectActor;

public class ScrollRoom extends Area.AreaPrototype {
    public ScrollRoom() {
        super("scroll");

        /* Adding things to area */
        bgm = "Arpanauts.mp3";
        viewport = new Vector2(4 * Constants.TILE_SIZE, 4 * Constants.TILE_SIZE);
        playerStart = new Vector2(3 * Constants.TILE_SIZE, -Constants.TILE_SIZE);
        playerEnd = new Vector2(3 * Constants.TILE_SIZE, Constants.TILE_SIZE);
        mapSize = new Vector2(7, 8);
        tint = new Color(1, .8f, 1, 1);
    }

    public void init(Area area) {
        /* Entities */
        Entity scroll = new Entity();
        scroll.add(new NameComponent("scroll"));
        scroll.add(new ActorComponent(area, new Image(Main.getTexture("scroll"))));
        scroll.add(new PositionComponent(3 * Constants.TILE_SIZE, 5 * Constants.TILE_SIZE));
        if (!Player.getAttribute("nmScroll")) scroll.add(new VisibleComponent());
        CollisionComponent cc = new CollisionComponent(0, 0, 16, 8);
        cc.events.add(new StartDialogue("scroll"));
        scroll.add(cc);

        Entity leave = new Entity();
        EnterZoneComponent ec = new EnterZoneComponent(area);
        ec.bounds.set(0, -Constants.TILE_SIZE, mapSize.x * Constants.TILE_SIZE, Constants.TILE_SIZE);
        ec.events.add(new ChangeContext("puzzle1", new Vector2(13.5f * Constants.TILE_SIZE, 32 * Constants.TILE_SIZE), new Vector2(13.5f * Constants.TILE_SIZE, 30 * Constants.TILE_SIZE)));
        leave.add(ec);

        /* Dialogues */
        thepaperpilot.rpg.UI.Dialogue.Line line1 = new thepaperpilot.rpg.UI.Dialogue.Line("You pick up the scroll");
        thepaperpilot.rpg.UI.Dialogue.Line line2 = new thepaperpilot.rpg.UI.Dialogue.Line("The scroll reads 'HOW TO SPELL AWAY THE NMNMNMs'");
        Event addAttack = new Event() {
            public void run(Context context) {
                Attack attack = new Attack(Attack.prototypes.get("nmScroll"));
                Player.addInventory(attack);
                Player.addAttack(attack);
                Player.addAttribute("nmScroll");
                Player.save((Area) context);
            }
        };
        Event hideScroll = new SetEntityVisibility("scroll", false);
        line2.events.add(addAttack);
        line2.events.add(hideScroll);
        thepaperpilot.rpg.UI.Dialogue scrollDialogue = new thepaperpilot.rpg.UI.Dialogue("scroll", new thepaperpilot.rpg.UI.Dialogue.Line[]{line1, line2});

        /* Adding things to Area */
        entities = new Entity[]{scroll, leave};
        dialogues = new thepaperpilot.rpg.UI.Dialogue[]{scrollDialogue};

        new ParticleEffectActor.EnvironmentParticleEffect("scroll", area);
    }
}
