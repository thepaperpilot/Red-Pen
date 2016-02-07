package thepaperpilot.rpg;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;

public class Dialogue extends Table {
    private final Context context;
    private final ArrayList<Line> lines = new ArrayList<Line>();
    private final Image face = new Image();
    private final Label nameLabel = new Label("", Main.skin, "dialogue");
    private final Table message = new Table(Main.skin);
    private final Label messageLabel = new Label("", Main.skin, "large");
    private int line = 0;
    private Option selected;

    public Dialogue(final DialoguePrototype prototype, Context context) {
        super(Main.skin);
        this.context = context;
        setFillParent(true);
        setTouchable(Touchable.enabled);

        // create each part of the dialogue
        for (LinePrototype line : prototype.lines) {
            lines.add(new Line(line, this));
        }

        // if the dialogue is empty, let's go ahead and not do anything
        if (lines.size() == 0)
            return;

        // create the dialogue stage
        face.setScale(6); // exact value TBD
        messageLabel.setAlignment(Align.topLeft);
        messageLabel.setWrap(true);
        message.top().left();
        message.setBackground(Main.skin.getDrawable("default-round"));
        bottom().left().add(face).bottom().left().expand();
        add(nameLabel).bottom().row();
        add(message).colspan(2).expandX().fillX().height(100).row();
        message.add(new Label("Click to continue...", Main.skin)).expand().center().bottom();

        // left click to advance the dialogue
        addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (line > 0 && lines.get(line - 1).options.length == 0) next();
                return true;
            }

            public boolean keyDown(InputEvent event, int keycode) {
                switch (keycode) {
                    case Input.Keys.E:
                    case Input.Keys.ENTER:
                        if (line > 0 && lines.get(line - 1).options.length == 0) next();
                        else if (selected != null) selected.select();
                        break;
                    case Input.Keys.UP:
                    case Input.Keys.W:
                    case Input.Keys.A:
                        if (selected == null || line == 0) return false;
                        Line currLine = lines.get(line - 1);
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
                        currLine = lines.get(line - 1);
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

        // start the dialogue
        next();
    }

    private void next() {
        if (line > 0) {
            // run last line's events
            for (Event event : lines.get(line - 1).events) {
                event.run();
            }
        }

        // check if we're done with the dialogue
        if (lines.size() <= line) {
            line = 0;
            next();
            remove();
            return;
        }

        // update the dialogue stage for the next part of the dialogue
        Line nextLine = lines.get(line);
        line++;
        face.setDrawable(nextLine.face);
        nameLabel.setText(nextLine.name);
        nameLabel.setVisible(nextLine.name != null);
        messageLabel.setText(nextLine.message);
        message.clearChildren();
        message.add(messageLabel).expandX().fillX().left().padBottom(5).row();
        if (nextLine.options.length == 0) {
            message.add(new Label("Click to continue...", Main.skin)).expand().center().bottom();
        } else {
            for (int i = 0; i < nextLine.options.length; i++) {
                message.add(nextLine.options[i]).left().padLeft(10).row();
            }
            selected = nextLine.options[0];
            updateSelected();
        }

        Main.manager.get("click1.ogg", Sound.class).play();
    }

    private void updateSelected() {
        if (line == 0) return;
        for (int i = 0; i < lines.get(line - 1).options.length; i++) {
            Option option = lines.get(line - 1).options[i];
            if (selected == option) {
                option.setText(" > " + option.message);
                option.setColor(Color.ORANGE);
            } else {
                option.setText("> " + option.message);
                option.setColor(Color.WHITE);
            }
        }
    }

    static class Option extends Label {
        final Event[] events;
        final String message;
        final Dialogue dialogue;

        Option(OptionPrototype prototype, final Dialogue dialogue) {
            // indicate this is a button by preceding it with a ">"
            super("> " + prototype.message, Main.skin, "large");
            message = prototype.message;
            this.dialogue = dialogue;

            // create the actions to occur when the option is selected
            events = new Event[prototype.events.length];
            for (int i = 0; i < prototype.events.length; i++) {
                events[i] = new Event(prototype.events[i], dialogue.context);
            }

            // do the actions when this button is clicked
            addListener(new InputListener() {
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    select();
                    return true;
                }

                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    dialogue.selected = Option.this;
                    dialogue.updateSelected();
                }
            });
        }

        public void select() {
            Main.manager.get("click1.ogg", Sound.class).play();
            for (Event ev : events) {
                ev.run();
            }
            dialogue.selected = null;
            dialogue.next();
        }
    }

    public static class DialoguePrototype {
        public String name;
        public LinePrototype[] lines = new LinePrototype[]{};
    }

    public static class LinePrototype {
        public String name;
        public String message;
        public String face;
        public Event.EventPrototype[] events = new Event.EventPrototype[]{};
        public OptionPrototype[] options = new OptionPrototype[]{};
    }

    public static class OptionPrototype {
        public String message;
        public Event.EventPrototype[] events;
    }

    static class Line {
        final String name;
        final String message;
        final Event[] events;
        final Option[] options;
        Drawable face;

        Line(LinePrototype prototype, Dialogue dialogue) {
            name = prototype.name;
            message = prototype.message;

            events = new Event[prototype.events.length];
            for (int i = 0; i < prototype.events.length; i++) {
                events[i] = new Event(prototype.events[i], dialogue.context);
            }

            options = new Option[prototype.options.length];
            for (int i = 0; i < prototype.options.length; i++) {
                options[i] = new Option(prototype.options[i], dialogue);
            }

            // create the face for the talker
            if (prototype.face != null) {
                face = new Image(Main.getTexture(prototype.face)).getDrawable();
            }
        }
    }
}
