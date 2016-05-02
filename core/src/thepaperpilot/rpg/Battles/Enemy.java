package thepaperpilot.rpg.Battles;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import thepaperpilot.rpg.Components.DialogueComponent;
import thepaperpilot.rpg.Main;
import thepaperpilot.rpg.Screens.Battle;
import thepaperpilot.rpg.UI.Line;

public class Enemy extends Table {

    public final EnemyPrototype prototype;
    private final Battle battle;
    public float health;
    private final ProgressBar healthBar;
    private final Label leftSelect = new Label("> ", Main.skin);
    private final Label rightSelect = new Label(" <", Main.skin);
    private final Attack attack;
    private int spare = 0;

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
            die();
        }
        healthBar.setValue(health);
        battle.hitMarker(damage, getX(), getY() + 10);
    }

    private void die() {
        addAction(Actions.sequence(Actions.delay(.5f), Actions.run(new Runnable() {
            @Override
            public void run() {
                Main.manager.get("SFX/jingles_SAX05.ogg", Sound.class).play();
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

    public void spare(Engine engine) {
        DialogueComponent dc = new DialogueComponent();
        dc.start = "start";
        Line line = new Line(prototype.spares[spare]);
        dc.lines.put("start", line);
        if (spare == prototype.spares.length) {
            dc.events.put("end", new Runnable() {
                @Override
                public void run() {
                    die();
                }
            });
        }
        dc.small = true;
        dc.position = new Rectangle(getX() + 120, getY() + 10, 180, 30);
        spare++;
        Entity entity = new Entity();
        entity.add(dc);
        engine.addEntity(entity);
    }

    public static class EnemyPrototype {
        final String name;
        final String image;
        public final String title;
        final String[] spares;
        public final Vector2 position;
        final float health;
        private final Attack.AttackPrototype attack;

        public EnemyPrototype(String name, String image, String title, String[] spares, Vector2 position, float health, Attack.AttackPrototype attack) {
            this.name = name;
            this.image = image;
            this.title = title;
            this.spares = spares;
            this.position = position;
            this.health = health;
            this.attack = attack;
        }
    }
}
