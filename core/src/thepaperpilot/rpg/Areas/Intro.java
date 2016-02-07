package thepaperpilot.rpg.Areas;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import thepaperpilot.rpg.Battles.Battle;
import thepaperpilot.rpg.Battles.Enemy;
import thepaperpilot.rpg.Map.Area;

public class Intro extends Area.AreaPrototype {
    public Intro() {
        /* Enemies */
        Enemy.EnemyPrototype satanEnemy = new Enemy.EnemyPrototype("satan", "satan", 40, 200, 4);
        //satanEnemy.attacks = new Attack.AttackPrototype[]{satanAttack};

        /* Battles */
        Battle.BattlePrototype boss = new Battle.BattlePrototype("satan");
        boss.enemies = new Enemy.EnemyPrototype[]{satanEnemy};
        //boss.winEvents = boss.loseEvents = new Event.EventPrototype[]{win, removeJoker};
        boss.bgm = "Sad Descent";
    }

    public void loadAssets(AssetManager manager) {
        manager.load("satan.png", Texture.class);
        manager.load("Sad Descent.ogg", Sound.class);
    }
}
