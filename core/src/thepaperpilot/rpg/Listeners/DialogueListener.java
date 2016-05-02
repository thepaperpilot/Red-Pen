package thepaperpilot.rpg.Listeners;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import thepaperpilot.rpg.Components.*;
import thepaperpilot.rpg.Main;
import thepaperpilot.rpg.Screens.Area;
import thepaperpilot.rpg.Screens.Context;
import thepaperpilot.rpg.Systems.DialogueSystem;
import thepaperpilot.rpg.Systems.TiledMapSystem;
import thepaperpilot.rpg.UI.ScrollText;
import thepaperpilot.rpg.Util.Constants;
import thepaperpilot.rpg.Util.Mappers;

public class DialogueListener implements EntityListener {

    private Context context;
    private Engine engine;

    public DialogueListener(Context context, Engine engine) {
        this.context = context;
        this.engine = engine;
    }

    @Override
    public void entityAdded(final Entity entity) {
        final DialogueComponent dc = Mappers.dialogue.get(entity);
        ActorComponent ac = new ActorComponent();
        entity.add(ac);

        dc.playerFace = new Image();
        dc.actorFace = new Image();

        dc.messageLabel = new ScrollText(false);
        dc.messageLabel.setAlignment(Align.topLeft);
        dc.messageLabel.setWrap(true);
        dc.message = new Table(Main.skin);
        dc.message.top().left();
        if (dc.background)
            dc.message.setBackground(Main.skin.getDrawable("default-round"));
        dc.message.pad(4);

        dc.faces = new Table(Main.skin);
        Table dialogue;
        if (Mappers.follow.has(entity)) {
            final FollowComponent fc = Mappers.follow.get(entity);

            dialogue = new Table(Main.skin) {
                @Override
                public void act(float delta) {
                    super.act(delta);
                    Entity entity = fc.entity.equals("player") ? ((Area) context).player : ((Area) context).entities.get(fc.entity);
                    OrthographicCamera camera = context.engine.getSystem(TiledMapSystem.class).camera;
                    PositionComponent pc = Mappers.position.get(entity);
                    Vector3 pos = camera.project(new Vector3(pc.position.x + fc.offset.x, pc.position.y + fc.offset.y, 0));
                    setPosition(pos.x * context.stage.getWidth() / Gdx.graphics.getWidth(), pos.y * context.stage.getHeight() / Gdx.graphics.getHeight());
                }
            };
        } else {
            dialogue = new Table(Main.skin);
        }
        dialogue.pad(2).setFillParent(!dc.small);
        dialogue.bottom().left().add(dc.faces).expand().fill().padBottom(4).row();
        dialogue.add(dc.message).colspan(2).expandX().fillX().height(dc.small ? dc.position.height : Constants.DIALOGUE_SIZE);
        if (dc.small) {
            dialogue.setPosition(dc.position.getX(), dc.position.getY(), Align.center);
            dialogue.setSize(dc.position.width, dc.position.height);
            dc.messageLabel.setAlignment(Align.center);
        }
        ac.actor = dialogue;
        context.stage.addActor(ac.actor);
        ac.actor.toFront();

        if (Mappers.inventory.has(entity)) {
            InventoryComponent ic = Mappers.inventory.get(entity);

            dialogue.add(ic.descTable).fillY().expandY();
            dialogue.row();
            dialogue.add(ic.error);
            dialogue.addListener(new InputListener() {
                @Override
                public boolean keyDown(InputEvent event, int keycode) {
                    if (keycode == Input.Keys.ESCAPE) {
                        engine.removeEntity(entity);
                        event.cancel();
                    }
                    return true;
                }
            });
        }

        ac.actor.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                engine.getSystem(DialogueSystem.class).advance(entity, false);
                return false;
            }

            public boolean keyDown(InputEvent event, int keycode) {
                switch (keycode) {
                    case Input.Keys.E:
                    case Input.Keys.ENTER:
                    case Input.Keys.SPACE:
                        engine.getSystem(DialogueSystem.class).advance(entity, true);
                        break;
                    case Input.Keys.UP:
                    case Input.Keys.W:
                    case Input.Keys.A:
                        DialogueSystem.moveSelection(entity, -1);
                        break;
                    case Input.Keys.DOWN:
                    case Input.Keys.S:
                    case Input.Keys.D:
                        DialogueSystem.moveSelection(entity, 1);
                        break;
                    default:
                        return false;
                }
                event.cancel();
                return true;
            }
        });

        if (Mappers.menu.has(entity)) {
            ac.actor.addListener(new InputListener() {
                public boolean keyDown(InputEvent event, int keycode) {
                    if (keycode == Input.Keys.ESCAPE) {
                        engine.removeEntity(entity);
                        event.cancel();
                    }
                    return true;
                }
            });
        }

        context.stage.setKeyboardFocus(ac.actor);
        engine.getSystem(DialogueSystem.class).next(entity, dc.start);
    }

    @Override
    public void entityRemoved(Entity entity) {
        ActorComponent ac = Mappers.actor.get(entity);

        ac.actor.remove();
    }
}
