package thepaperpilot.rpg;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import thepaperpilot.rpg.Battles.Battle;
import thepaperpilot.rpg.Components.*;
import thepaperpilot.rpg.Components.Triggers.CollisionComponent;
import thepaperpilot.rpg.Events.EndCutscene;
import thepaperpilot.rpg.Events.Event;
import thepaperpilot.rpg.Events.MoveEntity;
import thepaperpilot.rpg.Listeners.ActorListener;
import thepaperpilot.rpg.Listeners.NameListener;
import thepaperpilot.rpg.Systems.*;
import thepaperpilot.rpg.UI.Menu;
import thepaperpilot.rpg.Util.Constants;
import thepaperpilot.rpg.Util.Mappers;

import java.util.HashMap;
import java.util.Map;

public class Area extends Context {
    public final AreaPrototype prototype;
    public final Entity player;
    public final Map<String, Entity> entities = new HashMap<String, Entity>();
    public final Map<String, Battle.BattlePrototype> battles = new HashMap<String, Battle.BattlePrototype>();
    public Vector3 cameraTarget;
    public float zoomTarget = 1;
    public Entity entityTarget;
    public TiledMapSystem.CAMERA_STATES cameraState = TiledMapSystem.CAMERA_STATES.ENTITY;
    public final Stage mapActors;

    public Area(AreaPrototype prototype) {
        super(prototype);
        this.prototype = prototype;
        mapActors = new Stage(new ExtendViewport(prototype.mapSize.x * Constants.TILE_SIZE, prototype.mapSize.y * Constants.TILE_SIZE));

        /* Add Systems */
        engine.addSystem(new ActorSystem(mapActors));
        engine.addSystem(new ChangeActorSystem());
        engine.addSystem(new PlayerControlledSystem());
        engine.addSystem(new TargetSystem());
        engine.addSystem(new TiledMapSystem(this));
        engine.addSystem(new ZoneSystem());
        engine.addSystem(new DebugSystem());

        /* Add Listeners */
        engine.addEntityListener(Family.all(ActorComponent.class).get(), new ActorListener());
        engine.addEntityListener(Family.all(NameComponent.class).get(), new NameListener(this));

        /* Add Entities */
        entityTarget = player = new Entity();
        player.add(new ActorComponent(this, new Image(Main.getTexture("player"))));
        player.add(new CollisionComponent(0, 0, 16, 8));
        player.add(new NameComponent("player"));
        player.add(new PlayerControllerComponent());
        player.add(new PositionComponent(prototype.playerStart));
        player.add(new VisibleComponent());
        player.add(new WalkingComponent());
        engine.addEntity(player);

        /* Add some listeners */
        // Because I'm not sure how to do events with Ashley, otherwise I could add these in the PlayerControlledSystem
        stage.addListener(new InputListener() {
            public boolean keyDown(InputEvent event, int keycode) {
                if (!engine.getSystem(PlayerControlledSystem.class).checkProcessing()) return false;

                switch (keycode) {
                    case Input.Keys.ESCAPE:
                        Menu.open(Area.this);
                        break;
                    case Input.Keys.E:case Input.Keys.ENTER:
                        for (Entity entity : engine.getEntitiesFor(Family.all(PositionComponent.class, CollisionComponent.class).get())) {
                            if (Mappers.walkable.has(entity)) continue;
                            if (!Mappers.visible.has(entity)) continue;
                            if (Mappers.playerController.has(entity)) continue;

                            Vector2 pos = Mappers.playerController.get(player).target;

                            PositionComponent pcEntity = Mappers.position.get(entity);
                            CollisionComponent cc = Mappers.collision.get(entity);
                            Rectangle entityBounds = new Rectangle(pcEntity.position.x + cc.bounds.x, pcEntity.position.y + cc.bounds.y, cc.bounds.width, cc.bounds.height);

                            if (entityBounds.contains(pos)) {
                                cc.run(Area.this);
                                return false;
                            }
                        }
                        break;
                }
                return false;
            }
        });
        //noinspection PointlessBooleanExpression
        if (!Constants.DEBUG) {
            engine.getSystem(DebugSystem.class).setProcessing(false);
        }
    }

    public void init() {
        prototype.init(this);
        super.init();

        for (int i = 0; i < prototype.entities.length; i++) {
            engine.addEntity(prototype.entities[i]);
        }

        for (int i = 0; i < prototype.battles.length; i++) {
            battles.put(prototype.battles[i].name, prototype.battles[i]);
        }
    }

    @Override
    public void show() {
        super.show();
        engine.getSystem(TiledMapSystem.class).updateCamera(100);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        TiledMapSystem system = engine.getSystem(TiledMapSystem.class);
        system.viewport.update(width, height);
        system.updateCamera(100);
    }

    @Override
    public void dispose() {
        super.dispose();
        engine.getSystem(TiledMapSystem.class).tiledMap.dispose();
    }

    public static class AreaPrototype extends ContextPrototype {
        public final String name;
        public Vector2 viewport = new Vector2(8 * Constants.TILE_SIZE, 8 * Constants.TILE_SIZE);
        public Vector2 playerStart = new Vector2(64, 64);
        public Vector2 playerEnd = new Vector2(64, 64);
        public Vector2 mapSize = new Vector2(32, 32);
        protected Entity[] entities = new Entity[]{};
        protected Battle.BattlePrototype[] battles = new Battle.BattlePrototype[]{};

        protected AreaPrototype(String name) {
            this.name = name;
        }

        public void init(Area area) {

        }

        final public Context getContext() {
            return getContext(playerStart, playerEnd);
        }

        public Context getContext(Vector2 start, Vector2 end) {
            Area area = new Area(this);
            area.init();
            moveEvent(start, end, area);
            return area;
        }

        public Event moveEvent(Vector2 start, Vector2 end, Area area) {
            MoveEntity startEvent = new MoveEntity("player", start.x, start.y, true);
            MoveEntity endEvent = new MoveEntity("player", end.x, end.y, false);
            Event event = new EndCutscene();
            startEvent.chain.add(endEvent);
            endEvent.chain.add(event);
            area.events.add(startEvent);
            return event;
        }
    }
}
