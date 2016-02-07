package thepaperpilot.rpg.Battles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import thepaperpilot.rpg.Main;

public class Enemy extends Table {

    private final Attack.AttackPrototype[] attacks;
    private final Battle battle;
    private float health;
    private ProgressBar healthBar;

    public Enemy(EnemyPrototype prototype, Battle battle) {
        super(Main.skin);
        this.battle = battle;
        health = prototype.health;
        healthBar = new ProgressBar(0, health, .1f, false, Main.skin);
        healthBar.setAnimateDuration(.5f);
        healthBar.setValue(health);
        attacks = prototype.attacks;
        add(new Image(Main.getTexture(prototype.image))).size(32).spaceBottom(4).row();
        add(healthBar).width(health * 4);
    }

    public Attack getAttack() {
        return new Attack(attacks[MathUtils.random(attacks.length - 1)], battle);
    }

    public void hit(float damage) {
        health -= damage;
        if (health <= 0) {
            battle.enemies.remove(this);
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
            if (battle.enemies.isEmpty())
                battle.win();
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

    public static class EnemyPrototype {
        final String name;
        final String image;
        final float x;
        final float y;
        final float health;
        public Attack.AttackPrototype[] attacks = new Attack.AttackPrototype[]{};

        public EnemyPrototype(String name, String image, float x, float y, float health) {
            this.name = name;
            this.image = image;
            this.x = x;
            this.y = y;
            this.health = health;
        }
    }
}
