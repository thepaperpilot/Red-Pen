package thepaperpilot.rpg.Chapters;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;
import thepaperpilot.rpg.Components.DialogueComponent;
import thepaperpilot.rpg.Events.Event;
import thepaperpilot.rpg.Main;
import thepaperpilot.rpg.Screens.Context;
import thepaperpilot.rpg.UI.ScrollText;
import thepaperpilot.rpg.Util.Player;

public class GameOver extends Context.ContextPrototype {
    private static GameOver instance;
    private static final String[] bank = new String[]{
            "They fought valiantly, but unfortunately [ENEMY] was just too strong",
            "Try as they might, [ENEMY] was just too much for them",
            "They failed to handle the pressure",
            "Maybe if they could just get good, they could've beaten [ENEMY]",
            "But evidently enough even just [ENEMY] was able to stop their quest in its tracks"
    };
    private String part1;
    private String part2;
    private String part3;

    public GameOver() {
        GameOver.instance = this;

        bgm = "Were all under the stars.mp3";
    }

    private void init(String enemy) {
        part1 = "and then the Player fought with " + enemy;
        part2 = bank[MathUtils.random(bank.length - 1)].replaceAll("(\\[ENEMY\\])", enemy);
        part3 = Player.getDeaths() == 0 ? "for the first time in their life, the Player died" : "the Player died for what seemed like the " + (Player.getDeaths() + 1) + " time";
    }

    public Context getContext() {
        final Context context = new Context(this);
        context.init();
        final ScrollText scroll = new ScrollText(false);
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
                Entity entity = new Entity();
                DialogueComponent dc = DialogueComponent.read("death");
                dc.events.put("end", new Runnable() {
                    @Override
                    public void run() {
                        context.stage.addAction(Actions.sequence(Actions.delay(.5f), Actions.fadeOut(2), Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                context.events.add(new Event() {
                                    @Override
                                    public void run(Context context) {
                                        Player.addDeath();
                                        Player.load();
                                    }
                                });
                            }
                        })));
                    }
                });
                entity.add(dc);
                context.engine.addEntity(entity);
            }
        })));
        return context;
    }

    public static void gameOver(String enemy) {
        instance.init(enemy);
        Main.changeContext("gameover");
    }
}
