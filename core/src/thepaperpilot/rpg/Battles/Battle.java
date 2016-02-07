package thepaperpilot.rpg.Battles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import thepaperpilot.rpg.Context;
import thepaperpilot.rpg.Dialogue;
import thepaperpilot.rpg.Event;
import thepaperpilot.rpg.Main;
import thepaperpilot.rpg.Map.Area;

import java.util.ArrayList;
import java.util.Collections;

public class Battle extends Context implements InputProcessor {

    public final ArrayList<Enemy> enemies;
    public final Vector2 playerPos;
    public final ProgressBar health;
    private final Event[] events;
    private final Dialogue attackDialogue;
    public final Area area;
    ArrayList<Attack.Word> words = new ArrayList<Attack.Word>();
    ArrayList<Attack> attacks;
    protected Attack.Word selected;
    private boolean attacking;

    public Battle(BattlePrototype prototype, Area area) {
        super(prototype);
        this.area = area;
        dialogues = area.dialogues;

        Image player = new Image(Main.getTexture(Main.PLAYER_TEXTURE));
        health = new ProgressBar(0, area.health, .1f, false, Main.skin);
        health.setValue(area.health);
        health.setAnimateDuration(.5f);
        Table playerTable = new Table(Main.skin);
        playerTable.add(player).size(32).spaceBottom(4).row();
        playerTable.add(health).width(area.health * 4);
        playerPos = new Vector2(3 * stage.getWidth() / 4, stage.getHeight() / 2);
        playerTable.setPosition(playerPos.x, playerPos.y);
        stage.addActor(playerTable);

        enemies = new ArrayList<Enemy>();
        for (int i = 0; i < prototype.enemies.length; i++) {
            Enemy enemy = new Enemy(prototype.enemies[i], this);
            enemy.setPosition(prototype.enemies[i].x, prototype.enemies[i].y);
            enemies.add(enemy);
            stage.addActor(enemy);
        }

        events = new Event[prototype.winEvents.length];
        for (int i = 0; i < prototype.winEvents.length; i++) {
            events[i] = new Event(prototype.winEvents[i], area);
        }

        Dialogue.DialoguePrototype dialoguePrototype = new Dialogue.DialoguePrototype();
        Dialogue.LinePrototype linePrototype = new Dialogue.LinePrototype();
        linePrototype.message = "Choose an Action...";
        ArrayList<Dialogue.OptionPrototype> options = new ArrayList<Dialogue.OptionPrototype>();
        for (Attack.AttackPrototype attackPrototype : area.attacks.values()) {
            Dialogue.OptionPrototype optionPrototype = new Dialogue.OptionPrototype();
            Event.EventPrototype eventPrototype = new Event.EventPrototype();
            eventPrototype.type = "SET_ATTACK";
            eventPrototype.attributes.put("target", attackPrototype.name);
            optionPrototype.events = new Event.EventPrototype[]{eventPrototype};
            optionPrototype.message = attackPrototype.name;
            options.add(optionPrototype);
        }
        linePrototype.options = options.toArray(new Dialogue.OptionPrototype[options.size()]);
        dialoguePrototype.lines = new Dialogue.LinePrototype[]{linePrototype};
        attackDialogue = new Dialogue(dialoguePrototype, this);

        next();
    }

    public void show() {
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, this));
    }

    private void next() {
        attacking = false;
        stage.addActor(attackDialogue);
        stage.setKeyboardFocus(attackDialogue);
    }

    private void attack() {
        attacking = true;
        attacks = new ArrayList<Attack>();
        for (Enemy enemy : enemies) {
            attacks.add(enemy.getAttack());
        }
    }

    public void render(float delta) {
        super.render(delta);

        if (attacking) {
            for (Attack attack : attacks) {
                attack.update(delta);
            }
            for (int i = 0; i < attacks.size();) {
                Attack attack = attacks.get(i);
                if (attack.words.isEmpty() && attack.done) {
                    attacks.remove(attack);
                } else i++;
            }
            if (attacks.isEmpty()) {
                next();
            }
        }
    }

    public void run(Event event) {
        switch (event.type) {
            case SET_ATTACK:
                attack();
                attacks.add(new Attack(area.attacks.get(event.attributes.get("target")), this));
                break;
            default:
                super.run(event);
                break;
        }
    }

    public void addWords(final Attack.Word[] words) {
        Collections.addAll(this.words, words);
        for (final Attack.Word word : words) {
            word.setPosition(word.start.x, word.start.y);
            word.addAction(new SequenceAction(Actions.moveTo(word.end.x, word.end.y, word.speed), Actions.run(new Runnable() {
                @Override
                public void run() {
                    if (!word.runOnComplete) {
                        word.attack.run(word);
                    }
                    Battle.this.words.remove(word);
                    word.attack.words.remove(word);
                    if (selected == word)
                        selected = null;
                }
            }), Actions.fadeOut(1), Actions.run(new Runnable() {
                @Override
                public void run() {
                    word.remove();
                }
            })));
            stage.addActor(word);
        }
    }

    public void exit() {
        stage.addAction(Actions.sequence(Actions.fadeOut(1), Actions.run(new Runnable() {
            @Override
            public void run() {
                Main.changeScreen(area);
            }
        })));
    }

    public void win() {
        for (Event event : events) {
            event.run();
        }
        exit();
    }

    private void lose() {
        area.health = 10;
        exit();
    }

    public void hit(float damage) {
        area.health -= damage;
        if (area.health <= 0) {
            lose();
        }
        final Label label = new Label("" + Math.abs(damage), Main.skin);
        label.setColor(damage < 0 ? Color.GREEN : Color.RED);
        label.setPosition(playerPos.x, playerPos.y + 10);
        label.addAction(Actions.sequence(Actions.parallel(Actions.fadeOut(1), Actions.moveBy(0, 10, 1)), Actions.run(new Runnable() {
            @Override
            public void run() {
                label.remove();
            }
        })));
        stage.addActor(label);
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        if (!attacking) return false;
        if (selected != null) {
            if (character == selected.nextLetter()) {
                selected.letter++;
                if (selected.update()) selected = null;
            }
        } else {
            for (Attack.Word word : words) {
                if (word.word.charAt(0) == character) {
                    selected = word;
                    selected.letter++;
                    if (selected.update()) selected = null;
                    break;
                }
            }
        }
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    public static class BattlePrototype extends ContextPrototype {
        public final String name;
        public Enemy.EnemyPrototype[] enemies = new Enemy.EnemyPrototype[]{};
        public Event.EventPrototype[] winEvents = new Event.EventPrototype[]{};

        public BattlePrototype(String name) {
            this.name = name;
        }
    }
}
