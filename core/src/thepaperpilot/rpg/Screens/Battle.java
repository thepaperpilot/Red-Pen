package thepaperpilot.rpg.Screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import thepaperpilot.rpg.Battles.Attack;
import thepaperpilot.rpg.Battles.Enemy;
import thepaperpilot.rpg.Chapters.GameOver;
import thepaperpilot.rpg.Components.DialogueComponent;
import thepaperpilot.rpg.Events.Event;
import thepaperpilot.rpg.Main;
import thepaperpilot.rpg.UI.Line;
import thepaperpilot.rpg.UI.Option;
import thepaperpilot.rpg.Util.Constants;
import thepaperpilot.rpg.Util.Mappers;
import thepaperpilot.rpg.Util.Player;

import java.util.ArrayList;

public class Battle extends Context implements InputProcessor {

    public final ArrayList<Enemy> enemies;
    public final Vector2 playerPos;
    private final ProgressBar health;
    private final Entity attackDialogue;
    private final BattlePrototype prototype;
    public final Area area;
    public final ArrayList<Attack.Word> words = new ArrayList<Attack.Word>();
    public ArrayList<Attack> attacks;
    public int turn = 0;
    public Attack.Word selected;
    public boolean attacking;
    public Enemy target;
    private float shake;

    public Battle(BattlePrototype prototype, Area area) {
        super(prototype);
        this.prototype = prototype;
        this.area = area;

        Image player = new Image(Main.portraits.findRegion("player"));
        health = new ProgressBar(0, Player.getMaxHealth(), .1f, false, Main.skin);
        health.setAnimateDuration(.5f);
        health.setValue(Player.getHealth());
        Table playerTable = new Table(Main.skin);
        playerTable.add(player).size(player.getPrefWidth() * Constants.FIGHTER_SIZE, player.getPrefHeight() * Constants.FIGHTER_SIZE).spaceBottom(4).row();
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

        DialogueComponent dc = new DialogueComponent();
        Line line = new Line("Choose an Action...");
        ArrayList<Option> options = new ArrayList<Option>();
        for (int i = 0; i < Player.getAttacks().size(); i++) {
            Option option = new Option(Player.getAttacks().get(i).prototype.name, false);
            option.event = "" + i;
            final int index = i;
            dc.events.put("" + i, new Runnable() {
                @Override
                public void run() {
                    Battle battle = (Battle.this);
                    battle.attack();
                    Attack attack = Player.getAttacks().get(index);
                    attack.init(battle, battle.playerPos);
                    battle.attacks.add(attack);
                }
            });
            options.add(option);
        }
        line.options = options.toArray(new Option[options.size()]);
        dc.lines.put("attack", line);
        dc.start = "attack";
        attackDialogue = new Entity();
        attackDialogue.add(dc);
        prototype.start(this);

        next();
    }

    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
        stage.addActor(enemy);

        if (attacking)
            attacks.add(enemy.getAttack());

        if (target == null) setTarget(enemy);
    }

    public void setInputProcessor() {
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, this));
    }

    public void next() {
        attacking = false;
        if (!engine.getEntities().contains(attackDialogue, true))
            engine.addEntity(attackDialogue);
        stage.setKeyboardFocus(Mappers.actor.get(attackDialogue).actor);
    }

    public void attack() {
        attacking = true;
        turn++;
        attacks = new ArrayList<Attack>();
        for (Enemy enemy : enemies) {
            attacks.add(enemy.getAttack());
        }
        prototype.update(this);
    }

    public void render(float delta) {
        if (shake > 0) {
            Vector3 position = stage.getCamera().position.cpy();
            stage.getCamera().position.set(position.x + MathUtils.random(-shake, shake), position.y + MathUtils.random(-shake, shake), 0);
            super.render(delta);
            stage.getCamera().position.set(position);
            shake *= .75f;
            if (shake < .1f)
                shake = 0;
        } else super.render(delta);

        if (attacking) {
            // I can't use a for each because some attacks might add new attacks
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0; i < attacks.size(); i++) {
                Attack attack = attacks.get(i);
                attack.update(delta);
            }
            for (int i = 0; i < attacks.size();) {
                Attack attack = attacks.get(i);
                if (attack.words.isEmpty() && attack.attacks >= attack.prototype.attacks) {
                    attacks.remove(attack);
                } else i++;
            }
            if (attacks.isEmpty()) {
                next();
            }
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

    private void win() {
        area.events.addAll(prototype.winEvents);
        exit();
    }

    private void lose() {
        attacking = false;
        for (Attack.Word word : words) {
            word.clearActions();
        }
        stage.addAction(Actions.sequence(Actions.delay(.5f), Actions.run(new Runnable() {
            @Override
            public void run() {
                Main.manager.get("SFX/jingles_SAX07.ogg", Sound.class).play();
            }
        }), Actions.delay(1f), Actions.fadeOut(1), Actions.run(new Runnable() {
            @Override
            public void run() {
                GameOver.gameOver(enemies.get(0).prototype.title);
            }
        })));
    }

    public void hit(float damage) {
        Player.addHealth(-damage);
        if (damage > 0) shake += damage * 10;
        if (Player.getHealth() <= 0) {
            lose();
        }
        health.setValue(Player.getHealth());
        hitMarker(damage, playerPos.x, playerPos.y + 10);
    }

    public void hitMarker(float damage, float x, float y) {
        final Label label = new Label("" + (int) Math.abs(damage), Main.skin, "large");
        label.setColor(damage < 0 ? Color.GREEN : Color.RED);
        label.setPosition(x, y);
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
        if (keycode == Input.Keys.TAB) {
            int index = enemies.indexOf(target) + 1;
            if (index == enemies.size()) index = 0;
            setTarget(enemies.get(index));
        }
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
                else if (selected.nextLetter() == ' ')
                    selected.letter++;
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
        public Enemy.EnemyPrototype[] enemies = new Enemy.EnemyPrototype[]{};
        public final ArrayList<Event> winEvents = new ArrayList<Event>();
        public Vector2 playerPosition = new Vector2(480, 180);

        public void start(Battle battle) {

        }

        public void update(Battle battle) {

        }

        public BattlePrototype(String name) {
            this.name = name;
        }
    }
}
