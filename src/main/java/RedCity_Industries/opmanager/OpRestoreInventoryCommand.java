package RedCity_Industries.opmanager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class OpRestoreInventoryCommand implements CommandExecutor {

    private final OpManagerPlugin plugin;

    public OpRestoreInventoryCommand(OpManagerPlugin plugin) {
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

        // Prüfen ob Spieler OP hat
        if (!player.isOp()) {
            plugin.getMessageManager().send(player, "restore.no-op");
            return true;
        }

        // Prüfen ob inventory_save aktiviert ist
        if (!plugin.getConfig().getBoolean("inventory_save", true)) {
            plugin.getMessageManager().sendList(player, "restore.inventory-disabled");
            return true;
        }

        // Prüfen ob gespeicherte Daten existieren
        if (!plugin.getStorageManager().hasPlayerData(player.getName())) {
            plugin.getMessageManager().sendList(player, "restore.no-data");
            return true;
        }

        // Daten laden
        PlayerData data = plugin.getStorageManager().loadPlayerData(player.getName());

        if (data == null) {
            plugin.getMessageManager().send(player, "restore.error-loading");
            return true;
        }

        // Mit Scheduler verzögern für Stabilität
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {

            plugin.getMessageManager().sendVerbose("verbose.inventory-restore-start");

            // Inventar leeren
            plugin.getMessageManager().sendVerbose("verbose.clearing-inventory");
            player.getInventory().clear();

            // Rüstung wiederherstellen
            plugin.getMessageManager().sendVerbose("verbose.restoring-armor");
            ItemStack[] armor = data.getArmor();
            plugin.getMessageManager().sendVerbose("verbose.armor-helmet", "type", (armor[3] != null ? armor[3].getType().toString() : "NULL"));
            plugin.getMessageManager().sendVerbose("verbose.armor-chestplate", "type", (armor[2] != null ? armor[2].getType().toString() : "NULL"));
            plugin.getMessageManager().sendVerbose("verbose.armor-leggings", "type", (armor[1] != null ? armor[1].getType().toString() : "NULL"));
            plugin.getMessageManager().sendVerbose("verbose.armor-boots", "type", (armor[0] != null ? armor[0].getType().toString() : "NULL"));
            player.getInventory().setArmorContents(armor);

            // Offhand wiederherstellen
            plugin.getMessageManager().sendVerbose("verbose.restoring-offhand");
            ItemStack offHand = data.getOffHand();
            plugin.getMessageManager().sendVerbose("verbose.offhand-item", "type", (offHand != null ? offHand.getType().toString() : "NULL"));
            player.getInventory().setItemInOffHand(offHand);

            // Inventar wiederherstellen
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
            plugin.getMessageManager().sendVerbose("verbose.xp-level", "level", String.valueOf(data.getLevel()));
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

            // Erfolgsnachricht aus messages.yml
            plugin.getMessageManager().sendList(player, "restore.success");

            plugin.getLogger().info(plugin.getMessageManager().getMessage("logs.inventory-restored",
                    "player", player.getName()));

            plugin.getMessageManager().sendVerbose("verbose.effects-count", "count", String.valueOf(data.getPotionEffects().size()));

        }, 1L);

        return true;
    }
}