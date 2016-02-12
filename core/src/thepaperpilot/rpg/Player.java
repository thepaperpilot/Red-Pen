package thepaperpilot.rpg;

import com.badlogic.gdx.Preferences;
import thepaperpilot.rpg.Battles.Attack;

import java.util.ArrayList;
import java.util.Collection;

public class Player {
    private static Preferences save;

    private static String area;

    private static float health;
    private static float maxHealth;

    private static final ArrayList<Attack.AttackPrototype> inventory = new ArrayList<Attack.AttackPrototype>();
    private static final ArrayList<Attack.AttackPrototype> attacks = new ArrayList<Attack.AttackPrototype>();

    private static boolean portal;
    private static boolean nm;

    public static void setPreferences(Preferences preferences) {
        save = preferences;
    }

    public static void save() {
        save.putString("area", area);
        save.putFloat("health", health);
        save.putFloat("maxHealth", maxHealth);
        String inventoryString = "";
        for (Attack.AttackPrototype prototype : inventory) {
            inventoryString += prototype.name + ",";
        }
        save.putString("inventory", inventoryString);
        String attackString = "";
        for (Attack.AttackPrototype prototype : attacks) {
            attackString += prototype.name + ",";
        }
        save.putString("attacks", attackString);
        save.putBoolean("portal", portal);
        save.putBoolean("nm", nm);

        save.flush();
    }

    public static void load() {
        area = save.getString("area", "welcome");
        health = save.getFloat("health", 10);
        maxHealth = save.getFloat("maxHealth", 10);
        inventory.clear();
        String[] inventoryStrings = save.getString("inventory", "stick,heal,run").split(",");
        for (String attackString : inventoryStrings) {
            inventory.add(Attack.prototypes.get(attackString));
        }
        attacks.clear();
        String[] attackStrings = save.getString("attacks", "stick,heal,run").split(",");
        for (String attackString : attackStrings) {
            attacks.add(Attack.prototypes.get(attackString));
        }
        portal = save.getBoolean("portal", false);
        nm = save.getBoolean("nm", false);
    }

    public static void reset() {
        save.remove("area");
        save.remove("health");
        save.remove("maxHealth");
        save.remove("x");
        save.remove("y");
        save.remove("inventory");
        save.remove("attacks");
        save.remove("portal");
        save.remove("nm");

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

    public static Collection<Attack.AttackPrototype> getInventory() {
        return inventory;
    }

    public static Collection<Attack.AttackPrototype> getAttacks() {
        return attacks;
    }

    public static boolean getPortal() {
        return portal;
    }

    public static boolean getNM() {
        return nm;
    }

    public static void addHealth(float health) {
        setHealth(getHealth() + health);
    }

    public static void addMaxHealth(float health) {
        setMaxHealth(getMaxHealth() + health);
    }

    public static void addInventory(String item) {
        addInventory(Attack.prototypes.get(item));
    }

    public static void addInventory(Attack.AttackPrototype item) {
        inventory.add(item);
    }

    public static void addAttack(String attack) {
        addAttack(Attack.prototypes.get(attack));
    }

    public static void addAttack(Attack.AttackPrototype attack) {
        attacks.add(attack);
    }

    public static void removeInventory(String item) {
        removeInventory(Attack.prototypes.get(item));
    }

    public static void removeInventory(Attack.AttackPrototype item) {
        inventory.remove(item);
        if (inventory.isEmpty())
            addInventory("run");
        removeAttack(item);
    }

    public static void removeAttack(String attack) {
        removeAttack(Attack.prototypes.get(attack));
    }

    public static void removeAttack(Attack.AttackPrototype attack) {
        attacks.remove(attack);
        if (attacks.isEmpty())
            addAttack("run");
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

    public static void setPortal(boolean portal) {
        Player.portal = portal;
    }

    public static void setNM(boolean nm) {
        Player.nm = nm;
    }
}
