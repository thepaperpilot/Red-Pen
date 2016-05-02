package thepaperpilot.rpg.Chapters.One;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import thepaperpilot.rpg.Components.*;
import thepaperpilot.rpg.Components.Triggers.CollisionComponent;
import thepaperpilot.rpg.Components.Triggers.EnterZoneComponent;
import thepaperpilot.rpg.Events.*;
import thepaperpilot.rpg.Main;
import thepaperpilot.rpg.Screens.Area;
import thepaperpilot.rpg.Screens.Context;
import thepaperpilot.rpg.UI.ParticleEffectActor;
import thepaperpilot.rpg.Util.Constants;
import thepaperpilot.rpg.Util.Player;

public class Corridor1 extends Area.AreaPrototype {
    public Corridor1() {
        super("corridor1");

        bgm = "Digital Native.mp3";
        viewport = new Vector2(6 * Constants.TILE_SIZE, 6 * Constants.TILE_SIZE);
        playerStart = playerEnd = new Vector2(5 * Constants.TILE_SIZE, 4 * Constants.TILE_SIZE);
        mapSize = new Vector2(27, 23);
        tint = new Color(1, .8f, .8f, 1);
    }

    public void init(final Area area) {
        /* Entities */
        Entity demonOld = new Entity();
        demonOld.add(new NameComponent("habit"));
        demonOld.add(new AreaComponent(area));
        demonOld.add(new ActorComponent(new Image(Main.getTexture("demonOld"))));
        demonOld.add(new PositionComponent(18 * Constants.TILE_SIZE, 5 * Constants.TILE_SIZE));
        if (!Player.getAttribute("corridor1")) demonOld.add(new VisibleComponent());

        Entity flower = new Entity();
        flower.add(new NameComponent("flower"));
        flower.add(new AreaComponent(area));
        flower.add(new ActorComponent(new Image(Main.getTexture("thisiswhyimnotanartist"))));
        flower.add(new PositionComponent(19 * Constants.TILE_SIZE, 17 * Constants.TILE_SIZE));
        flower.add(new VisibleComponent());
        CollisionComponent cc = new CollisionComponent(0, 0, 16, 8);
        cc.events.add(new Event() {
            public void run(Context context) {
                if (!Player.getAttribute("spare")) {
                    DialogueComponent spare = DialogueComponent.read("corridor");
                    spare.start = "spare";
                    spare.events.put("spare", new Runnable() {
                        @Override
                        public void run() {
                            area.events.add(new AddAttribute("spare"));
                            Player.addInventory("spare");
                            Player.save(area);
                        }
                    });
                    new StartDialogue(spare).run(area);
                } else {
                    DialogueComponent flower = DialogueComponent.read("corridor");
                    flower.start = "flower";
                    new StartDialogue(flower).run(area);
                }
                super.run(context);
            }
        });
        flower.add(cc);

        Entity puzzle1 = new Entity();
        EnterZoneComponent ec = new EnterZoneComponent(area);
        ec.bounds.set(26 * Constants.TILE_SIZE, 0, Constants.TILE_SIZE, mapSize.y * Constants.TILE_SIZE);
        ec.events.add(new ChangeContext("puzzle1"));
        puzzle1.add(ec);

        /* Adding things to area */
        entities = new Entity[]{demonOld, flower, puzzle1};

        new ParticleEffectActor.EnvironmentParticleEffect("hell", area);
    }

    public Context getContext(Vector2 start, Vector2 end) {
        final Area area = new Area(this);
        area.init();
        Event ec = moveEvent(start, end, area);
        if (!Player.getAttribute("corridor1")) {
            Player.addAttribute("corridor1");
            StartCutscene cc = new StartCutscene();
            MoveEntity mc = new MoveEntity("habit", 5 * Constants.TILE_SIZE, 5 * Constants.TILE_SIZE, false);
            mc.delay = 2;
            ec.chain.add(cc);
            cc.chain.add(mc);
            DialogueComponent welcome = DialogueComponent.read("corridor");
            welcome.start = "welcome";
            welcome.events.put("end", new Runnable() {
                @Override
                public void run() {
                    area.events.add(new SetEntityVisibility("habit", false));
                    area.events.add(new EndCutscene());
                }
            });
            mc.chain.add(new StartDialogue(welcome));
        }
        return area;
    }
}
