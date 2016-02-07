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
import java.util.Collections;

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
        Word[] newWords = prototype.update(delta, this);
        Collections.addAll(words, newWords);
        battle.addWords(newWords);
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
            return new Word(bank[MathUtils.random(bank.length - 1)], target, color, speed, runOnComplete, attack);
        }

        // Also change "done" to true if the attack is over
        public abstract Word[] update(float delta, Attack attack);

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
