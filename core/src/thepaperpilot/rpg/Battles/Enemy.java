package thepaperpilot.rpg.Battles;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import thepaperpilot.rpg.Main;

import java.util.HashMap;
import java.util.Map;

public class Enemy extends Table {

    public static Map<String, EnemyPrototype> prototypes = new HashMap<String, EnemyPrototype>();

    static {
        prototypes.put("joker", new Enemy.EnemyPrototype("joker", "joker", new Vector2(80, 240), 20) {
            @Override
            public Attack.AttackPrototype getAttack(Enemy enemy) {
                if (enemy.battle.turn % 2 == 0) {
                    return Attack.prototypes.get("portalSpawn");
                }
                return Attack.prototypes.get("dummy");
            }
        });
        prototypes.put("satan", new Enemy.EnemyPrototype("satan", "satan", new Vector2(320, 320), 100) {
            @Override
            public Attack.AttackPrototype getAttack(Enemy enemy) {
                return Attack.prototypes.get("satan");
            }
        });
        prototypes.put("portal", new EnemyPrototype("portal", "portal", new Vector2(0, 0), 5) {
            @Override
            public Attack.AttackPrototype getAttack(Enemy enemy) {
                return Attack.prototypes.get("portal");
            }
        });
    }

    private final EnemyPrototype prototype;
    public final Battle battle;
    private float health;
    private ProgressBar healthBar;
    private Label leftSelect = new Label("> ", Main.skin);
    private Label rightSelect = new Label(" <", Main.skin);

    public Enemy(EnemyPrototype prototype, final Battle battle) {
        super(Main.skin);
        this.prototype = prototype;
        this.battle = battle;
        health = prototype.health;
        healthBar = new ProgressBar(0, health, .1f, false, Main.skin);
        healthBar.setAnimateDuration(.5f);
        healthBar.setValue(health);
        Image image = new Image(Main.getTexture(prototype.image));
        setSelected(false);
        add(image).size(image.getPrefWidth() * 2, image.getPrefHeight() * 2).colspan(3).spaceBottom(4).row();
        add(leftSelect);
        add(healthBar).width(health * 4);
        add(rightSelect);

        addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                battle.setTarget(Enemy.this);
            }
        });
    }

    public void setSelected(boolean selected) {
        leftSelect.setVisible(selected);
        rightSelect.setVisible(selected);
    }

    public Attack getAttack() {
        return new Attack(prototype.getAttack(this), battle, new Vector2(getX(), getY()));
    }

    public void hit(float damage) {
        health -= damage;
        if (health <= 0) {
            addAction(Actions.sequence(Actions.delay(.5f), Actions.run(new Runnable() {
                @Override
                public void run() {
                    Main.manager.get("jingles_SAX05.ogg", Sound.class).play();
                }
            })));
            battle.enemies.remove(this);
            battle.updateEnemies();
            addAction(Actions.sequence(Actions.fadeOut(1), Actions.run(new Runnable() {
                @Override
                public void run() {
                    remove();
                }
            })));
            healthBar.addAction(Actions.sequence(Actions.fadeOut(1), Actions.run(new Runnable() {
                @Override
                public void run() {
                    remove();
                }
            })));
        }
        healthBar.setValue(health);
        final Label label = new Label("" + Math.abs(damage), Main.skin);
        label.setColor(damage < 0 ? Color.GREEN : Color.RED);
        label.setPosition(getX(), getY() + 10);
        label.addAction(Actions.sequence(Actions.parallel(Actions.fadeOut(1), Actions.moveBy(0, 10, 1)), Actions.run(new Runnable() {
            @Override
            public void run() {
                label.remove();
            }
        })));
        battle.stage.addActor(label);
    }

    public static abstract class EnemyPrototype {
        final String name;
        final String image;
        public final Vector2 position;
        final float health;

        public EnemyPrototype(String name, String image, Vector2 position, float health) {
            this.name = name;
            this.image = image;
            this.position = position;
            this.health = health;
        }

        public abstract Attack.AttackPrototype getAttack(Enemy enemy);
    }
}
