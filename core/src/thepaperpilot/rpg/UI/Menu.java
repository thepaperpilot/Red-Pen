package thepaperpilot.rpg.UI;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import thepaperpilot.rpg.Battles.Attack;
import thepaperpilot.rpg.Context;
import thepaperpilot.rpg.Event;
import thepaperpilot.rpg.Main;
import thepaperpilot.rpg.Player;

import java.util.ArrayList;

public class Menu {

    private static final Menu instance = new Menu();
    public static final Label error = new Label("You can only select 5 actions", Main.skin);

    public static void open(Context context) {
        instance.menu.open(context);
    }

    private Dialogue menu;
    private Table descTable;
    private Label description;

    private Menu() {
        Vector2 size = new Vector2(100, 70);
        Vector2 position = new Vector2(20 + size.x / 2, 360 - 20 - size.y / 2);
        Dialogue.Line line = new Dialogue.Line("Menu");
        Dialogue.Option save = new Dialogue.Option("save", new Event[]{new Event(Event.Type.SAVE)});
        Dialogue.Option exit = new Dialogue.Option("exit", new Event[]{new Event(Event.Type.TITLE)});
        Dialogue.Option equip = new Dialogue.Option("equip", new Event[]{}) {
            public void select(Dialogue dialogue) {
                super.select(dialogue);
                final Dialogue inventory = getInventory();
                descTable = new Table(Main.skin);
                description = new Label("", Main.skin);
                description.setWrap(true);
                descTable.setBackground(Main.skin.getDrawable("default-round"));
                descTable.add(description).top().width(200).fillY().expand();
                inventory.add(descTable).fillY().expandY();
                inventory.row();
                error.setColor(1, 1, 1, 0);
                inventory.add(error);
                inventory.addListener(new InputListener() {
                    public boolean keyDown(InputEvent event, int keycode) {
                        if (keycode == Input.Keys.ESCAPE) {
                            inventory.end();
                            event.cancel();
                        }
                        return true;
                    }
                });
                inventory.open(dialogue.context);
            }
        };
        line.options = new Dialogue.Option[]{save, exit, equip};
        menu = new Dialogue.SmallDialogue("", new Dialogue.Line[]{line}, 0, position, size, false);menu.addListener(new InputListener() {
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    instance.menu.end();
                    event.cancel();
                }
                return true;
            }
        });
    }

    private Dialogue getInventory() {
        Dialogue.Line line = new Dialogue.Line("Inventory");
        GlyphLayout layout = new GlyphLayout(Main.skin.getFont("large"), "INVENTORY");
        float width = layout.width;
        float height = layout.height + 9;
        ArrayList<Dialogue.Option> options = new ArrayList<Dialogue.Option>();
        for (Attack attack : Player.getInventory()) {
            options.add(attack.option);
            layout.setText(Main.skin.getFont("large"), attack.option.message + ">  <");
            width = Math.max(width, layout.width) + 10;
            height += layout.height + 4;
        }
        options.add(new Dialogue.Option(" exit", new Event[]{}) {
            public void select(Dialogue dialogue) {
                dialogue.end();
            }
        });
        layout.setText(Main.skin.getFont("large"), " exit");
        height += layout.height + 4;
        Vector2 size = new Vector2(width + 7, height + 6);
        Vector2 position = new Vector2(20 + size.x / 2, 360 - 20 - size.y / 2);
        line.options = options.toArray(new Dialogue.Option[options.size()]);
        return new Dialogue.SmallDialogue("", new Dialogue.Line[]{line}, 0, position, size, false){
            public void updateSelected() {
                if (line == 0) return;
                for (int i = 0; i < lines[line - 1].options.length; i++) {
                    Option option = lines[line - 1].options[i];
                    if (selected == option) {
                        option.setColor(Color.ORANGE);
                    } else {
                        option.setColor(Color.WHITE);
                    }
                    boolean selected = false;
                    for (Attack attack : Player.getAttacks()) {
                        if (attack.option == option) {
                            selected = true;
                            break;
                        }
                    }
                    option.setText(selected ? "> " + option.message + " <" : option.message);
                }

                Attack attack = null;
                for (Attack action : Player.getInventory()) {
                    if (action.option == selected) {
                        attack = action;
                        break;
                    }
                }
                descTable.setVisible(attack != null);
                if (attack != null) {
                    description.setText(attack.prototype.description);
                }
            }
        };
    }
}
