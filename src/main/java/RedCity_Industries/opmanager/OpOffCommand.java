package RedCity_Industries.opmanager;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class OpOffCommand implements CommandExecutor {

    private final OpManagerPlugin plugin;

    public OpOffCommand(OpManagerPlugin plugin) {
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

        // Prüfen ob Spieler überhaupt OP hat
        if (!player.isOp()) {
            plugin.getMessageManager().send(player, "opoff.no-op");
            return true;
        }

        // Prüfen ob gespeicherte Daten existieren
        boolean hasData = plugin.getStorageManager().hasPlayerData(player.getName());

        if (!hasData) {
            // Keine gespeicherten Daten, aber OP trotzdem entfernen
            player.setOp(false);
            plugin.getMessageManager().sendList(player, "opoff.no-data-found");
            return true;
        }

        // Daten aus StorageManager holen
        PlayerData data = plugin.getStorageManager().loadPlayerData(player.getName());

        // Vanish automatisch deaktivieren (falls aktiviert in Config)
        if (plugin.getConfig().getBoolean("auto_disable_vanish", false)) {
            String vanishCommand = plugin.getConfig().getString("vanish_disable_command", "vanishium:vium {player}");

            // Platzhalter {player} durch echten Namen ersetzen
            vanishCommand = vanishCommand.replace("{player}", player.getName());

            plugin.getMessageManager().sendVerbose("verbose.vanish-command", "cmd", vanishCommand);

            // Als CONSOLE ausführen (nicht als Spieler)
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), vanishCommand);

            plugin.getMessageManager().send(player, "opoff.vanish-disabled");
            plugin.getLogger().info(plugin.getMessageManager().getMessage("logs.vanish-disabled", "player", player.getName()));
        }

        // OP entfernen
        player.setOp(false);

        // ALLES WIEDERHERSTELLEN
        restorePlayerData(player, data);

        // Daten aus StorageManager löschen (aufräumen)
        plugin.getStorageManager().deletePlayerData(player.getName());
        plugin.getMessageManager().sendVerbose("verbose.data-deleted");

        // Erfolgsnachricht (angepasst an inventory_save)
        boolean inventorySave = plugin.getConfig().getBoolean("inventory_save", true);

        if (inventorySave) {
            plugin.getMessageManager().sendList(player, "opoff.success-full");
        } else {
            plugin.getMessageManager().sendList(player, "opoff.success-minimal");
        }
        return true;
    }

    // Methode zum Wiederherstellen der Spielerdaten
    private void restorePlayerData(Player player, PlayerData data) {

        plugin.getMessageManager().sendVerbose("verbose.restore-start");

        // Prüfen ob, Inventar gespeichert wurde
        boolean inventorySave = plugin.getConfig().getBoolean("inventory_save", true);

        if (inventorySave) {
            // Inventar wurde gespeichert - normal wiederherstellen

            // Inventar komplett leeren
            plugin.getMessageManager().sendVerbose("verbose.clearing-inventory");
            player.getInventory().clear();

            // ALLES ANDERE mit 1 Tick Verzögerung
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {

                // Position wiederherstellen (IMMER!)
                plugin.getMessageManager().sendVerbose("verbose.restoring-location");
                Location loc = data.getLocation();
                plugin.getMessageManager().sendVerbose("verbose.location-world", "world", loc.getWorld().getName());
                player.teleport(loc);

                // Gamemode wiederherstellen
                plugin.getMessageManager().sendVerbose("verbose.restoring-gamemode");
                GameMode gm = data.getGameMode();
                plugin.getMessageManager().sendVerbose("verbose.gamemode-value", "gamemode", gm.toString());
                player.setGameMode(gm);

                // Rüstung setzen
                plugin.getMessageManager().sendVerbose("verbose.restoring-armor");
                ItemStack[] armor = data.getArmor();
                plugin.getMessageManager().sendVerbose("verbose.armor-helmet", "type", (armor[3] != null ? armor[3].getType().toString() : "NULL"));
                plugin.getMessageManager().sendVerbose("verbose.armor-chestplate", "type", (armor[2] != null ? armor[2].getType().toString() : "NULL"));
                plugin.getMessageManager().sendVerbose("verbose.armor-leggings", "type", (armor[1] != null ? armor[1].getType().toString() : "NULL"));
                plugin.getMessageManager().sendVerbose("verbose.armor-boots", "type", (armor[0] != null ? armor[0].getType().toString() : "NULL"));
                player.getInventory().setArmorContents(armor);

                // Offhand setzen
                plugin.getMessageManager().sendVerbose("verbose.restoring-offhand");
                ItemStack offHand = data.getOffHand();
                plugin.getMessageManager().sendVerbose("verbose.offhand-item", "type", (offHand != null ? offHand.getType().toString() : "NULL"));
                player.getInventory().setItemInOffHand(offHand);

                // Inventar setzen
                plugin.getMessageManager().sendVerbose("verbose.restoring-inventory");
                ItemStack[] inventory = data.getInventory();
                int itemCount = 0;
                for (ItemStack item : inventory) {
                    if (item != null) itemCount++;
                }
                plugin.getMessageManager().sendVerbose("verbose.inventory-item-count", "count", String.valueOf(itemCount));
                player.getInventory().setContents(inventory);

                // XP wiederherstellen
                plugin.getMessageManager().sendVerbose("verbose.restoring-xp");
                plugin.getMessageManager().sendVerbose("verbose.xp-level", "level", String.valueOf(data.getLevel()));
                player.setLevel(data.getLevel());
                player.setExp(data.getExp());

                // Leben wiederherstellen
                plugin.getMessageManager().sendVerbose("verbose.restoring-health");
                plugin.getMessageManager().sendVerbose("verbose.health-value", "health", String.valueOf(data.getHealth()));
                player.setHealth(data.getHealth());

                // Hunger wiederherstellen
                plugin.getMessageManager().sendVerbose("verbose.restoring-hunger");
                plugin.getMessageManager().sendVerbose("verbose.hunger-details", "food", String.valueOf(data.getFoodLevel()), "saturation", String.valueOf(data.getSaturation()));
                player.setFoodLevel(data.getFoodLevel());
                player.setSaturation(data.getSaturation());

                // Effekte wiederherstellen
                plugin.getMessageManager().sendVerbose("verbose.restoring-effects");
                for (org.bukkit.potion.PotionEffect effect : player.getActivePotionEffects()) {
                    player.removePotionEffect(effect.getType());
                }
                plugin.getMessageManager().sendVerbose("verbose.effects-count", "count", String.valueOf(data.getPotionEffects().size()));
                player.addPotionEffects(data.getPotionEffects());

                // Log-Nachricht
                plugin.getLogger().info(plugin.getMessageManager().getMessage("logs.data-restored",
                        "player", player.getName()));

                plugin.getMessageManager().sendVerbose("verbose.restore-end");

            }, 1L);

        } else {
            // Inventar wurde NICHT gespeichert - nur Position & Gamemode
            plugin.getMessageManager().sendVerbose("verbose.inventory-save-disabled");

            // Mit 1 Tick Verzögerung (für Stabilität)
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {

                // Position wiederherstellen
                plugin.getMessageManager().sendVerbose("verbose.restoring-location");
                Location loc = data.getLocation();
                plugin.getMessageManager().sendVerbose("verbose.location-world", "world", loc.getWorld().getName());
                player.teleport(loc);

                // Gamemode wiederherstellen
                plugin.getMessageManager().sendVerbose("verbose.restoring-gamemode");
                GameMode gm = data.getGameMode();
                plugin.getMessageManager().sendVerbose("verbose.gamemode-value", "gamemode", gm.toString());
                player.setGameMode(gm);

                // Log-Nachricht
                plugin.getLogger().info(plugin.getMessageManager().getMessage("logs.data-restored-minimal",
                        "player", player.getName()));

                plugin.getMessageManager().sendVerbose("verbose.restore-end-minimal");

            }, 1L);
        }
    }
}