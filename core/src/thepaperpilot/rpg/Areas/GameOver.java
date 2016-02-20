package thepaperpilot.rpg.Areas;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;
import thepaperpilot.rpg.Context;
import thepaperpilot.rpg.Event;
import thepaperpilot.rpg.Main;
import thepaperpilot.rpg.Player;
import thepaperpilot.rpg.UI.Dialogue;

public class GameOver extends Context.ContextPrototype {
    private static GameOver instance;
    private static String[] bank = new String[]{
            "They fought valiantly, but unfortunately [ENEMY] was just too strong",
            "Try as they might, [ENEMY] was just too much for them",
            "They failed to handle the pressure",
    };
    private String part1;
    private String part2;
    private String part3;

    public GameOver() {
        GameOver.instance = this;

        bgm = "Were all under the stars.mp3";
    }

    public void init(String enemy) {
        part1 = "and then the Player fought with " + enemy;
        part2 = bank[MathUtils.random(bank.length - 1)].replaceAll("(\\[ENEMY\\])", enemy);
        part3 = Player.getDeaths() == 0 ? "for the first time in their life, the Player died." : "the Player died for what seemed like the " + (Player.getDeaths() + 1) + " time.";

        final Event reset = new Event(Event.Type.DUMMY) {
            public void run(Context context) {
                Player.addDeath();
                Player.load();
            }
        };

        Dialogue.Line line = new Dialogue.Line("No. No. That's not how it ends. Let me just quickly revise that...");
        line.events = new Event[]{new Event(Event.Type.DUMMY) {
            public void run(final Context context) {
                context.stage.addAction(Actions.sequence(Actions.delay(.5f), Actions.fadeOut(2), Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        reset.run(context);
                    }
                })));
            }
        }};
        dialogues = new Dialogue[]{new Dialogue("death", new Dialogue.Line[]{line})};
    }

    public void loadAssets(AssetManager manager) {
        super.loadAssets(manager);
        manager.load("Were all under the stars.mp3", Sound.class);
    }

    public Context getContext() {
        final Context context = new Context(this);
        final Dialogue.ScrollText scroll = new Dialogue.ScrollText();
        scroll.setWrap(true);
        scroll.setSize(400, 200);
        scroll.setPosition(320, 270, Align.center);
        scroll.setAlignment(Align.topLeft);
        context.stage.addActor(scroll);
        context.stage.addAction(Actions.sequence(Actions.delay(1), Actions.run(new Runnable() {
            @Override
            public void run() {
                scroll.message = "\n > " + part1 + "\n\n > ";
            }
        }), Actions.delay(4), Actions.run(new Runnable() {
            @Override
            public void run() {
                scroll.message = "\n > " + part1 + "\n\n > " + part2 + "\n\n > ";
            }
        }), Actions.delay(6), Actions.run(new Runnable() {
            @Override
            public void run() {
                scroll.message = "\n > " + part1 + "\n\n > " + part2 + "\n\n > " + part3 + "\n\n > ";
            }
        }), Actions.delay(4), Actions.run(new Runnable() {
            @Override
            public void run() {
                new Event(Event.Type.DIALOGUE, "death").run(context);
            }
        })));
        return context;
    }

    public static void gameOver(String enemy) {
        instance.init(enemy);
        Main.changeContext("gameover");
    }
}
