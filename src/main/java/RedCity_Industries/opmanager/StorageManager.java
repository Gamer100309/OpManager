package RedCity_Industries.opmanager;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class StorageManager {

    private final OpManagerPlugin plugin;
    private final File dataFolder;
    private final String strategy;

    // In-Memory Storage (für MEMORY_ONLY und DISK_AND_MEMORY)
    private HashMap<String, PlayerData> memoryStorage;

    public StorageManager(OpManagerPlugin plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "playerdata");
        this.strategy = plugin.getConfig().getString("storage_strategy", "DISK_AND_MEMORY");
        this.memoryStorage = new HashMap<>();

        // Ordner erstellen falls nicht vorhanden
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        logMessage("logs.storage-strategy", "strategy", strategy);
    }

    // Daten speichern
    public void savePlayerData(String playerName, PlayerData data) {

        switch (strategy.toUpperCase()) {
            case "MEMORY_ONLY":
                // Nur im RAM speichern
                memoryStorage.put(playerName, data);
                logMessage("logs.data-saved-memory", "player", playerName);
                break;

            case "DISK_ONLY":
                // Nur auf Festplatte speichern
                saveToDisk(playerName, data);
                logMessage("logs.data-saved-disk", "player", playerName);
                break;

            case "DISK_AND_MEMORY":
                // Beides
                memoryStorage.put(playerName, data);
                saveToDisk(playerName, data);
                logMessage("logs.data-saved-both", "player", playerName);
                break;

            default:
                plugin.getLogger().warning("Unbekannte Storage-Strategie: " + strategy + " - nutze DISK_AND_MEMORY");
                memoryStorage.put(playerName, data);
                saveToDisk(playerName, data);
                break;
        }
    }

    // Daten laden
    public PlayerData loadPlayerData(String playerName) {

        switch (strategy.toUpperCase()) {
            case "MEMORY_ONLY":
                // Aus RAM laden
                return memoryStorage.get(playerName);

            case "DISK_ONLY":
                // Von Festplatte laden
                return loadFromDisk(playerName);

            case "DISK_AND_MEMORY":
                // Erst RAM probieren, dann Disk
                PlayerData memData = memoryStorage.get(playerName);
                if (memData != null) {
                    return memData;
                }
                return loadFromDisk(playerName);

            default:
                plugin.getLogger().warning("Unbekannte Storage-Strategie: " + strategy);
                return memoryStorage.get(playerName);
        }
    }

    // Daten löschen
    public void deletePlayerData(String playerName) {

        // Aus RAM löschen
        memoryStorage.remove(playerName);

        // Von Disk löschen (außer bei MEMORY_ONLY)
        if (!strategy.equalsIgnoreCase("MEMORY_ONLY")) {
            File file = new File(dataFolder, playerName + ".yml");
            if (file.exists()) {
                file.delete();
                logMessage("logs.data-file-deleted", "player", playerName);
            }
        }
    }

    // Prüfen ob Daten existieren
    public boolean hasPlayerData(String playerName) {

        switch (strategy.toUpperCase()) {
            case "MEMORY_ONLY":
                return memoryStorage.containsKey(playerName);

            case "DISK_ONLY":
                File file = new File(dataFolder, playerName + ".yml");
                return file.exists();

            case "DISK_AND_MEMORY":
                return memoryStorage.containsKey(playerName) ||
                        new File(dataFolder, playerName + ".yml").exists();

            default:
                return memoryStorage.containsKey(playerName);
        }
    }

    // === PRIVATE HELPER-METHODEN ===

    // Auf Festplatte speichern
    private void saveToDisk(String playerName, PlayerData data) {

        File file = new File(dataFolder, playerName + ".yml");
        YamlConfiguration yaml = new YamlConfiguration();

        try {
            // Inventar speichern
            yaml.set("inventory", data.getInventory());

            // Rüstung speichern
            yaml.set("armor", data.getArmor());

            // Offhand speichern
            yaml.set("offhand", data.getOffHand());

            // Position speichern
            Location loc = data.getLocation();
            yaml.set("location.world", loc.getWorld().getName());
            yaml.set("location.x", loc.getX());
            yaml.set("location.y", loc.getY());
            yaml.set("location.z", loc.getZ());
            yaml.set("location.yaw", loc.getYaw());
            yaml.set("location.pitch", loc.getPitch());

            // Gamemode speichern
            yaml.set("gamemode", data.getGameMode().toString());

            // XP speichern
            yaml.set("xp.level", data.getLevel());
            yaml.set("xp.exp", data.getExp());

            // Leben speichern
            yaml.set("health", data.getHealth());

            // Hunger speichern
            yaml.set("food.level", data.getFoodLevel());
            yaml.set("food.saturation", data.getSaturation());

            // Effekte speichern
            List<String> effects = new ArrayList<>();
            for (PotionEffect effect : data.getPotionEffects()) {
                String effectString = effect.getType().getName() + "," +
                        effect.getAmplifier() + "," +
                        effect.getDuration();
                effects.add(effectString);
            }
            yaml.set("effects", effects);

            // Datei speichern
            yaml.save(file);

        } catch (IOException e) {
            plugin.getLogger().severe("Fehler beim Speichern von " + playerName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Von Festplatte laden
    private PlayerData loadFromDisk(String playerName) {

        File file = new File(dataFolder, playerName + ".yml");

        if (!file.exists()) {
            return null;
        }

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        try {
            // Inventar laden
            @SuppressWarnings("unchecked")
            List<ItemStack> invList = (List<ItemStack>) yaml.get("inventory");
            ItemStack[] inventory = invList != null ? invList.toArray(new ItemStack[0]) : new ItemStack[0];

            // Rüstung laden
            @SuppressWarnings("unchecked")
            List<ItemStack> armorList = (List<ItemStack>) yaml.get("armor");
            ItemStack[] armor = armorList != null ? armorList.toArray(new ItemStack[0]) : new ItemStack[4];

            // Offhand laden
            ItemStack offHand = yaml.getItemStack("offhand");

            // Position laden
            String worldName = yaml.getString("location.world");
            World world = plugin.getServer().getWorld(worldName);
            double x = yaml.getDouble("location.x");
            double y = yaml.getDouble("location.y");
            double z = yaml.getDouble("location.z");
            float yaw = (float) yaml.getDouble("location.yaw");
            float pitch = (float) yaml.getDouble("location.pitch");
            Location location = new Location(world, x, y, z, yaw, pitch);

            // Gamemode laden
            GameMode gameMode = GameMode.valueOf(yaml.getString("gamemode"));

            // XP laden
            int level = yaml.getInt("xp.level");
            float exp = (float) yaml.getDouble("xp.exp");

            // Leben laden
            double health = yaml.getDouble("health");

            // Hunger laden
            int foodLevel = yaml.getInt("food.level");
            float saturation = (float) yaml.getDouble("food.saturation");

            // Effekte laden
            Collection<PotionEffect> potionEffects = new ArrayList<>();
            List<String> effectStrings = yaml.getStringList("effects");
            for (String effectString : effectStrings) {
                String[] parts = effectString.split(",");
                PotionEffectType type = PotionEffectType.getByName(parts[0]);
                int amplifier = Integer.parseInt(parts[1]);
                int duration = Integer.parseInt(parts[2]);
                potionEffects.add(new PotionEffect(type, duration, amplifier));
            }

            // PlayerData-Objekt erstellen und zurückgeben
            return new PlayerData(inventory, armor, offHand, location, gameMode,
                    level, exp, health, foodLevel, saturation, potionEffects);

        } catch (Exception e) {
            plugin.getLogger().severe("Fehler beim Laden von " + playerName + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Helper-Methode für Log-Nachrichten mit Platzhaltern
    private void logMessage(String path, String... replacements) {
        String message = plugin.getMessageManager().getMessage(path, replacements);
        plugin.getLogger().info(message);
    }
}