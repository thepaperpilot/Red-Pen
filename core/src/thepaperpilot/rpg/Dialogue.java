package thepaperpilot.rpg;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;

public class Dialogue extends Table {
    String name;
    private final ArrayList<Line> lines = new ArrayList<Line>();
    private Image face = new Image();
    private Label nameLabel = new Label("", Main.skin, "dialogue");
    private Table message = new Table(Main.skin);
    private Label messageLabel = new Label("", Main.skin, "large");
    private int line = 0;

    public Dialogue(DialoguePrototype prototype) {
        super(Main.skin);
        name = prototype.name;
        setFillParent(true);
        setTouchable(Touchable.enabled);

        // create each part of the dialogue
        for (LinePrototype line : prototype.lines) {
            lines.add(new Line(line));
        }

        // if the dialogue is empty, let's go ahead and not do anything
        if (lines.size() == 0)
            return;

        // create the dialogue ui
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
        addListener(new ClickListener(Input.Buttons.LEFT) {
            public void clicked(InputEvent event, float x, float y) {
                next();
            }
        });

        // right click to undo the conversation
        addListener(new ClickListener(Input.Buttons.RIGHT) {
            public void clicked(InputEvent event, float x, float y) {
                remove();
            }
        });

        // start the dialogue
        next();
    }

    public void next() {
        // check if we're done with the dialogue
        if (lines.size() <= line) {
            line = 0;
            next();
            remove();
            return;
        }

        // update the dialogue ui for the next part of the dialogue
        Line nextLine = lines.get(line);
        face.setDrawable(nextLine.face);
        nameLabel.setText(nextLine.name);
        messageLabel.setText(nextLine.message);
        message.clearChildren();
        message.add(messageLabel).expandX().fillX().left().padBottom(5).row();
        message.add(new Label("Click to continue...", Main.skin)).expand().center().bottom();

        line++;

        Main.manager.get("click1.ogg", Sound.class).play();
    }

    public static class DialoguePrototype {
        String name;
        LinePrototype[] lines = new LinePrototype[]{};
    }

    public static class LinePrototype {
        String name;
        String message;
        String face;
    }

    static class Line {
        String name;
        String message;
        Drawable face;

        Line(LinePrototype prototype) {
            name = prototype.name;
            message = prototype.message;

            // create the face for the talker
            if (prototype.face != null) {
                face = new Image(Main.getTexture(prototype.face)).getDrawable();
            }
        }
    }
}
