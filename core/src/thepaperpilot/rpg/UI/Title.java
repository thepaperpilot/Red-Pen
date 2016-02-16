package thepaperpilot.rpg.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import thepaperpilot.rpg.Main;
import thepaperpilot.rpg.Player;

public class Title implements Screen{
    Stage stage;

    Option selected;
    Option[] options;
    Table optionsTable;

    public Title() {
        stage = new Stage(new StretchViewport(315, 250));

        Image bg = new Image(Main.getTexture("title"));
        bg.setFillParent(true);
        stage.addActor(bg);

        optionsTable = new Table(Main.skin);
        optionsTable.setFillParent(true);
        optionsTable.pad(20);
        optionsTable.bottom();
        final Option continueGame = new Option("Continue Game") {
            @Override
            public void run() {
                Player.load();
                Main.changeContext(Player.getArea());
            }
        };
        final Option newGame = new Option("New Game") {
            @Override
            public void run() {
                Player.reset();
                Main.changeContext("welcome");
            }
        };
        final Option exit = new Option("Exit Game") {
            @Override
            public void run() {
                Gdx.app.exit();
            }
        };
        optionsTable.add(continueGame).center().row();
        optionsTable.add(newGame).center().row();
        optionsTable.add(exit).center().row();
        stage.addActor(optionsTable);

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
        private String message;

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
        Main.changeBGM("Time Driving");
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
