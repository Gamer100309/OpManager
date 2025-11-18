package RedCity_Industries.opmanager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {

    private final OpManagerPlugin plugin;

    public DeathListener(OpManagerPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        // Prüfen ob Spieler eine aktive OP-Session hat (= gespeicherte Daten existieren)
        if (plugin.getStorageManager().hasPlayerData(player.getName())) {

            // Warnung an Spieler (Items werden NICHT gelöscht!)
            plugin.getMessageManager().sendList(player, "death.warning",
                    "world", event.getEntity().getLocation().getWorld().getName(),
                    "x", String.valueOf(event.getEntity().getLocation().getBlockX()),
                    "y", String.valueOf(event.getEntity().getLocation().getBlockY()),
                    "z", String.valueOf(event.getEntity().getLocation().getBlockZ())
            );
        }
    }
}