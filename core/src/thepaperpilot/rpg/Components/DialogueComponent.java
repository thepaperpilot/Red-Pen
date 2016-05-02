package thepaperpilot.rpg.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Json;
import thepaperpilot.rpg.UI.Line;
import thepaperpilot.rpg.UI.Option;
import thepaperpilot.rpg.UI.ScrollText;

import java.util.HashMap;
import java.util.Map;

public class DialogueComponent implements Component {
    public HashMap<String, Line> lines = new HashMap<String, Line>();
    public String start = "";
    public String player = "player";
    public String[] actors = new String[]{};
    transient public Map<String, Runnable> events = new HashMap<String, Runnable>();
    public boolean small = false;
    public boolean background = true;
    public Rectangle position;

    transient public String line;
    transient public Option selected;
    transient public float timer;

    transient public Image playerFace;
    transient public Image actorFace;
    transient public Table faces;
    transient public Table message;
    transient public ScrollText messageLabel;

    public static DialogueComponent read(String file) {
        return new Json().fromJson(DialogueComponent.class, Gdx.files.internal("dialogues/" + file + ".json").readString());
    }

    public static Entity alert(String name, Vector2 offset) {
        Entity entity = new Entity();
        DialogueComponent dc = new DialogueComponent();
        dc.start = "start";
        dc.small = true;
        dc.position = new Rectangle(offset.x, offset.y, 4, 22);
        Line line = new Line("!");
        line.timer = 2;
        dc.lines.put("start", line);
        dc.background = false;
        entity.add(dc);
        FollowComponent fc = new FollowComponent();
        fc.entity = name;
        fc.offset = offset;
        entity.add(fc);
        return entity;
    }
}
