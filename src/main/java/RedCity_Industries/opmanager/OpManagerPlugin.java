package RedCity_Industries.opmanager;

import org.bukkit.plugin.java.JavaPlugin;

public class OpManagerPlugin extends JavaPlugin {

    // StorageManager für Daten-Speicherung
    private StorageManager storageManager;

    // MessageManager für Übersetzungen
    private MessageManager messageManager;

    @Override
    public void onEnable() {
        // Config laden
        saveDefaultConfig();

        // MessageManager initialisieren
        messageManager = new MessageManager(this);

        // Startup-Nachricht
        getLogger().info(messageManager.getMessage("plugin.loading"));

        // StorageManager initialisieren
        storageManager = new StorageManager(this);

        // Hinweis zur Datenspeicherung
        String strategy = getConfig().getString("storage_strategy", "DISK_AND_MEMORY");
        if (strategy.equalsIgnoreCase("DISK_ONLY") || strategy.equalsIgnoreCase("DISK_AND_MEMORY")) {
            // Liste von Nachrichten ausgeben
            for (String message : messageManager.getMessageList("plugin.disk-storage-info")) {
                getLogger().info(message);
            }
        }

        // Commands registrieren
        getCommand("opon").setExecutor(new OpOnCommand(this));
        getCommand("opoff").setExecutor(new OpOffCommand(this));
        getCommand("opreturnback").setExecutor(new OpReturnCommand(this));
        getCommand("opRestoreInventory").setExecutor(new OpRestoreInventoryCommand(this));

        // Event Listener registrieren
        getServer().getPluginManager().registerEvents(new DeathListener(this), this);

        // Erfolgsnachricht
        getLogger().info(messageManager.getMessage("plugin.started"));
    }

    @Override
    public void onDisable() {
        getLogger().info(messageManager.getMessage("plugin.stopping"));
    }

    // Getter-Methode für StorageManager
    public StorageManager getStorageManager() {
        return storageManager;
    }

    // Getter-Methode für MessageManager
    public MessageManager getMessageManager() {
        return messageManager;
    }
}