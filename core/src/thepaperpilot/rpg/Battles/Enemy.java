package thepaperpilot.rpg.Battles;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
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
        Attack.AttackPrototype portalSpawn = new Attack.AttackPrototype(new String[]{},
                "jingles_SAX16", "portalSpawn", Attack.Target.OTHER, 0, Color.BLACK, 0, 0, 1, false) {
            @Override
            public void run(Vector2 position, Attack attack) {
                Enemy enemy = new Enemy(Enemy.prototypes.get("portal"), attack.battle);
                enemy.setPosition(position.x + MathUtils.random(50), position.y + MathUtils.randomSign() * MathUtils.random(75, 100));
                attack.battle.addEnemy(enemy);
            }
        };
        prototypes.put("joker", new Enemy.EnemyPrototype("joker", "joker", new Vector2(80, 240), 20, portalSpawn) {
            @Override
            public Attack.AttackPrototype getAttack(Enemy enemy) {
                if (enemy.battle.turn % 2 == 0) {
                    return super.getAttack(enemy);
                }
                return Attack.prototypes.get("dummy");
            }
        });

        Attack.AttackPrototype satan = new Attack.AttackPrototype(
                new String[]{"hell", "satan", "death", "die", "sin", "death", "immoral", "evil", "despicable", "mean", "horrible", "rude", "afterlife", "dead", "never"},
                "jingles_SAX16", "satan", Attack.Target.PLAYER, 1, Color.RED, 8, 6, 2, false) {
            @Override
            public void run(Vector2 position, Attack attack) {
                for (int i = 0; i < 2; i++) {
                    for (int j = 0; j < 2; j++) {
                        Attack.Word word = getWord(attack);
                        word.start = new Vector2(attack.battle.playerPos.x - 80 + 160 * i, attack.battle.playerPos.y - 80 + 160 * j);
                        word.end = attack.battle.playerPos.cpy();
                        attack.addWord(word);
                    }
                }
            }
        };
        prototypes.put("satan", new Enemy.EnemyPrototype("satan", "satan", new Vector2(320, 320), 100, satan));

        Attack.AttackPrototype portal = new Attack.AttackPrototype(
                new String[]{"portal", "magic", "speed", "fast", "swarm", "mystery"},
                "jingles_SAX16", "portal", Attack.Target.PLAYER, 1, Color.YELLOW, 10, 1, 5, false) {
            @Override
            public void run(Vector2 position, Attack attack) {
                Attack.Word word = getWord(attack);
                float y = position.y + MathUtils.random(-50, 50);
                word.start = new Vector2(position.x, y);
                word.end = new Vector2(attack.battle.playerPos.x, y);
                attack.addWord(word);
            }
        };
        prototypes.put("portal", new EnemyPrototype("portal", "portal", new Vector2(0, 0), 5, portal));

        Attack.AttackPrototype portalAbility = new Attack.AttackPrototype(
                new String[]{"portal", "magic", "speed", "fast", "swarm", "mystery"},
                "jingles_SAX16", "portal", Attack.Target.PLAYER, 1, Color.YELLOW, 10, 1.5f, 10, false) {
            @Override
            public void run(Vector2 position, Attack attack) {
                Attack.Word word = getWord(attack);
                float y = position.y + MathUtils.random(-50, 50);
                word.start = new Vector2(position.x, y);
                word.end = new Vector2(attack.battle.playerPos.x, y);
                attack.addWord(word);
            }
        };
        prototypes.put("portalAbility", new EnemyPrototype("portal", "portal", new Vector2(80, 180), 20, portalAbility));

        Attack.AttackPrototype nm = new Attack.AttackPrototype(
                new String[]{"n", "m"},
                "jingles_SAX16", "nm", Attack.Target.PLAYER, 1, Color.CORAL, 2, .2f, 20, false) {
            @Override
            public void run(Vector2 position, Attack attack) {
                Attack.Word word = getWord(attack);
                word.start = new Vector2().setAngle(MathUtils.random(360));
                word.end = attack.battle.playerPos;
                attack.addWord(word);
            }
        };
        prototypes.put("nm", new EnemyPrototype("nm", "talker", new Vector2(80, 180), 20, nm));
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

    public static class EnemyPrototype {
        final String name;
        final String image;
        public final Vector2 position;
        final float health;
        private final Attack.AttackPrototype attack;

        public EnemyPrototype(String name, String image, Vector2 position, float health, Attack.AttackPrototype attack) {
            this.name = name;
            this.image = image;
            this.position = position;
            this.health = health;
            this.attack = attack;
        }

        public Attack.AttackPrototype getAttack(Enemy enemy) {
            return attack;
        }
    }
}
