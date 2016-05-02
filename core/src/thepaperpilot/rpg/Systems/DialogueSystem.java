package thepaperpilot.rpg.Systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import thepaperpilot.rpg.Battles.Attack;
import thepaperpilot.rpg.Components.ActorComponent;
import thepaperpilot.rpg.Components.DialogueComponent;
import thepaperpilot.rpg.Components.IgnoreComponent;
import thepaperpilot.rpg.Components.InventoryComponent;
import thepaperpilot.rpg.Main;
import thepaperpilot.rpg.UI.Line;
import thepaperpilot.rpg.UI.Option;
import thepaperpilot.rpg.Util.Constants;
import thepaperpilot.rpg.Util.Mappers;
import thepaperpilot.rpg.Util.Player;

public class DialogueSystem extends IteratingSystem {

    public DialogueSystem() {
        super(Family.all(DialogueComponent.class).get(), 5);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        DialogueComponent dc = Mappers.dialogue.get(entity);

        if (dc.timer != 0) {
            dc.timer -= deltaTime;
            if (dc.timer <= 0) {
                next(entity, dc.lines.get(dc.line).next);
            }
        }
    }

    public void advance(Entity entity, boolean override) {
        DialogueComponent dc = Mappers.dialogue.get(entity);

        if (override || dc.lines.get(dc.line).options.length == 0) {
            if (dc.messageLabel.isFinished()) {
                if (dc.lines.get(dc.line).options.length == 0) next(entity, dc.lines.get(dc.line).next);
                else if (dc.selected != null) dc.selected.select(entity, this);
            } else {
                dc.messageLabel.finish();
            }
        }
    }

    public void next(Entity entity, String start) {
        DialogueComponent dc = Mappers.dialogue.get(entity);

        if (dc.line == null)
            dc.line = start;
        else if (dc.events.get(dc.lines.get(dc.line).event) != null)
            dc.events.get(dc.lines.get(dc.line).event).run();

        if (!dc.lines.containsKey(start)) {
            getEngine().removeEntity(entity);
            return;
        }

        // update the dialogue stage for the next part of the dialogue
        Line line = dc.lines.get(dc.line = start);
        if (line.face == 0) {
            TextureRegion face = Main.portraits.findRegion(dc.player);
            dc.playerFace.setDrawable(new TextureRegionDrawable(face));
            dc.playerFace.setSize(face.getRegionWidth() * Constants.FACE_SIZE, face.getRegionHeight() * Constants.FACE_SIZE);
            dc.playerFace.setColor(1, 1, 1, 1);
            dc.actorFace.setColor(.5f, .5f, .5f, 1);
        } else if (line.face > 0) {
            dc.playerFace.setColor(.5f, .5f, .5f, 1);
            TextureRegion face = Main.portraits.findRegion(dc.actors[line.face - 1]);
            dc.actorFace.setDrawable(new TextureRegionDrawable(face));
            dc.actorFace.setSize(face.getRegionWidth() * Constants.FACE_SIZE, face.getRegionHeight() * Constants.FACE_SIZE);
            dc.actorFace.setColor(1, 1, 1, 1);
        }
        dc.faces.clearChildren();
        dc.faces.bottom().left().add(dc.playerFace).size(dc.playerFace.getPrefWidth() * Constants.FACE_SIZE, dc.playerFace.getPrefHeight() * Constants.FACE_SIZE).expand().bottom().left();
        dc.faces.add(dc.actorFace).size(dc.actorFace.getPrefWidth() * Constants.FACE_SIZE, dc.actorFace.getPrefHeight() * Constants.FACE_SIZE).expand().bottom().right();
        dc.messageLabel.setMessage((line.name != null ? "[GOLD]" + line.name + "[]\n" : "") + line.message);
        dc.messageLabel.setFontScale(line.name == null ? .5f * line.fontScale : .35f * line.fontScale);

        dc.message.clearChildren();
        dc.message.add(dc.messageLabel).expandX().fillX().left().padBottom(4).row();
        dc.timer = line.timer;
        if (line.options.length == 0) {
            if (line.timer == 0)
                dc.message.add(new Label("Click to continue...", Main.skin)).expand().center().bottom();
        } else {
            for (int i = 0; i < line.options.length; i++) {
                line.options[i].reset(entity, this);
                dc.message.add(line.options[i].label).left().padLeft(10).row();
            }
            dc.selected = line.options[0];
            updateSelected(entity);
        }
        if (line.timer == 0) {
            ActorComponent ac = Mappers.actor.get(entity);
            ac.actor.setTouchable(line.options.length == 0 ? Touchable.enabled : Touchable.childrenOnly);
            entity.remove(IgnoreComponent.class);
        } else entity.add(new IgnoreComponent());
    }

    public static void moveSelection(Entity entity, int amount) {
        DialogueComponent dc = Mappers.dialogue.get(entity);

        if (dc.selected == null) return;
        Line currLine = dc.lines.get(dc.line);
        for (int i = 0; i < currLine.options.length; i++) {
            if (dc.selected == currLine.options[i]) {
                if (i + amount < 0)
                    dc.selected = currLine.options[currLine.options.length - 1];
                else if (i + amount >= currLine.options.length)
                    dc.selected = currLine.options[0];
                else dc.selected = currLine.options[i + amount];
                break;
            }
        }
        updateSelected(entity);
    }

    public static void updateSelected(Entity entity) {
        DialogueComponent dc = Mappers.dialogue.get(entity);
        boolean inventory = Mappers.inventory.has(entity);

        for (int i = 0; i < dc.lines.get(dc.line).options.length; i++) {
            Option option = dc.lines.get(dc.line).options[i];
            if (dc.selected == option) {
                if (!inventory) option.label.setText(" > " + option.message);
                option.label.setColor(Color.ORANGE);
            } else {
                if (!inventory) option.label.setText("> " + option.message);
                option.label.setColor(Color.WHITE);
            }

            if (inventory) {
                boolean selected = false;
                for (Attack attack : Player.getAttacks()) {
                    if (attack.option == option) {
                        selected = true;
                        break;
                    }
                }
                option.label.setText(selected ? "> " + option.message + " <" : option.message);
            }
        }

        if (inventory) {
            InventoryComponent ic = Mappers.inventory.get(entity);

            Attack attack = null;
            for (Attack action : Player.getInventory()) {
                if (action.option == dc.selected) {
                    attack = action;
                    break;
                }
            }
            ic.descTable.setVisible(attack != null);
            if (attack != null) {
                ic.description.setText(attack.prototype.description);
            }
        }
    }
}
