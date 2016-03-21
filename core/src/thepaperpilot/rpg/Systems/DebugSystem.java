package thepaperpilot.rpg.Systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import thepaperpilot.rpg.Components.PositionComponent;
import thepaperpilot.rpg.Components.Triggers.CollisionComponent;
import thepaperpilot.rpg.Util.Mappers;

public class DebugSystem extends IteratingSystem{
    private final ShapeRenderer renderer = new ShapeRenderer();

    public DebugSystem() {
        super(Family.all(CollisionComponent.class).get());
    }

    @Override
    public void update(float delta) {
        renderer.setProjectionMatrix(getEngine().getSystem(TiledMapSystem.class).camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        super.update(delta);
        renderer.end();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent pc = Mappers.position.get(entity);
        CollisionComponent cc = Mappers.collision.get(entity);

        renderer.setColor(Mappers.walkable.has(entity) ? Color.GREEN : Color.RED);
        renderer.rect(pc.position.x + cc.bounds.x, pc.position.y + cc.bounds.y, cc.bounds.width, cc.bounds.height);

        if (Mappers.playerController.has(entity)) {
            Vector2 pos = Mappers.playerController.get(entity).target;
            renderer.setColor(Color.BLACK);
            renderer.point(pos.x, pos.y, 0);
        }
    }
}
