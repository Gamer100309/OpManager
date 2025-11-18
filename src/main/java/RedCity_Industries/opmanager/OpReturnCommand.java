package RedCity_Industries.opmanager;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpReturnCommand implements CommandExecutor {

    private final OpManagerPlugin plugin;

    public OpReturnCommand(OpManagerPlugin plugin) {
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

        // Prüfen ob Spieler berechtigt ist (OP ODER auf Whitelist)
        if (!player.isOp()) {
            // Wenn kein OP, prüfen ob auf Whitelist
            if (plugin.getConfig().getBoolean("whitelist_enabled")) {
                if (!plugin.getConfig().getStringList("whitelist").contains(player.getName())) {
                    plugin.getMessageManager().send(player, "returnback.no-permission-whitelist");
                    return true;
                }
            }
            // Hat kein OP, aber ist auf Whitelist (oder Whitelist deaktiviert) - erlauben!
        }

        // Prüfen ob, gespeicherte Daten existieren
        if (!plugin.getStorageManager().hasPlayerData(player.getName())) {
            plugin.getMessageManager().sendList(player, "returnback.no-data");
            return true;
        }

        // Daten laden
        PlayerData data = plugin.getStorageManager().loadPlayerData(player.getName());

        if (data == null) {
            plugin.getMessageManager().send(player, "returnback.error-loading");
            return true;
        }

        // Nur zur gespeicherten Position teleportieren
        Location loc = data.getLocation();

        plugin.getMessageManager().sendVerbose("verbose.returnback-teleporting", "player", player.getName());
        plugin.getMessageManager().sendVerbose("verbose.returnback-world", "world", loc.getWorld().getName());
        plugin.getMessageManager().sendVerbose("verbose.returnback-position",
                "x", String.valueOf(loc.getBlockX()),
                "y", String.valueOf(loc.getBlockY()),
                "z", String.valueOf(loc.getBlockZ()));

        player.teleport(loc);

        plugin.getMessageManager().sendList(player, "returnback.success",
                "world", loc.getWorld().getName(),
                "x", String.valueOf(loc.getBlockX()),
                "y", String.valueOf(loc.getBlockY()),
                "z", String.valueOf(loc.getBlockZ())
        );

        plugin.getLogger().info(plugin.getMessageManager().getMessage("logs.returned",
                "player", player.getName()));

        return true;
    }
}