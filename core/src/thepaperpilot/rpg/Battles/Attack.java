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
import java.util.Arrays;

public class Attack {

    private final AttackPrototype prototype;
    public final Battle battle;
    protected ArrayList<Word> words = new ArrayList<Word>();
    public boolean done = false;

    public Attack(AttackPrototype prototype, Battle battle) {
        this.prototype = prototype;
        this.battle = battle;
        prototype.reset();
    }

    public void update(float delta) {
        if (!battle.attacking) return;
        if (!done) done = prototype.update(delta, this);
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
        public final String name;
        public String sound;
        public final Target target;
        public final float damage;
        String[] bank = new String[]{};
        final Color color;
        final float speed;
        private boolean runOnComplete;

        public AttackPrototype(String[] bank, String sound, String name, Target target, float damage, Color color, float speed, boolean runOnComplete) {
            this.bank = bank;
            this.sound = sound;
            this.name = name;
            this.target = target;
            this.damage = damage;
            this.color = color;
            this.speed = speed;
            this.runOnComplete = runOnComplete;
        }

        protected Word getWord(Attack attack) {
            String string = null;
            shuffleWords();
            for (String bankWord : bank) {
                for (Word word : attack.words) {
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

        // return true if the attack is over
        public abstract boolean update(float delta, Attack attack);

        public void run(Word word) {
            if (target == Target.ENEMY) {
                word.attack.battle.enemies.get(0).hit(damage);
            } else if (target == Target.PLAYER) {
                word.attack.battle.hit(damage);
            }
            Main.manager.get(sound + ".ogg", Sound.class).play();
        }

        public void reset() {

        }
    }
}
