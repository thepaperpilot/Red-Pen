package thepaperpilot.rpg.UI;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import thepaperpilot.rpg.Area;
import thepaperpilot.rpg.Components.PositionComponent;
import thepaperpilot.rpg.Context;
import thepaperpilot.rpg.Events.EndCutscene;
import thepaperpilot.rpg.Events.Event;
import thepaperpilot.rpg.Events.StartCutscene;
import thepaperpilot.rpg.Main;
import thepaperpilot.rpg.Systems.TiledMapSystem;
import thepaperpilot.rpg.Util.Constants;
import thepaperpilot.rpg.Util.Mappers;

import java.util.ArrayList;

public class Dialogue extends Table {
    Context context;
    final Line[] lines;
    int line = 0;
    Option selected;
    public final String name;
    private final Image face = new Image();
    private final Label nameLabel = new Label("", Main.skin, "dialogue");
    protected final Table message = new Table(Main.skin);
    ScrollText messageLabel;
    private float maxTimer;
    private float timer;
    public ArrayList<Event> chain = new ArrayList<Event>();

    public Dialogue(String name, Line[] lines) {
        this(name, lines, 0, 100, false);
    }

    private Dialogue(String name, Line[] lines, float timer, float height, boolean smallFont) {
        super(Main.skin);
        this.name = name;
        setFillParent(true);
        pad(4);

        // create each part of the dialogue
        this.lines = lines;

        // if the dialogue is empty, let's go ahead and not do anything
        if (this.lines.length == 0)
            return;

        // create the dialogue stage
        face.setScale(6); // exact value TBD
        messageLabel = new ScrollText();
        messageLabel.setAlignment(Align.topLeft);
        messageLabel.setWrap(true);
        message.top().left();
        message.setBackground(Main.skin.getDrawable("default-round"));
        bottom().left().add(face).bottom().left().expand();
        add(nameLabel).bottom().row();
        add(message).colspan(2).expandX().fillX().height(height);
        message.add(new Label("Click to continue...", Main.skin)).expand().center().bottom();

        if (smallFont) messageLabel.setFontScale(.25f);

        this.timer = this.maxTimer = timer;
        if (maxTimer == 0) {
            // left click to advance the dialogue
            addListener(new InputListener() {
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    advance(false);
                    return false;
                }

                public boolean keyDown(InputEvent event, int keycode) {
                    switch (keycode) {
                        case Input.Keys.E:
                        case Input.Keys.ENTER:
                            advance(true);
                            break;
                        case Input.Keys.UP:
                        case Input.Keys.W:
                        case Input.Keys.A:
                            if (selected == null || line == 0) return false;
                            Line currLine = Dialogue.this.lines[line - 1];
                            for (int i = 0; i < currLine.options.length; i++) {
                                if (selected == currLine.options[i]) {
                                    if (i == 0)
                                        selected = currLine.options[currLine.options.length - 1];
                                    else selected = currLine.options[i - 1];
                                    break;
                                }
                            }
                            updateSelected();
                            break;
                        case Input.Keys.DOWN:
                        case Input.Keys.S:
                        case Input.Keys.D:
                            if (selected == null || line == 0) return false;
                            currLine = Dialogue.this.lines[line - 1];
                            for (int i = 0; i < currLine.options.length; i++) {
                                if (selected == currLine.options[i]) {
                                    if (i == currLine.options.length - 1)
                                        selected = currLine.options[0];
                                    else selected = currLine.options[i + 1];
                                    break;
                                }
                            }
                            updateSelected();
                            break;
                        default:
                            return false;
                    }
                    return true;
                }
            });
        }
    }

    public void open(Context context) {
        this.context = context;
        context.stage.addActor(this);
        if (timer == 0) context.stage.setKeyboardFocus(this);
        context.events.add(new StartCutscene());

        // start the dialogue
        if(line == 0) next();
    }

    public void act(float delta) {
        super.act(delta);
        if (timer != 0) {
            timer -= delta;
            if (timer <= 0) {
                timer = maxTimer;
                next();
            }
        }
    }

    private void advance(boolean override) {
        if (line > 0 && (override || lines[line  - 1].options.length == 0)) {
            if (messageLabel.getText().toString().equals(lines[line - 1].message)) {
                if (lines[line - 1].options.length == 0) next();
                else if (selected != null) selected.select(this);
            } else {
                messageLabel.finish();
            }
        }
    }

    void end() {
        line = 0;
        timer = maxTimer;
        next();
        remove();
        context.events.add(new EndCutscene());
        context.events.addAll(chain);
    }

    private void next() {
        if (line > 0) {
            // run last line's events
            context.events.addAll(lines[line - 1].events);
        }

        // check if we're done with the dialogue
        if (lines.length <= line) {
            end();
            return;
        }

        // update the dialogue stage for the next part of the dialogue
        Line nextLine = lines[line];
        line++;
        face.setDrawable(nextLine.face.equals("") ? null : new Image(Main.getTexture(nextLine.face)).getDrawable());
        nameLabel.setText(nextLine.name);
        nameLabel.setVisible(nextLine.name != null);
        messageLabel.setMessage(nextLine.message);
        message.clearChildren();
        message.add(messageLabel).expandX().fillX().left().padBottom(5).row();
        if (nextLine.options.length == 0) {
            if (maxTimer == 0)
                message.add(new Label("Click to continue...", Main.skin)).expand().center().bottom();
        } else {
            for (int i = 0; i < nextLine.options.length; i++) {
                nextLine.options[i].reset(this);
                message.add(nextLine.options[i]).left().padLeft(10).row();
            }
            selected = nextLine.options[0];
            updateSelected();
        }
        if (maxTimer == 0) {
            setTouchable(nextLine.options.length == 0 ? Touchable.enabled : Touchable.childrenOnly);
        }

        Main.click();
    }

    public void updateSelected() {
        if (line == 0) return;
        for (int i = 0; i < lines[line - 1].options.length; i++) {
            Option option = lines[line - 1].options[i];
            if (selected == option) {
                option.setText(" > " + option.message);
                option.setColor(Color.ORANGE);
            } else {
                option.setText("> " + option.message);
                option.setColor(Color.WHITE);
            }
        }
    }

    public static class Option extends Label {
        public final ArrayList<Event> events = new ArrayList<Event>();
        final String message;

        public Option(String message) {
            super("> " + message, Main.skin, "large");
            this.message = message;
        }

        public void reset(final Dialogue dialogue) {
            // do the actions when this button is clicked
            addListener(new InputListener() {
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    select(dialogue);
                    return true;
                }

                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    dialogue.selected = Option.this;
                    dialogue.updateSelected();
                }
            });
        }

        public void select(Dialogue dialogue) {
            Main.click();
            dialogue.context.events.addAll(events);
            dialogue.selected = null;
            dialogue.next();
        }
    }

    public static class ScrollText extends Label {
        private float time = 0;
        private int chars = 0;
        public String message = "";

        public ScrollText() {
            super("", Main.skin, "large");
        }

        public void act(float delta) {
            super.act(delta);
            if (!message.equals("")) {
                time = Math.min(time += delta, message.length() / Constants.TEXT_SPEED);
                if (chars < Math.min(message.length(), (int) (time * Constants.TEXT_SPEED))) {
                    Main.click();
                    chars += 3;
                }
                setText(message.substring(0, Math.min(message.length(), (int) (time * Constants.TEXT_SPEED))));
            }
        }

        public void setMessage(String message) {
            this.message = message;
            time = chars = 0;
        }

        public void finish() {
            chars = message.length();
            time = message.length();
            Main.click();
        }
    }

    public static class Line {
        public String name;
        final String message;
        public String face = "";
        public final ArrayList<Event> events = new ArrayList<Event>();
        public Option[] options = new Option[]{};

        public Line(String message) {
            this.message = message;
        }

        public Line(String message, String name) {
            this(message);
            this.name = name;
        }

        public Line(String message, String name, String face) {
            this(message, name);
            this.face = face;
        }
    }

    public static class SmallDialogue extends Dialogue {

        public SmallDialogue(String name, Line[] lines, float timer, Vector2 position, Vector2 size, boolean smallFont) {
            super(name, lines, timer, size.y, smallFont);
            setFillParent(false);
            setPosition(position.x - size.x / 2, position.y - size.y / 2);
            setSize(size.x, size.y);
            if (smallFont) this.messageLabel.setFontScale(.25f);
            this.messageLabel.setAlignment(Align.center);
        }
    }

    public static class EntityDialogue extends SmallDialogue {
        final String entity;
        final Vector2 offset;

        public EntityDialogue(String name, Line[] lines, float time, String entity, Vector2 offset, Vector2 size, boolean smallFont) {
            super(name, lines, time, new Vector2(0, 0), size, smallFont);
            this.entity = entity;
            this.offset = offset;
        }

        public void act(float delta) {
            super.act(delta);
            Entity entity = this.entity.equals("player") ? ((Area) context).player : ((Area) context).entities.get(this.entity);
            OrthographicCamera camera = context.engine.getSystem(TiledMapSystem.class).camera;
            PositionComponent pc = Mappers.position.get(entity);
            Vector3 pos = camera.project(new Vector3(pc.position.x + offset.x, pc.position.y + offset.y, 0));
            setPosition(pos.x * context.stage.getWidth() / Gdx.graphics.getWidth(), pos.y * context.stage.getHeight() / Gdx.graphics.getHeight());
        }
    }

    public static EntityDialogue alert(String entity) {
        EntityDialogue dialogue = new EntityDialogue("alert", new Line[]{new Line("!")}, 3, entity, new Vector2(4, 22), new Vector2(0, 0), false);
        dialogue.message.setBackground(new Image().getDrawable());
        return dialogue;
    }
}
