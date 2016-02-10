package thepaperpilot.rpg;

import com.badlogic.gdx.Preferences;
import thepaperpilot.rpg.Battles.Attack;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Player {
    private static Preferences save;

    private static String area;
    private static float health;
    private static float maxHealth;
    private static final Map<String, Attack.AttackPrototype> attacks = new HashMap<String, Attack.AttackPrototype>();

    public static void setPreferences(Preferences preferences) {
        save = preferences;
    }

    public static void save() {
        save.putString("area", area);
        save.putFloat("health", health);
        save.putFloat("maxHealth", maxHealth);
        String attackString = "";
        for (Attack.AttackPrototype prototype : attacks.values()) {
            attackString += prototype.name + ",";
        }
        save.putString("attacks", attackString);
        save.flush();
    }

    public static void load() {
        area = save.getString("area", "welcome");
        health = save.getFloat("health", 10);
        maxHealth = save.getFloat("maxHealth", 10);
        attacks.clear();
        String[] attackStrings = save.getString("attacks", "stick,heal,run").split(",");
        for (String attackString : attackStrings) {
            attacks.put(attackString, Attack.prototypes.get(attackString));
        }
    }

    public static void reset() {
        save.remove("area");
        save.remove("health");
        save.remove("maxHealth");
        save.remove("x");
        save.remove("y");
        save.remove("attacks");

        load();
    }

    public static String getArea() {
        return area;
    }

    public static float getHealth() {
        return health;
    }

    public static float getMaxHealth() {
        return maxHealth;
    }

    public static Attack.AttackPrototype getAttack(String attack) {
        return attacks.get(attack);
    }

    public static Collection<Attack.AttackPrototype> getAttacks() {
        return attacks.values();
    }

    public static void addHealth(float health) {
        setHealth(getHealth() + health);
    }

    public static void addAttack(String attack) {
        attacks.put(attack, Attack.prototypes.get(attack));
    }

    public static void removeAttack(String attack) {
        attacks.remove(Attack.prototypes.get(attack));
    }

    public static void setArea(String area) {
        Player.area = area;
    }

    public static void setHealth(float health) {
        Player.health = health;
    }

    public static void setMaxHealth(float health) {
        Player.maxHealth = health;
    }
}
