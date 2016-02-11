package thepaperpilot.rpg.Battles;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import thepaperpilot.rpg.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Attack {

    public static final Map<String, AttackPrototype> prototypes = new HashMap<String, AttackPrototype>();

    static {
        prototypes.put("stick", new Attack.AttackPrototype(new String[]{"attack", "poke", "stick", "sticky", "jab", "whack", "whump", "swish", "slash"}, "jingles_SAX16", "stick", Target.ENEMY, 2, Color.RED, 6, 2, 3, true) {
            @Override
            public void run(Vector2 position, Attack attack) {
                Attack.Word word = getWord(attack);
                word.start = new Vector2(attack.battle.playerPos.x + MathUtils.random(50) - 25, attack.battle.playerPos.y - MathUtils.random(50));
                word.end = word.start.cpy().add(0, 20);
                attack.addWord(word);
            }
        });
        prototypes.put("heal", new Attack.AttackPrototype(new String[]{"help", "heal", "magic", "power", "assist", "you matter"}, "jingles_SAX15", "heal", Target.PLAYER, -2, Color.GREEN, 9, 2, 3, true) {
            @Override
            public void run(Vector2 position, Attack attack) {
                Attack.Word word = getWord(attack);
                word.start = new Vector2(attack.battle.playerPos.x + MathUtils.random(50) - 25, attack.battle.playerPos.y - MathUtils.random(50));
                word.end = word.start.cpy().add(0, 10);
                attack.addWord(word);
            }
        });
        prototypes.put("run", new Attack.AttackPrototype(new String[]{"help!", "escape...", "run...", "away...", "run away..", "get away.."}, "jingles_SAX03", "run", Target.OTHER, 0, Color.TEAL, 20, 0, 1, true) {
            @Override
            public void run(Vector2 position, Attack attack) {
                Attack.Word word = getWord(attack);
                word.start = new Vector2(attack.battle.playerPos.x + MathUtils.random(50) - 25, attack.battle.playerPos.y - MathUtils.random(50));
                word.end = word.start.cpy().add(0, 10);
                attack.addWord(word);
            }

            public void run(Attack.Word word) {
                word.attack.battle.escape();
                super.run(word);
            }
        });

        prototypes.put("ball", new Attack.AttackPrototype(new String[]{"fun", "ball", "catch", "juggle", "joy", "happy", "play"}, "jingles_SAX16", "ball", Target.PLAYER, 1, Color.RED, 10, 2, 3, false) {
            @Override
            public void run(Vector2 position, Attack attack) {
                Attack.Word word = getWord(attack);
                word.start = new Vector2(position.x + MathUtils.random(50) - 25, position.y + MathUtils.random(50) - 25);
                word.end = new Vector2(attack.battle.playerPos.x, attack.battle.playerPos.y);
                attack.addWord(word);
            }
        });
        prototypes.put("satan", new Attack.AttackPrototype(new String[]{"hell", "satan", "death", "die", "sin", "death", "immoral", "evil", "despicable", "mean", "horrible", "rude", "afterlife", "dead", "never"}, "jingles_SAX16", "satan", Target.PLAYER, 1, Color.RED, 8, 6, 2, false) {
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
        });
        prototypes.put("portalSpawn", new AttackPrototype(new String[]{}, "jingles_SAX16", "portalSpawn", Target.OTHER, 0, Color.BLACK, 0, 0, 1, false) {
            @Override
            public void run(Vector2 position, Attack attack) {
                Enemy enemy = new Enemy(Enemy.prototypes.get("portal"), attack.battle);
                enemy.setPosition(position.x + MathUtils.random(50), position.y + MathUtils.randomSign() * MathUtils.random(75, 100));
                attack.battle.addEnemy(enemy);
            }
        });
        prototypes.put("portal", new AttackPrototype(new String[]{"portal", "magic", "speed", "fast", "swarm", "mystery"}, "jingles_SAX16", "portal", Target.PLAYER, 1, Color.GRAY, 10, 1, 5, false) {
            @Override
            public void run(Vector2 position, Attack attack) {
                Word word = getWord(attack);
                float y = position.y + MathUtils.random(-100, 100);
                word.start = new Vector2(position.x, y);
                word.end = new Vector2(attack.battle.playerPos.x, y);
                attack.addWord(word);
            }
        });
        prototypes.put("portalAbility", new AttackPrototype(new String[]{"portal", "magic", "speed", "fast", "swarm", "mystery"}, "jingles_SAX16", "portal", Target.PLAYER, 1, Color.GRAY, 10, 1.5f, 10, false) {
            @Override
            public void run(Vector2 position, Attack attack) {
                Word word = getWord(attack);
                float y = position.y + MathUtils.random(-100, 100);
                word.start = new Vector2(position.x, y);
                word.end = new Vector2(attack.battle.playerPos.x, y);
                attack.addWord(word);
            }
        });

        prototypes.put("dummy", new AttackPrototype(new String[]{}, "jingles_SAX16", "dummy", Target.OTHER, 0, Color.BLACK, 0, 0, 0, true) {
            @Override
            public void run(Vector2 position, Attack attack) {

            }
        });
    }

    private final AttackPrototype prototype;
    public final Battle battle;
    private final Vector2 position;
    protected ArrayList<Word> words = new ArrayList<Word>();
    public float timer;
    public float attacks;

    public Attack(AttackPrototype prototype, Battle battle, Vector2 position) {
        this.prototype = prototype;
        this.battle = battle;
        this.position = position;
        attacks = prototype.attacks;
    }

    public void update(float delta) {
        if (!battle.attacking) return;

        timer += delta;
        while (timer > prototype.spawnSpeed && attacks > 0) {
            timer -= prototype.spawnSpeed;
            attacks--;
            prototype.run(position, this);
        }
    }

    public void addWord(Word word) {
        words.add(word);
        battle.addWord(word);
    }

    public void run(Word word) {
        prototype.run(word);
    }

    public enum Target {
        PLAYER,
        OTHER,
        ENEMY
    }

    public static class Word extends Label {
        final String word;
        final Target target;
        final Color color;
        public Vector2 start;
        public Vector2 end;
        final float speed;
        public final Attack attack;
        int letter = 0;
        boolean runOnComplete = false;

        public Word(String word, Target target, Color color, float speed, boolean runOnComplete, Attack attack) {
            super(word, Main.skin);
            this.word = word;
            this.target = target;
            this.color = color;
            this.speed = speed;
            this.runOnComplete = runOnComplete;
            this.attack = attack;
        }

        public char nextLetter() {
            return word.charAt(letter);
        }

        public void removeWord() {
            attack.battle.words.remove(this);
            attack.words.remove(this);
            if (attack.battle.selected == this)
                attack.battle.selected = null;
            clearActions();
            addAction(new SequenceAction(Actions.fadeOut(1), Actions.run(new Runnable() {
                @Override
                public void run() {
                    remove();
                }
            })));
        }

        public boolean update() {
            setText("[#" + color.toString() + "]" + word.substring(0, letter) + "[]" + word.substring(letter));
            if (letter == word.toCharArray().length) {
                if (runOnComplete) {
                    attack.run(this);
                }
                removeWord();
                return true;
            }
            return false;
        }
    }

    public static abstract class AttackPrototype {
        String[] bank = new String[]{};
        public final String name;
        public final String sound;
        public final Target target;
        public final float damage;
        final Color color;
        final float speed;
        private final int attacks;
        private final float spawnSpeed;
        private final boolean runOnComplete;

        public AttackPrototype(String[] bank, String sound, String name, Target target, float damage, Color color, float speed, float spawnSpeed, int attacks, boolean runOnComplete) {
            this.bank = bank;
            this.sound = sound;
            this.name = name;
            this.target = target;
            this.damage = damage;
            this.color = color;
            this.speed = speed;
            this.attacks = attacks;
            this.spawnSpeed = spawnSpeed;
            this.runOnComplete = runOnComplete;
        }

        protected Word getWord(Attack attack) {
            String string = null;
            shuffleWords();
            for (String bankWord : bank) {
                for (Word word : attack.battle.words) {
                    if (word.word.charAt(0) == bankWord.charAt(0)) {
                        string = null;
                        break;
                    }
                    string = bankWord;
                }
                if (string != null) break;
            }
            if (string == null) string = bank[0];
            return new Word(string, target, color, speed, runOnComplete, attack);
        }

        void shuffleWords() {
            for (int i = bank.length - 1; i > 0; i--) {
                int index = MathUtils.random(i);
                String a = bank[index];
                bank[index] = bank[i];
                bank[i] = a;
            }
        }

        public void run(Word word) {
            if (target == Target.ENEMY) {
                word.attack.battle.target.hit(damage);
            } else if (target == Target.PLAYER) {
                word.attack.battle.hit(damage);
            }
            Main.manager.get(sound + ".ogg", Sound.class).play();
        }

        public abstract void run(Vector2 position, Attack attack);
    }
}
