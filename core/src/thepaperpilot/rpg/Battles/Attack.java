package thepaperpilot.rpg.Battles;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import thepaperpilot.rpg.Event;
import thepaperpilot.rpg.Main;
import thepaperpilot.rpg.Player;
import thepaperpilot.rpg.UI.Dialogue;
import thepaperpilot.rpg.UI.Menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Attack {

    public static final Map<String, AttackPrototype> prototypes = new HashMap<String, AttackPrototype>();

    static {
        prototypes.put("pencil", new Attack.AttackPrototype(new String[]{"word", "draw", "sketch", "talk", "write", "jab", "stab", "dictate", "words"}, "jingles_SAX16", "pencil", Target.ENEMY, 2, Color.BROWN, 6, 2, 2, true, "It's the pencil you were using before Satan showed up. 4 ATK") {
            @Override
            public void run(Vector2 position, Attack attack) {
                Attack.Word word = getWord(attack);
                word.start = new Vector2(attack.battle.playerPos.x + MathUtils.random(50) - 25, attack.battle.playerPos.y - MathUtils.random(25, 75));
                word.end = word.start.cpy().add(0, 20);
                attack.addWord(word);
            }
        });
        prototypes.put("heal", new Attack.AttackPrototype(new String[]{"help", "heal", "magic", "power", "assist", "you matter"}, "jingles_SAX15", "heal", Target.PLAYER, -2, Color.GREEN, 9, 2, 3, true, "It's a spell? Maybe a first aid kit? It heals you up to 6 HEALTH") {
            @Override
            public void run(Vector2 position, Attack attack) {
                Attack.Word word = getWord(attack);
                word.start = new Vector2(attack.battle.playerPos.x + MathUtils.random(50) - 25, attack.battle.playerPos.y - MathUtils.random(25, 75));
                word.end = word.start.cpy().add(0, 10);
                attack.addWord(word);
            }
        });
        prototypes.put("run", new Attack.AttackPrototype(new String[]{"help!", "escape...", "run...", "away...", "run away..", "get away.."}, "jingles_SAX03", "run", Target.OTHER, 0, Color.TEAL, 20, 0, 1, true, "run away, like a coward.") {
            @Override
            public void run(Vector2 position, Attack attack) {
                Attack.Word word = getWord(attack);
                word.start = new Vector2(attack.battle.playerPos.x + MathUtils.random(-25, 25), attack.battle.playerPos.y - MathUtils.random(25, 75));
                word.end = word.start.cpy().add(0, 10);
                attack.addWord(word);
            }

            public void run(Attack.Word word) {
                word.attack.battle.escape();
                super.run(word);
            }
        });

        prototypes.put("nmScroll", new Attack.ScrollPrototype(new String[]{"patet", "castus", "absterget"}, "jingles_SAX16", "nmScroll", Target.ENEMY, 0, Color.BLUE, 10, 2, 3, "It's a scroll you found. It reads 'HOW TO SPELL AWAY THE NMNMNMs'") {
            @Override
            public void run(Vector2 position, Attack attack) {
                Attack.Word patet = getWord(attack, 0);
                patet.start = attack.battle.playerPos.cpy().add(new Vector2(1, 0).setAngle(0).nor().scl(80));
                attack.addWord(patet);

                Attack.Word mundi = getWord(attack, 1);
                mundi.start = attack.battle.playerPos.cpy().add(new Vector2(1, 0).setAngle(120).nor().scl(80));
                attack.addWord(mundi);

                Attack.Word absterget = getWord(attack, 2);
                absterget.start = attack.battle.playerPos.cpy().add(new Vector2(1, 0).setAngle(240).nor().scl(80));
                attack.addWord(absterget);
            }

            public void run(Attack attack) {
                super.run(attack);
                if (attack.battle.enemies.get(0).prototype.name.equals("nm")) {
                    new Event(Event.Type.DIALOGUE, "nmScroll").run(attack.battle.area);
                    attack.battle.exit();
                }
            }
        });

        prototypes.put("stick", new Attack.AttackPrototype(new String[]{"attack", "poke", "stick", "sticky", "jab", "whack", "whump", "swish", "slash"}, "jingles_SAX16", "stick", Target.ENEMY, 3, Color.BROWN, 6, 2, 3, true, "It's a stick. You probably found it on the ground somewhere. 9 ATK") {
            @Override
            public void run(Vector2 position, Attack attack) {
                Attack.Word word = getWord(attack);
                word.start = new Vector2(attack.battle.playerPos.x + MathUtils.random(50) - 25, attack.battle.playerPos.y - MathUtils.random(25, 75));
                word.end = word.start.cpy().add(0, 20);
                attack.addWord(word);
            }
        });

        prototypes.put("dummy", new AttackPrototype(new String[]{}, "jingles_SAX16", "dummy", Target.OTHER, 0, Color.BLACK, 0, 0, 0, true) {
            @Override
            public void run(Vector2 position, Attack attack) {

            }
        });
    }

    public final AttackPrototype prototype;
    public Battle battle;
    private Vector2 position;
    protected ArrayList<Word> words = new ArrayList<Word>();
    public float timer;
    public int attacks;
    public Dialogue.Option option;

    public Attack(AttackPrototype prototype) {
        this.prototype = prototype;

        option = new Dialogue.Option(prototype.name, new Event[]{}) {
            public void select(Dialogue dialogue) {
                if (Player.getAttacks().contains(Attack.this)) {
                    Player.removeAttack(Attack.this);
                } else {
                    if (Player.getAttacks().size() < 5){
                        Player.addAttack(Attack.this);
                    } else {
                        Menu.error.setColor(1, 1, 1, 1);
                        Menu.error.clearActions();
                        Menu.error.addAction(Actions.fadeOut(2));
                    }
                }
                dialogue.updateSelected();
            }
        };
    }

    public void init(Battle battle, Vector2 position) {
        this.battle = battle;
        this.position = position;
        timer = 0;
        attacks = 0;
    }

    public void update(float delta) {
        if (!battle.attacking) return;

        timer += delta;
        while (timer > prototype.spawnSpeed && attacks < prototype.attacks) {
            timer -= prototype.spawnSpeed;
            attacks++;
            prototype.run(position, this);
        }
    }

    public void addWord(final Word word) {
        words.add(word);
        battle.words.add(word);

        word.setPosition(word.start.x, word.start.y);
        battle.stage.addActor(word);
        word.attack.prototype.addAnimation(word, battle);
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
        String word;
        final Target target;
        final Color color;
        final Color opposite;
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
            opposite = new Color(1 - color.r, 1 - color.g, 1 - color.b, 1);
            this.speed = speed;
            this.runOnComplete = runOnComplete;
            this.attack = attack;
            update();
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
            setText("[#" + opposite.toString() + "]" + word.substring(0, letter) + "[#" + color.toString() + "]" + word.substring(letter));
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
        public final int attacks;
        private final float spawnSpeed;
        private final boolean runOnComplete;
        public String description;

        // this is getting excessive. (too many parameters)
        public AttackPrototype(String[] bank, String sound, String name, Target target, float damage, Color color, float speed, float spawnSpeed, int attacks, boolean runOnComplete, String description) {
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
            this.description = description;
        }

        public AttackPrototype(String[] bank, String sound, String name, Target target, float damage, Color color, float speed, float spawnSpeed, int attacks, boolean runOnComplete) {
            this(bank, sound, name, target, damage, color, speed, spawnSpeed, attacks, runOnComplete, "");
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

        public void addAnimation(final Word word, final Battle battle) {
            word.addAction(new SequenceAction(Actions.moveTo(word.end.x, word.end.y, speed), Actions.run(new Runnable() {
                @Override
                public void run() {
                    if (!runOnComplete && battle.words.contains(word)) {
                        word.attack.run(word);
                    }
                    word.removeWord();
                }
            })));
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

    public abstract static class ScrollPrototype extends AttackPrototype{
        int maxAttacks;
        int attacks;

        public ScrollPrototype(String[] bank, String sound, String name, Target target, float damage, Color color, float speed, float spawnSpeed, int attacks, String description) {
            super(bank, sound, name, target, damage, color, speed, spawnSpeed, 1, true, description);
            this.attacks = maxAttacks = attacks;
        }

        public void addAnimation(final Word word, final Battle battle) {
            word.addAction(Actions.sequence(Actions.delay(speed), Actions.run(new Runnable() {
                @Override
                public void run() {
                    word.removeWord();
                }
            })));
        }

        protected Word getWord(Attack attack, int i) {
            if (i == 0) attacks = maxAttacks;
            return new Word(bank[i], target, color, speed, true, attack) {
                public void act(float delta) {
                    if (!attack.battle.attacking) return;
                    Vector2 velocity = attack.battle.playerPos.cpy().sub(getX(), getY()).rotate90(1).nor().scl(delta * 30);
                    setPosition(getX() + velocity.x, getY() + velocity.y);
                }
            };
        }

        final public void run(Word word) {
            attacks--;
            if (attacks == 0) {
                run(word.attack);
            }
        }

        protected void run(Attack attack) {
            Main.manager.get(sound + ".ogg", Sound.class).play();
            while (attack.words.size() > 0) {
                attack.words.get(0).removeWord();
            }
        }
    }
}
