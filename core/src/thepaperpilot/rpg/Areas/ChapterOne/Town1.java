package thepaperpilot.rpg.Areas.ChapterOne;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import thepaperpilot.rpg.Battles.Battle;
import thepaperpilot.rpg.Context;
import thepaperpilot.rpg.Main;
import thepaperpilot.rpg.Map.Area;
import thepaperpilot.rpg.Map.Entity;
import thepaperpilot.rpg.UI.Dialogue;

public class Town1 extends Area.AreaPrototype {
    public Town1() {
        super("town1");

        /* Adding things to area */
        entities = new Entity[]{};
        dialogues = new Dialogue[]{};
        battles = new Battle.BattlePrototype[]{};
        bgm = "Searching.mp3";
        viewport = new Vector2(8 * Main.TILE_SIZE, 8 * Main.TILE_SIZE);
        playerPosition = new Vector2(Main.TILE_SIZE, 8 * Main.TILE_SIZE);
        mapSize = new Vector2(64, 16);
        tint = new Color(1, .8f, 1, 1);
    }

    public void loadAssets(AssetManager manager) {
        super.loadAssets(manager);
        manager.load("Searching.mp3", Sound.class);
    }

    public Context getContext() {
        Area area = new Area(this);
        return area;
    }
}
