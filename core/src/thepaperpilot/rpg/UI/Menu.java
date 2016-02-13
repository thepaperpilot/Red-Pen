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
        final Dialogue dialogue = instance.menu.getDialogue(context);
        dialogue.addListener(new InputListener() {
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    dialogue.end();
                    event.cancel();
                }
                return true;
            }
        });
        context.addDialogue(dialogue);
    }

    private Dialogue.DialoguePrototype menu;

    private Menu() {
        menu = new Dialogue.DialoguePrototype();
        menu.type = Dialogue.DialougeType.SMALL;
        menu.size = new Vector2(100, 70);
        menu.position = new Vector2(20 + menu.size.x / 2, 360 - 20 - menu.size.y / 2);
        Dialogue.LinePrototype line = new Dialogue.LinePrototype();
        line.message = "MENU";
        Dialogue.Option save = new Dialogue.Option("save", new Event.EventPrototype[]{new Event.EventPrototype(Event.Type.SAVE)});
        Dialogue.Option exit = new Dialogue.Option("exit", new Event.EventPrototype[]{new Event.EventPrototype(Event.Type.TITLE)});
        Dialogue.Option equip = new Dialogue.Option("equip", new Event.EventPrototype[]{}) {
            public void select(Dialogue dialogue) {
                super.select(dialogue);
                Dialogue.DialoguePrototype prototype = getInventory();
                final Table descTable = new Table(Main.skin);
                final Label description = new Label("", Main.skin);
                final Dialogue inventory = new Dialogue.SmallDialogue(prototype, dialogue.context, prototype.position, prototype.size, false) {
                    public void updateSelected() {
                        if (line == 0) return;
                        for (int i = 0; i < lines.get(line - 1).options.length; i++) {
                            Option option = lines.get(line - 1).options[i];
                            if (selected == option) {
                                option.setColor(Color.ORANGE);
                            } else {
                                option.setColor(Color.WHITE);
                            }
                            boolean selected = false;
                            for (Attack.AttackPrototype attackPrototype : Player.getAttacks()) {
                                if (attackPrototype.getOption() == option) {
                                    selected = true;
                                    break;
                                }
                            }
                            option.setText(selected ? "> " + option.message + " <" : option.message);
                        }

                        Attack.AttackPrototype attack = null;
                        for (Attack.AttackPrototype attackPrototype : Player.getInventory()) {
                            if (attackPrototype.getOption() == selected) {
                                attack = attackPrototype;
                                break;
                            }
                        }
                        descTable.setVisible(attack != null);
                        if (attack != null) {
                            description.setText(attack.description);
                        }
                    }
                };
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
                dialogue.context.addDialogue(inventory);
            }
        };
        line.options = new Dialogue.Option[]{save, exit, equip};
        menu.lines = new Dialogue.LinePrototype[]{line};
    }

    private Dialogue.DialoguePrototype getInventory() {
        final Dialogue.DialoguePrototype inventory = new Dialogue.DialoguePrototype();
        inventory.type = Dialogue.DialougeType.SMALL;
        Dialogue.LinePrototype line = new Dialogue.LinePrototype();
        line.message = "INVENTORY";
        GlyphLayout layout = new GlyphLayout(Main.skin.getFont("large"), "INVENTORY");
        float width = layout.width;
        float height = layout.height + 9;
        ArrayList<Dialogue.Option> options = new ArrayList<Dialogue.Option>();
        for (Attack.AttackPrototype attackPrototype : Player.getInventory()) {
            options.add(attackPrototype.getOption());
            layout.setText(Main.skin.getFont("large"), attackPrototype.getOption().message + ">  <");
            width = Math.max(width, layout.width) + 10;
            height += layout.height + 4;
        }
        options.add(new Dialogue.Option(" exit", new Event.EventPrototype[]{}) {
            public void select(Dialogue dialogue) {
                dialogue.end();
            }
        });
        layout.setText(Main.skin.getFont("large"), " exit");
        height += layout.height + 4;
        inventory.size = new Vector2(width + 7, height + 6);
        inventory.position = new Vector2(20 + inventory.size.x / 2, 360 - 20 - inventory.size.y / 2);
        line.options = options.toArray(new Dialogue.Option[options.size()]);
        inventory.lines = new Dialogue.LinePrototype[]{line};
        return inventory;
    }
}
