package RedCity_Industries.opmanager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import java.util.Collection;

public class OpOnCommand implements CommandExecutor {

    private final OpManagerPlugin plugin;

    public OpOnCommand(OpManagerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Prüfen ob Command von einem Spieler kommt
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessageManager().getMessage("general.console-only"));
            return true;
        }

        Player player = (Player) sender;

        // WHITELIST-PRÜFUNG
        if (plugin.getConfig().getBoolean("whitelist_enabled")) {
            if (!plugin.getConfig().getStringList("whitelist").contains(player.getName())) {
                plugin.getMessageManager().send(player, "opon.whitelist-disabled");
                return true;
            }
        }

        // Prüfen ob Spieler bereits OP ist
        if (player.isOp()) {
            plugin.getMessageManager().send(player, "opon.already-op");
            return true;
        }

        // DATEN SPEICHERN
        savePlayerData(player);

        // OP geben
        player.setOp(true);

        // Erfolgsnachricht (angepasst an inventory_save)
        boolean inventorySave = plugin.getConfig().getBoolean("inventory_save", true);

        if (inventorySave) {
            // Normale Nachricht aus messages.yml
            plugin.getMessageManager().sendList(player, "opon.success-full");
        } else {
            // Reduzierte Nachricht aus messages.yml
            plugin.getMessageManager().sendList(player, "opon.success-minimal");
        }

        return true;
    }

    // Methode zum Speichern der Spielerdaten
    private void savePlayerData(Player player) {

        plugin.getMessageManager().sendVerbose("verbose.save-start");

        ItemStack[] inventory;
        ItemStack[] armor;
        ItemStack offHand;
        int level;
        float exp;
        double health;
        int foodLevel;
        float saturation;
        Collection<org.bukkit.potion.PotionEffect> potionEffects;

        // Prüfen ob Inventar gespeichert werden soll
        boolean saveInventory = plugin.getConfig().getBoolean("inventory_save", true);

        if (saveInventory) {

            plugin.getMessageManager().sendVerbose("verbose.inventory-save-enabled");

            // Inventar kopieren (36 Slots) - TIEFE KOPIE!
            plugin.getMessageManager().sendVerbose("verbose.copying-inventory");
            ItemStack[] inventoryOriginal = player.getInventory().getContents();
            inventory = new ItemStack[inventoryOriginal.length];
            for (int i = 0; i < inventoryOriginal.length; i++) {
                if (inventoryOriginal[i] != null) {
                    inventory[i] = inventoryOriginal[i].clone();
                }
            }
            int invCount = 0;
            for (ItemStack item : inventory) {
                if (item != null) invCount++;
            }
            plugin.getMessageManager().sendVerbose("verbose.inventory-item-count", "count", String.valueOf(invCount));

            // Rüstung kopieren (4 Slots) - TIEFE KOPIE!
            plugin.getMessageManager().sendVerbose("verbose.copying-armor");
            ItemStack[] armorOriginal = player.getInventory().getArmorContents();
            armor = new ItemStack[4];
            for (int i = 0; i < 4; i++) {
                if (armorOriginal[i] != null) {
                    armor[i] = armorOriginal[i].clone();
                }
            }
            plugin.getMessageManager().sendVerbose("verbose.armor-helmet", "type", (armor[3] != null ? armor[3].getType().toString() : "NULL"));
            plugin.getMessageManager().sendVerbose("verbose.armor-chestplate", "type", (armor[2] != null ? armor[2].getType().toString() : "NULL"));
            plugin.getMessageManager().sendVerbose("verbose.armor-leggings", "type", (armor[1] != null ? armor[1].getType().toString() : "NULL"));
            plugin.getMessageManager().sendVerbose("verbose.armor-boots", "type", (armor[0] != null ? armor[0].getType().toString() : "NULL"));

            // Offhand kopieren
            plugin.getMessageManager().sendVerbose("verbose.copying-offhand");
            ItemStack offHandOriginal = player.getInventory().getItemInOffHand();
            offHand = (offHandOriginal != null && offHandOriginal.getType() != org.bukkit.Material.AIR)
                    ? offHandOriginal.clone()
                    : null;
            plugin.getMessageManager().sendVerbose("verbose.offhand-item", "type", (offHand != null ? offHand.getType().toString() : "NULL"));

            // XP speichern
            plugin.getMessageManager().sendVerbose("verbose.saving-xp");
            level = player.getLevel();
            exp = player.getExp();
            plugin.getMessageManager().sendVerbose("verbose.xp-details", "level", String.valueOf(level), "exp", String.valueOf(exp));

            // Leben speichern
            plugin.getMessageManager().sendVerbose("verbose.saving-health");
            health = player.getHealth();
            plugin.getMessageManager().sendVerbose("verbose.health-value", "health", String.valueOf(health));

            // Hunger speichern
            plugin.getMessageManager().sendVerbose("verbose.saving-hunger");
            foodLevel = player.getFoodLevel();
            saturation = player.getSaturation();
            plugin.getMessageManager().sendVerbose("verbose.hunger-details", "food", String.valueOf(foodLevel), "saturation", String.valueOf(saturation));

            // Effekte speichern
            plugin.getMessageManager().sendVerbose("verbose.saving-effects");
            potionEffects = player.getActivePotionEffects();
            plugin.getMessageManager().sendVerbose("verbose.effects-count", "count", String.valueOf(potionEffects.size()));

        } else {
            // Inventar-Speicherung deaktiviert - Standardwerte
            plugin.getMessageManager().sendVerbose("verbose.inventory-save-disabled");
            inventory = new ItemStack[0];
            armor = new ItemStack[4];
            offHand = null;
            level = 0;
            exp = 0.0f;
            health = 20.0;
            foodLevel = 20;
            saturation = 5.0f;
            potionEffects = new java.util.ArrayList<>();
        }

        // Position kopieren (wird IMMER gespeichert)
        plugin.getMessageManager().sendVerbose("verbose.copying-location");
        org.bukkit.Location location = player.getLocation().clone();
        plugin.getMessageManager().sendVerbose("verbose.location-details",
                "world", location.getWorld().getName(),
                "x", String.valueOf(location.getBlockX()),
                "y", String.valueOf(location.getBlockY()),
                "z", String.valueOf(location.getBlockZ()));

        // Gamemode speichern (wird IMMER gespeichert)
        plugin.getMessageManager().sendVerbose("verbose.saving-gamemode");
        org.bukkit.GameMode gameMode = player.getGameMode();
        plugin.getMessageManager().sendVerbose("verbose.gamemode-value", "gamemode", gameMode.toString());

        // PlayerData-Objekt erstellen
        plugin.getMessageManager().sendVerbose("verbose.creating-playerdata");
        PlayerData data = new PlayerData(inventory, armor, offHand, location, gameMode,
                level, exp, health, foodLevel, saturation, potionEffects);

        // Im StorageManager speichern
        plugin.getMessageManager().sendVerbose("verbose.storing-in-manager");
        plugin.getStorageManager().savePlayerData(player.getName(), data);

        // Log-Nachricht aus messages.yml
        if (saveInventory) {
            plugin.getLogger().info(plugin.getMessageManager().getMessage("logs.data-saved",
                    "player", player.getName()));
        } else {
            plugin.getLogger().info(plugin.getMessageManager().getMessage("logs.data-saved-minimal",
                    "player", player.getName()));
        }

        plugin.getMessageManager().sendVerbose("verbose.save-end");
    }
}