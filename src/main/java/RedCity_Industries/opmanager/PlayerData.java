package RedCity_Industries.opmanager;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import java.util.Collection;

public class PlayerData {

    private final ItemStack[] inventory;
    private final ItemStack[] armor;
    private final ItemStack offHand;
    private final Location location;
    private final GameMode gameMode;
    private final int level;
    private final float exp;
    private final double health;
    private final int foodLevel;
    private final float saturation;
    private final Collection<PotionEffect> potionEffects;

    // Konstruktor - speichert alle Daten
    public PlayerData(ItemStack[] inventory, ItemStack[] armor, ItemStack offHand,
                      Location location, GameMode gameMode, int level, float exp,
                      double health, int foodLevel, float saturation,
                      Collection<PotionEffect> potionEffects) {
        this.inventory = inventory;
        this.armor = armor;
        this.offHand = offHand;
        this.location = location;
        this.gameMode = gameMode;
        this.level = level;
        this.exp = exp;
        this.health = health;
        this.foodLevel = foodLevel;
        this.saturation = saturation;
        this.potionEffects = potionEffects;
    }

    // Getter-Methoden
    public ItemStack[] getInventory() {
        return inventory;
    }

    public ItemStack[] getArmor() {
        return armor;
    }

    public ItemStack getOffHand() {
        return offHand;
    }

    public Location getLocation() {
        return location;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public int getLevel() {
        return level;
    }

    public float getExp() {
        return exp;
    }

    public double getHealth() {
        return health;
    }

    public int getFoodLevel() {
        return foodLevel;
    }

    public float getSaturation() {
        return saturation;
    }

    public Collection<PotionEffect> getPotionEffects() {
        return potionEffects;
    }
}