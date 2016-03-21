package thepaperpilot.rpg.Events;

import thepaperpilot.rpg.Battles.Attack;
import thepaperpilot.rpg.Battles.Battle;
import thepaperpilot.rpg.Context;
import thepaperpilot.rpg.Player;

public class SetAttack extends Event {
    private int attack = 0;

    public SetAttack(int attack) {
        this.attack = attack;
    }

    @Override
    public void run(Context context) {
        if (!(context instanceof Battle)) return;
        if (Player.getAttacks().size() < attack + 1) return;
        Battle battle = ((Battle) context);
        battle.attack();
        Attack attack = Player.getAttacks().get(this.attack);
        attack.init(battle, battle.playerPos);
        battle.attacks.add(attack);
        runNext(context);
    }
}
