package thepaperpilot.rpg.Systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import thepaperpilot.rpg.Components.*;
import thepaperpilot.rpg.Components.Triggers.CollisionComponent;
import thepaperpilot.rpg.Screens.Area;
import thepaperpilot.rpg.Util.Constants;
import thepaperpilot.rpg.Util.Mappers;

import java.util.HashSet;
import java.util.Set;

public class PlayerSystem extends IteratingSystem {
    public PlayerSystem() {
        super(Family.all(PlayerComponent.class, ActorComponent.class, PositionComponent.class, CollisionComponent.class, AreaComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float delta) {
        for (Entity entity1 : getEngine().getEntitiesFor(Family.all(DialogueComponent.class).get()))
            if (entity1.getComponent(IgnoreComponent.class) == null) return;

        PositionComponent pc = Mappers.position.get(entity);
        AreaComponent ac = Mappers.area.get(entity);

        final boolean w = Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP);
        final boolean a = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT);
        final boolean s = Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN);
        final boolean d = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);

        float xVel = 0;
        float yVel = 0;
        float sqrt = 1.41421356f; // approximately sqrt(2)

        if (a && !d) {
            xVel = 1;
        } else if (d && !a) {
            xVel = -1;
        }
        if (w && !s) {
            yVel = -1;
        } else if (s && !w) {
            yVel = 1;
        }
        if (xVel != 0 && yVel != 0) {
            xVel /= sqrt;
            yVel /= sqrt;
        }
        if (xVel != 0 || yVel != 0) pc.angle = new Vector2(-xVel, -yVel).angle();

        float newX = pc.position.x - Constants.MOVE_SPEED * xVel * delta;
        float newY = pc.position.y - Constants.MOVE_SPEED * yVel * delta;
        CollisionComponent cc = Mappers.collision.get(entity);
        if (newX != pc.position.x && walkable(ac.area, new Rectangle(cc.bounds.x + newX, cc.bounds.y + pc.position.y, cc.bounds.width, cc.bounds.height))) pc.position.x = newX;
        if (newY != pc.position.y && walkable(ac.area, new Rectangle(cc.bounds.x + pc.position.x, cc.bounds.y + newY, cc.bounds.width, cc.bounds.height))) pc.position.y = newY;
        Mappers.playerController.get(entity).target.set(pc.position.cpy().add(cc.bounds.width / 2, cc.bounds.height / 2).add(new Vector2(cc.bounds.width * MathUtils.cosDeg(pc.angle), cc.bounds.height * MathUtils.sinDeg(pc.angle))));

        if (Mappers.walk.has(entity)) {
            Vector2 diff = new Vector2(Constants.MOVE_SPEED * -xVel * delta, Constants.MOVE_SPEED * -yVel * delta);
            WalkSystem.updateFacing(entity, diff, delta);
        }
    }

    private boolean walkable(Area area, Rectangle bounds) {
        for (Entity entity : area.engine.getEntitiesFor(Family.all(PositionComponent.class, CollisionComponent.class).get())) {
            if (!Mappers.visible.has(entity)) continue;
            if (Mappers.playerController.has(entity)) continue;
            PositionComponent pc = Mappers.position.get(entity);
            CollisionComponent cc = Mappers.collision.get(entity);
            Rectangle entityBounds = new Rectangle(pc.position.x + cc.bounds.x, pc.position.y + cc.bounds.y, cc.bounds.width, cc.bounds.height);
            if (bounds.overlaps(entityBounds)) {
                if (Mappers.trigger.has(entity)) {
                    Mappers.trigger.get(entity).run(area);
                    continue;
                }
                if (Mappers.walkable.has(entity)) {
                    cc.run(area);
                } else return false;
            }
        }
        Set<TiledMapTileLayer.Cell> cells = new HashSet<TiledMapTileLayer.Cell>();
        // each of the 4 corners of the bounds rectangle
        TiledMapTileLayer layer = ((TiledMapTileLayer) area.engine.getSystem(TiledMapSystem.class).tiledMap.getLayers().get("collisions"));
        cells.add(layer.getCell(MathUtils.floor(bounds.x / Constants.TILE_SIZE), MathUtils.floor(bounds.y / Constants.TILE_SIZE)));
        cells.add(layer.getCell(MathUtils.floor(bounds.x / Constants.TILE_SIZE), MathUtils.floor((bounds.y + bounds.height) / Constants.TILE_SIZE)));
        cells.add(layer.getCell(MathUtils.floor((bounds.x + bounds.width) / Constants.TILE_SIZE), MathUtils.floor(bounds.y / Constants.TILE_SIZE)));
        cells.add(layer.getCell(MathUtils.floor((bounds.x + bounds.width) / Constants.TILE_SIZE), MathUtils.floor((bounds.y + bounds.height) / Constants.TILE_SIZE)));

        for (TiledMapTileLayer.Cell cell : cells) {
            if (cell == null) continue;
            MapProperties properties = cell.getTile().getProperties();
            if (!(properties.containsKey("b") && properties.get("b").equals("1"))) return false;
        }
        return true;
    }
}
