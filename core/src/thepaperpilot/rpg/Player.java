package thepaperpilot.rpg;

import com.badlogic.gdx.Preferences;
import thepaperpilot.rpg.Battles.Attack;

import java.util.*;

public class Player {
    private static Preferences save;

    private static String area;

    private static float health;
    private static float maxHealth;

    private static final ArrayList<Attack> inventory = new ArrayList<Attack>();
    private static final ArrayList<Attack> attacks = new ArrayList<Attack>();

    private static final ArrayList<String> attributes = new ArrayList<String>();

    private static Comparator<Attack> comparator;

    public static void setPreferences(Preferences preferences) {
        save = preferences;
    }

    static {
        comparator = new Comparator<Attack>() {
            @Override
            public int compare(Attack attack, Attack oAttack) {
                if (attack.prototype == oAttack.prototype) {
                    if (attacks.contains(attack) && !attacks.contains(oAttack))
                        return -1;
                    else if (!attacks.contains(attack) && attacks.contains(oAttack))
                        return 1;
                    else return 0;
                }
                return attack.prototype.name.compareTo(oAttack.prototype.name);
            }
        };
    }

    public static void save() {
        save.putString("area", area);
        save.putFloat("health", health);
        save.putFloat("maxHealth", maxHealth);
        String inventoryString = "";
        for (Attack attack : inventory) {
            if (attacks.contains(attack)) continue;
            inventoryString += attack.prototype.name + ",";
        }
        save.putString("inventory", inventoryString);
        String attackString = "";
        for (Attack attack : attacks) {
            attackString += attack.prototype.name + ",";
        }
        save.putString("attacks", attackString);
        String attributes = Arrays.toString(Player.attributes.toArray(new String[Player.attributes.size()])).replaceAll(", ", ",");
        save.putString("attributes", attributes.substring(1, attributes.length() - 1));

        save.flush();
    }

    public static void load() {
        setArea(save.getString("area", "welcome"));
        setHealth(save.getFloat("health", 10));
        setMaxHealth(save.getFloat("maxHealth", 10));
        inventory.clear();
        String[] inventoryStrings = save.getString("inventory", "").split(",");
        for (String attackString : inventoryStrings) {
            if (!Attack.prototypes.containsKey(attackString)) continue;
            addInventory(attackString);
        }
        attacks.clear();
        String[] attackStrings = save.getString("attacks", "pencil,heal,run").split(",");
        for (String attackString : attackStrings) {
            if (!Attack.prototypes.containsKey(attackString)) continue;
            Attack attack = new Attack(Attack.prototypes.get(attackString));
            addAttack(attack);
            addInventory(attack);
        }
        if (attacks.isEmpty()) addAttack("run");
        Collections.sort(inventory, comparator);
        Collections.sort(attacks, comparator);
        Player.attributes.clear();
        String[] attributes = save.getString("attributes", "").split(",");
        Collections.addAll(Player.attributes, attributes);
    }

    public static void reset() {
        save.remove("area");
        save.remove("health");
        save.remove("maxHealth");
        save.remove("x");
        save.remove("y");
        save.remove("inventory");
        save.remove("attacks");
        save.remove("attributes");

        load();
    }

    public static List<Attack> getInventory() {
        return inventory;
    }

    private static Attack getAttack(String run) {
        for (Attack attack : inventory) {
            if (attack.prototype.name.equals(run))
                return attack;
        }
        if (!Attack.prototypes.containsKey(run)) return null;
        Attack attack = new Attack(Attack.prototypes.get(run));
        addInventory(attack);
        addAttack(attack);
        return attack;
    }

    public static List<Attack> getAttacks() {
        return attacks;
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

    public static boolean getAttribute(String attribute) {
        return attributes.contains(attribute);
    }

    public static void addHealth(float health) {
        setHealth(getHealth() + health);
    }

    public static void addMaxHealth(float health) {
        setMaxHealth(getMaxHealth() + health);
    }

    public static void addInventory(String item) {
        if (!Attack.prototypes.containsKey(item)) return;
        addInventory(new Attack(Attack.prototypes.get(item)));
    }

    public static void addInventory(Attack item) {
        inventory.add(item);
        Collections.sort(inventory, comparator);
    }

    public static void addAttack(String attack) {
        if (!Attack.prototypes.containsKey(attack)) return;
        addAttack(new Attack(Attack.prototypes.get(attack)));
    }

    public static void addAttack(Attack attack) {
        attacks.add(attack);
        Collections.sort(attacks, comparator);
    }

    public static void addAttribute(String attribute) {
        attributes.add(attribute);
    }

    public static void removeInventory(Attack item) {
        inventory.remove(item);
        if (inventory.isEmpty())
            addInventory("run");
        removeAttack(item);
    }

    public static void removeAttack(Attack attack) {
        attacks.remove(attack);
        if (attacks.isEmpty())
            addAttack(getAttack("run"));
    }

    public static void removeAttribute(String attribute) {
        attributes.remove(attribute);
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
