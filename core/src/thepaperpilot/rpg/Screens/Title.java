package thepaperpilot.rpg.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import thepaperpilot.rpg.Main;
import thepaperpilot.rpg.Util.Player;

import java.util.regex.Pattern;

public class Title implements Screen{
    private final Stage stage;

    private Option selected;
    private final Option[] options;
    private final Table optionsTable;

    public Title() {
        stage = new Stage(new ExtendViewport(315, 250));

        Label book = new Label("     \"We'll visit my training school,\" smiled the general. \"It's in " +
                "the cellar. I have about a dozen pupils down there now. They're " +
                "from the Spanish bark San Lucar that had the bad luck to go " +
                "on the rocks out there. A very inferior lot, I regret to say. Poor " +
                "specimens and more accustomed to the deck than to the " +
                "jungle.\" He raised his hand, and Ivan, who served as waiter, " +
                "brought thick Turkish coffee. Rainsford, with an effort, held his " +
                "tongue in check.\n" +
                "     \"It's a game, you see,\" pursued the general blandly. \"I suggest" +
                " to one of them that we go hunting. I give him a supply of " +
                "food and an excellent hunting knife. I give him three hours' " +
                "start. I am to follow, armed only with a pistol of the smallest " +
                "caliber and range. If my quarry eludes me for three whole " +
                "days, he wins the game. If I find him\" the general smiled \"he " +
                "loses.\"\n" +
                "     \"Suppose he refuses to be hunted?\"\n" +
                "     \"Oh,\" said the general, \"I give him his option, of course. He " +
                "need not play that game if he doesn't wish to. If he does not " +
                "wish to hunt, I turn him over to Ivan. Ivan once had the honor " +
                "of serving as official knouter to the Great White Czar, and he " +
                "has his own ideas of sport. Invariably, Mr. Rainsford, invariably " +
                "they choose the hunt.\"\n" +
                "     \"And if they win?\"\n" +
                "     The smile on the general's face widened. \"To date I have not " +
                "lost,\" he said. Then he added, hastily: \"I don't wish you to think " +
                "me a braggart, Mr. Rainsford. Many of them afford only the " +
                "most elementary sort of problem. Occasionally I strike a tartar. " +
                "One almost did win. I eventually had to use the dogs.\"\n" +
                "     \"The dogs?\"\n" +
                "     \"This way, please. I'll show you.\"\n" +
                "The general steered Rainsford to a window. The lights from " +
                "the windows sent a flickering illumination that made grotesque " +
                "patterns on the courtyard below, and Rainsford could see moving " +
                "about there a dozen or so huge black shapes; as they " +
                "turned toward him, their eyes glittered greenly. " +
                "     \"A rather good lot, I think,\" observed the general. \"They are " +
                "let out at seven every night. If anyone should try to get into my " +
                "house—or out of it—something extremely regrettable would occur " +
                "to him.\" He hummed a snatch of song from the Folies " +
                "Bergere.\n" +
                "     \"And now,\" said the general, \"I want to show you my new collection " +
                "of heads. Will you come with me to the library?\"\n" +
                "     \"I hope,\" said Rainsford, \"that you will excuse me tonight, " +
                "General Zaroff. I'm really not feeling well.\"\n" +
                "     \"Ah, indeed?\" the general inquired solicitously. \"Well, I suppose " +
                "that's only natural, after your long swim. You need a " +
                "good, restful night's sleep. Tomorrow you'll feel like a new " +
                "man, I'll wager. Then we'll hunt, eh? I've one rather promising " +
                "prospect—\" Rainsford was hurrying from the room.", Main.skin) {
            @Override
            public void act(float delta) {
                super.act(delta);
                if (MathUtils.randomBoolean(.05f)) {
                    if (MathUtils.randomBoolean(.75f)) {
                        String text = getText().toString();
                        int firstIndex = text.indexOf(" ", MathUtils.random(getText().length - 1));
                        if (firstIndex == -1) return;
                        text = text.substring(0, firstIndex) + "[#ff0000]" + text.substring(firstIndex);
                        int secondIndex = text.indexOf(" ", firstIndex + 10);
                        if (secondIndex != -1) {
                            text = text.substring(0, secondIndex) + "[]" + text.substring(secondIndex);
                        }
                        setText(text);
                    } else if (getText().indexOf("[#ff0000]") != -1) {
                        String[] matches = getText().toString().split(Pattern.quote("[#ff0000]"));
                        String text = "";
                        int index = MathUtils.random(matches.length - 1);
                        for (int i = 0; i < index; i++) {
                            text += matches[i] + "[#ff0000]";
                        }
                        text += matches[index];
                        for (int i = index + 1; i < matches.length; i++) {
                            text += matches[i] + "[#ff0000]";
                        }
                        setText(text);
                    }
                }
            }
        };
        book.setWrap(true);
        book.setColor(1, 1, 1, .2f);

        Label title = new Label("Red Pen", Main.skin, "large");
        title.setWrap(true);
        title.setFontScale(1.5f);
        title.setColor(Color.RED);
        title.setAlignment(Align.center);

        Table label = new Table(Main.skin);
        label.setFillParent(true);
        label.pad(5).add(book).expand().fill();
        label.addAction(Actions.forever(Actions.sequence(Actions.moveBy(0, 40, 4), Actions.moveBy(0, -80, 8), Actions.moveBy(0, 40, 4))));
        stage.addActor(label);
        label = new Table(Main.skin);
        label.setFillParent(true);
        label.pad(5).add(title).spaceBottom(20).expand().fill();
        stage.addActor(label);

        optionsTable = new Table(Main.skin);
        optionsTable.setFillParent(true);
        optionsTable.pad(20);
        optionsTable.bottom();
        final Option continueGame = new Option("Continue Game") {
            @Override
            public void run() {
                stage.addAction(Actions.sequence(Actions.fadeOut(1), Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        Player.load();
                    }
                })));
            }
        };
        final Option newGame = new Option("New Game") {
            @Override
            public void run() {
                stage.addAction(Actions.sequence(Actions.fadeOut(1), Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        Player.reset();
                    }
                })));
            }
        };
        final Option exit = new Option("Exit Game") {
            @Override
            public void run() {
                stage.addAction(Actions.sequence(Actions.fadeOut(1), Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        Gdx.app.exit();
                    }
                })));
            }
        };
        optionsTable.add(continueGame).center().row();
        optionsTable.add(newGame).center().row();
        optionsTable.add(exit).center().row();
        stage.addActor(optionsTable);

        Table soundTable = new Table(Main.skin);
        soundTable.setFillParent(true);
        soundTable.pad(20).right().bottom();
        Button soundToggle = new TextButton("sound", Main.skin, "toggle");
        soundToggle.setChecked(Player.sound);
        soundToggle.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Player.sound = !Player.sound;
                Player.saveSound();
            }
        });
        Button musicToggle = new TextButton("music", Main.skin, "toggle");
        musicToggle.setChecked(Player.music);
        musicToggle.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Player.music = !Player.music;
                Player.saveSound();
                Main.bgm.setVolume(Main.bgmId, Player.music ? 1 : 0);
            }
        });
        soundTable.add(soundToggle).padRight(2);
        soundTable.add(musicToggle);
        stage.addActor(soundTable);

        this.options = new Option[]{continueGame, newGame, exit};
        updateSelected(continueGame);

        stage.addListener(new InputListener() {
            public boolean keyDown(InputEvent event, int keycode) {
                switch (keycode) {
                    case Input.Keys.E:
                    case Input.Keys.ENTER:
                        Main.click();
                        selected.run();
                        break;
                    case Input.Keys.UP:
                    case Input.Keys.W:
                    case Input.Keys.A:
                        if (selected == continueGame) {
                            updateSelected(exit);
                        } else if (selected == newGame) {
                            updateSelected(continueGame);
                        } else if (selected == exit) {
                            updateSelected(newGame);
                        }
                        break;
                    case Input.Keys.DOWN:
                    case Input.Keys.S:
                    case Input.Keys.D:
                        if (selected == continueGame) {
                            updateSelected(newGame);
                        } else if (selected == newGame) {
                            updateSelected(exit);
                        } else if (selected == exit) {
                            updateSelected(continueGame);
                        }
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
    }

    private void updateSelected(Option option) {
        selected = option;

        for (Option currOption : options) {
            if (currOption == selected) {
                currOption.setText("> " + currOption.message + " <");
                currOption.setColor(Color.ORANGE);
            } else {
                currOption.setText(currOption.message);
                currOption.setColor(Color.WHITE);
            }
        }
    }

    private abstract class Option extends Label {
        private final String message;

        public Option(CharSequence text) {
            super(text, Main.skin, "large");
            message = (String) text;

            addListener(new InputListener() {
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    Main.click();
                    run();
                    return true;
                }

                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    updateSelected(Option.this);
                }
            });
        }

        public abstract void run();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        Main.changeBGM("Arpanauts.mp3");
        stage.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(1)));
    }

    @Override
    public void render(float delta) {
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
