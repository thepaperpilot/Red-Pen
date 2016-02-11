package thepaperpilot.rpg.Battles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import thepaperpilot.rpg.*;
import thepaperpilot.rpg.Map.Area;

import java.util.ArrayList;

public class Battle extends Context implements InputProcessor {

    public final ArrayList<Enemy> enemies;
    public final Vector2 playerPos;
    public final ProgressBar health;
    private final Event[] winEvents;
    private final Event[] loseEvents;
    private final Dialogue attackDialogue;
    private final BattlePrototype prototype;
    public final Area area;
    ArrayList<Attack.Word> words = new ArrayList<Attack.Word>();
    ArrayList<Attack> attacks;
    public int turn = 0;
    protected Attack.Word selected;
    protected boolean attacking;
    public Enemy target;

    public Battle(BattlePrototype prototype, Area area) {
        super(prototype);
        this.prototype = prototype;
        this.area = area;
        dialogues = area.dialogues;

        Image player = new Image(Main.getTexture("player"));
        health = new ProgressBar(0, Player.getMaxHealth(), .1f, false, Main.skin);
        health.setAnimateDuration(.5f);
        health.setValue(Player.getHealth());
        Table playerTable = new Table(Main.skin);
        playerTable.add(player).size(32).spaceBottom(4).row();
        playerTable.add(health).width(Player.getMaxHealth() * 4);
        playerPos = prototype.playerPosition;
        playerTable.setPosition(playerPos.x + 8, playerPos.y);
        stage.addActor(playerTable);

        enemies = new ArrayList<Enemy>();
        for (int i = 0; i < prototype.enemies.length; i++) {
            Enemy enemy = new Enemy(prototype.enemies[i], this);
            enemy.setPosition(prototype.enemies[i].position.x + 8, prototype.enemies[i].position.y);
            addEnemy(enemy);
        }
        updateEnemies();

        winEvents = new Event[prototype.winEvents.length];
        for (int i = 0; i < prototype.winEvents.length; i++) {
            winEvents[i] = new Event(prototype.winEvents[i], area);
        }

        loseEvents = new Event[prototype.loseEvents.length];
        for (int i = 0; i < prototype.loseEvents.length; i++) {
            loseEvents[i] = new Event(prototype.loseEvents[i], area);
        }

        Dialogue.DialoguePrototype dialoguePrototype = new Dialogue.DialoguePrototype();
        Dialogue.LinePrototype linePrototype = new Dialogue.LinePrototype();
        linePrototype.message = "Choose an Action...";
        ArrayList<Dialogue.OptionPrototype> options = new ArrayList<Dialogue.OptionPrototype>();
        for (Attack.AttackPrototype attackPrototype : Player.getAttacks()) {
            Dialogue.OptionPrototype optionPrototype = new Dialogue.OptionPrototype();
            optionPrototype.events = new Event.EventPrototype[]{new Event.EventPrototype(Event.Type.SET_ATTACK, attackPrototype.name)};
            optionPrototype.message = attackPrototype.name;
            options.add(optionPrototype);
        }
        linePrototype.options = options.toArray(new Dialogue.OptionPrototype[options.size()]);
        dialoguePrototype.lines = new Dialogue.LinePrototype[]{linePrototype};
        attackDialogue = dialoguePrototype.getDialogue(this);
        prototype.start(this);

        next();
    }

    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
        stage.addActor(enemy);

        if (attacking)
            attacks.add(enemy.getAttack());

        setTarget(enemy);
    }

    public void show() {
        super.show();
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, this));
    }

    private void next() {
        turn++;
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
        prototype.update(this);
    }

    public void escape() {
        if (prototype.escapeable) {
            exit();
        } else lose();
    }

    public void render(float delta) {
        super.render(delta);

        if (attacking) {
            // I can't use a for each because some attacks might add new attacks
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0; i < attacks.size(); i++) {
                Attack attack = attacks.get(i);
                attack.update(delta);
            }
            for (int i = 0; i < attacks.size();) {
                Attack attack = attacks.get(i);
                if (attack.words.isEmpty() && attack.attacks <= 0) {
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
                attacks.add(new Attack(Player.getAttack(event.attributes.get("target")), this, playerPos));
                break;
            case RESUME_ATTACK:
                attacking = true;
                break;
            case NEXT_ATTACK:
                next();
                break;
            default:
                super.run(event);
                break;
        }
    }

    public void addWord(final Attack.Word newWord) {
        words.add(newWord);
        for (final Attack.Word word : words) {
            word.setPosition(word.start.x, word.start.y);
            word.addAction(new SequenceAction(Actions.moveTo(word.end.x, word.end.y, word.speed), Actions.run(new Runnable() {
                @Override
                public void run() {
                    if (!word.runOnComplete && Battle.this.words.contains(word)) {
                        word.attack.run(word);
                    }
                    word.removeWord();
                }
            })));
            stage.addActor(word);
        }
    }

    public void exit() {
        attacking = false;
        for (Attack.Word word : words) {
            word.clearActions();
        }
        stage.addAction(Actions.sequence(Actions.delay(2), Actions.fadeOut(1), Actions.run(new Runnable() {
            @Override
            public void run() {
                Main.changeScreen(area);
            }
        })));
    }

    public void win() {
        for (Event event : winEvents) {
            event.run();
        }
        exit();
    }

    private void lose() {
        stage.addAction(Actions.sequence(Actions.delay(.5f), Actions.run(new Runnable() {
            @Override
            public void run() {
                Main.manager.get("jingles_SAX07.ogg", Sound.class).play();
            }
        })));
        Player.setHealth(1);
        for (Event event : loseEvents) {
            event.run();
        }
        exit();
    }

    public void hit(float damage) {
        Player.addHealth(-damage);
        if (Player.getHealth() <= 0) {
            lose();
        }
        health.setValue(Player.getHealth());
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

    public void updateEnemies() {
        if (enemies.isEmpty())
            win();
        else setTarget(enemies.get(MathUtils.random(enemies.size() - 1)));
    }

    public void setTarget(Enemy target) {
        this.target = target;
        for (Enemy enemy : enemies) {
            enemy.setSelected(target == enemy);
        }
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
                Main.click();
                if (selected.update()) selected = null;
            }
        } else {
            for (Attack.Word word : words) {
                if (word.word.charAt(0) == character) {
                    selected = word;
                    selected.letter++;
                    Main.click();
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
        public final boolean escapeable;
        public Enemy.EnemyPrototype[] enemies = new Enemy.EnemyPrototype[]{};
        public Event.EventPrototype[] winEvents = new Event.EventPrototype[]{};
        public Event.EventPrototype[] loseEvents = new Event.EventPrototype[]{};
        public Vector2 playerPosition = new Vector2(480, 180);

        public void start(Battle battle) {

        }

        public void update(Battle battle) {

        }

        public BattlePrototype(String name, boolean escapable) {
            this.name = name;
            this.escapeable = escapable;
        }
    }
}
