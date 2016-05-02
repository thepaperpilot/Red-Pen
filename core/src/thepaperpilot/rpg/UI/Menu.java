package thepaperpilot.rpg.UI;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import thepaperpilot.rpg.Battles.Attack;
import thepaperpilot.rpg.Components.DialogueComponent;
import thepaperpilot.rpg.Components.InventoryComponent;
import thepaperpilot.rpg.Components.MenuComponent;
import thepaperpilot.rpg.Main;
import thepaperpilot.rpg.Screens.Area;
import thepaperpilot.rpg.Screens.Context;
import thepaperpilot.rpg.Screens.Title;
import thepaperpilot.rpg.Util.Player;

import java.util.ArrayList;

public class Menu {

    private static final Menu instance = new Menu();
    public static final Label error = new Label("You can only select 5 actions", Main.skin);

    public static void open(Context context) {
        if (context instanceof Area)
            instance.area = ((Area) context);
        context.engine.addEntity(instance.menu);
    }

    public Area area;
    private final Entity menu;
    private Table descTable;
    private Label description;

    private Menu() {
        Vector2 size = new Vector2(100, 70);
        Vector2 position = new Vector2(20, 360 - 20 - size.y);
        Line line = new Line("Menu");
        Option save = new Option("save", false);
        save.event = "save";
        Option exit = new Option("exit", false);
        exit.event = "exit";
        Option equip = new Option("equip", false);
        equip.event = "equip";
        line.options = new Option[]{save, exit, equip};
        DialogueComponent dc = new DialogueComponent();
        dc.start = "start";
        dc.lines.put("start", line);
        dc.small = true;
        dc.position = new Rectangle(position.x, position.y, size.x, size.y);
        dc.events.put("save", new Runnable() {
            @Override
            public void run() {
                Player.save(area);
            }
        });
        dc.events.put("exit", new Runnable() {
            @Override
            public void run() {
                Main.changeScreen(new Title());
            }
        });
        dc.events.put("equip", new Runnable() {
            @Override
            public void run() {
                final Entity entity = getInventory();
                descTable = new Table(Main.skin);
                description = new Label("", Main.skin);
                description.setWrap(true);
                descTable.setBackground(Main.skin.getDrawable("default-round"));
                descTable.add(description).top().width(200).fillY().expand();
                InventoryComponent ic = new InventoryComponent();
                error.setColor(1, 1, 1, 0);
                ic.description = description;
                ic.descTable = descTable;
                ic.error = error;
                entity.add(ic);
                area.engine.addEntity(entity);
            }
        });

        menu = new Entity();
        menu.add(dc);
        menu.add(new MenuComponent());
    }

    private Entity getInventory() {
        final Entity entity = new Entity();
        DialogueComponent dc = new DialogueComponent();
        entity.add(dc);
        Line line = new Line("Inventory");
        GlyphLayout layout = new GlyphLayout(Main.skin.getFont("large"), "INVENTORY");
        float width = layout.width;
        float height = layout.height + 9;
        ArrayList<Option> options = new ArrayList<Option>();
        for (Attack attack : Player.getInventory()) {
            options.add(attack.option);
            layout.setText(Main.skin.getFont("large"), attack.option.message + ">  <");
            width = Math.max(width, layout.width) + 10;
            height += layout.height + 4;
        }
        Option exit = new Option("exit", false);
        exit.event = "exit";
        options.add(exit);
        layout.setText(Main.skin.getFont("large"), " exit");
        height += layout.height + 4;
        Vector2 size = new Vector2(width + 7, height + 6);
        Vector2 position = new Vector2(20, 360 - 20 - size.y);
        line.options = options.toArray(new Option[options.size()]);
        dc.start = "start";
        dc.lines.put("start", line);
        dc.small = true;
        dc.position = new Rectangle(position.x, position.y, size.x, size.y);
        dc.events.put("exit", new Runnable() {
            @Override
            public void run() {
                area.engine.removeEntity(entity);
            }
        });
        return entity;
    }
}
