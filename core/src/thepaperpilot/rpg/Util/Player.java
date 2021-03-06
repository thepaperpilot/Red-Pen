package thepaperpilot.rpg.Util;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.Vector2;
import thepaperpilot.rpg.Battles.Attack;
import thepaperpilot.rpg.Main;
import thepaperpilot.rpg.Screens.Area;

import java.util.*;

public class Player {
    private static Preferences save;

    private static String area;

    private static float health;
    private static float maxHealth;
    private static int deaths;

    private static final ArrayList<Attack> inventory = new ArrayList<Attack>();
    private static final ArrayList<Attack> attacks = new ArrayList<Attack>();

    private static final ArrayList<String> attributes = new ArrayList<String>();

    private static final Comparator<Attack> comparator;

    private static float x;
    private static float y;

    public static boolean sound;
    public static boolean music;

    public static void setPreferences(Preferences preferences) {
        save = preferences;
        sound = save.getBoolean("sound", true);
        music = save.getBoolean("music", true);
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
                return (int) (oAttack.prototype.damage - attack.prototype.damage);
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
        save.remove("x");
        save.remove("y");

        save.flush();
    }

    public static void save(float x, float y) {
        save();
        save.putFloat("x", x);
        save.putFloat("y", y);
        save.flush();
    }

    public static void save(Area area) {
        if (area != null) {
            Vector2 pos = Mappers.position.get(area.player).position;
            save(pos.x, pos.y);

        } else save();
    }

    public static void saveSound() {
        save.putBoolean("sound", sound);
        save.putBoolean("music", music);

        save.flush();
    }

    public static void load() {
        setArea(save.getString("area", "welcome"));
        setHealth(save.getFloat("health", 10));
        setMaxHealth(save.getFloat("maxHealth", 10));
        setDeaths(save.getInteger("deaths", 0));
        inventory.clear();
        String[] inventoryStrings = save.getString("inventory", "").split(",");
        for (String attackString : inventoryStrings) {
            if (!Attack.prototypes.containsKey(attackString)) continue;
            addInventory(attackString);
        }
        attacks.clear();
        String[] attackStrings = save.getString("attacks", "pencil,heal").split(",");
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
        setPosition(save.getFloat("x", -1), save.getFloat("y", -1));

        Vector2 pos = Player.getPosition();
        if (pos == null) Main.changeContext(Player.getArea());
        else Main.changeContext(Player.getArea(), pos, pos);
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
        save.remove("x");
        save.remove("y");

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

    private static String getArea() {
        return area;
    }

    public static float getHealth() {
        return health;
    }

    public static float getMaxHealth() {
        return maxHealth;
    }

    public static int getDeaths() {
        return deaths;
    }

    public static boolean getAttribute(String attribute) {
        return attributes.contains(attribute);
    }

    private static Vector2 getPosition() {
        if (x == -1 || y == -1) {
            return null;
        }
        else return new Vector2(x, y);
    }

    public static void addHealth(float health) {
        setHealth(getHealth() + health);
    }

    public static void addMaxHealth(float health) {
        setMaxHealth(getMaxHealth() + health);
    }

    public static void addDeath() {
        deaths++;
        save.putInteger("deaths", deaths);
        save.flush();
    }

    public static void addInventory(String item) {
        if (!Attack.prototypes.containsKey(item)) return;
        addInventory(new Attack(Attack.prototypes.get(item)));
    }

    public static void addInventory(Attack item) {
        inventory.add(item);
        Collections.sort(inventory, comparator);
    }

    private static void addAttack(String attack) {
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

    private static void setMaxHealth(float health) {
        Player.maxHealth = health;
    }

    private static void setDeaths(int deaths) {
        Player.deaths = deaths;
    }

    private static void setPosition(float x, float y) {
        Player.x = x;
        Player.y = y;
    }
}
