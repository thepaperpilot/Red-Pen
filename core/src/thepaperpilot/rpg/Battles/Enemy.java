package thepaperpilot.rpg.Battles;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import thepaperpilot.rpg.Main;

public class Enemy extends Table {

    public final EnemyPrototype prototype;
    public final Battle battle;
    public float health;
    private ProgressBar healthBar;
    private Label leftSelect = new Label("> ", Main.skin);
    private Label rightSelect = new Label(" <", Main.skin);
    private final Attack attack;

    public Enemy(EnemyPrototype prototype, final Battle battle) {
        super(Main.skin);
        this.prototype = prototype;
        this.battle = battle;
        attack = new Attack(prototype.attack);
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
        attack.init(battle, new Vector2(getX(), getY()));
        return attack;
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
        battle.hitMarker(damage, getX(), getY() + 10);
    }

    public static class EnemyPrototype {
        final String name;
        final String image;
        public String title;
        public final Vector2 position;
        final float health;
        private final Attack.AttackPrototype attack;

        public EnemyPrototype(String name, String image, String title, Vector2 position, float health, Attack.AttackPrototype attack) {
            this.name = name;
            this.image = image;
            this.title = title;
            this.position = position;
            this.health = health;
            this.attack = attack;
        }
    }
}
