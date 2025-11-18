package RedCity_Industries.opmanager;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class MessageManager {

    private final OpManagerPlugin plugin;
    private YamlConfiguration messages;
    private String language;

    public MessageManager(OpManagerPlugin plugin) {
        this.plugin = plugin;
        loadMessages();
    }

    // Messages laden
    public void loadMessages() {
        // Sprache aus Config lesen
        language = plugin.getConfig().getString("language", "de");

        // Dateiname erstellen
        String fileName = "messages_" + language + ".yml";
        File messagesFile = new File(plugin.getDataFolder(), fileName);

        // Wenn Datei nicht existiert, aus JAR kopieren
        if (!messagesFile.exists()) {
            plugin.saveResource(fileName, false);
        }

        // Messages laden
        try {
            messages = YamlConfiguration.loadConfiguration(messagesFile);

            // Defaults laden (falls Keys fehlen)
            InputStream defaultStream = plugin.getResource(fileName);
            if (defaultStream != null) {
                YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                        new InputStreamReader(defaultStream, StandardCharsets.UTF_8)
                );
                messages.setDefaults(defaultConfig);
            }

            plugin.getLogger().info("Messages loaded: " + fileName);

        } catch (Exception e) {
            plugin.getLogger().severe("Failed to load " + fileName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Einzelne Message holen
    public String getMessage(String path) {
        String message = messages.getString(path);
        if (message == null) {
            plugin.getLogger().warning("Missing message: " + path);
            return "§cMissing message: " + path;
        }
        return message;
    }

    // Message mit Platzhaltern holen
    public String getMessage(String path, String... replacements) {
        String message = getMessage(path);

        // Platzhalter ersetzen (Paare: key, value)
        for (int i = 0; i < replacements.length - 1; i += 2) {
            String placeholder = "{" + replacements[i] + "}";
            String value = replacements[i + 1];
            message = message.replace(placeholder, value);
        }

        return message;
    }

    // Liste von Messages holen
    public List<String> getMessageList(String path) {
        List<String> messageList = messages.getStringList(path);
        if (messageList.isEmpty()) {
            plugin.getLogger().warning("Missing message list: " + path);
            return List.of("§cMissing message list: " + path);
        }
        return messageList;
    }

    // Liste von Messages mit Platzhaltern holen
    public List<String> getMessageList(String path, String... replacements) {
        List<String> messageList = getMessageList(path);

        // Platzhalter in allen Messages ersetzen
        return messageList.stream()
                .map(message -> {
                    String result = message;
                    for (int i = 0; i < replacements.length - 1; i += 2) {
                        String placeholder = "{" + replacements[i] + "}";
                        String value = replacements[i + 1];
                        result = result.replace(placeholder, value);
                    }
                    return result;
                })
                .collect(Collectors.toList());
    }

    // Message an Spieler senden
    public void send(Player player, String path) {
        player.sendMessage(getMessage(path));
    }

    // Message mit Platzhaltern an Spieler senden
    public void send(Player player, String path, String... replacements) {
        player.sendMessage(getMessage(path, replacements));
    }

    // Liste von Messages an Spieler senden
    public void sendList(Player player, String path) {
        getMessageList(path).forEach(player::sendMessage);
    }

    // Liste von Messages mit Platzhaltern an Spieler senden
    public void sendList(Player player, String path, String... replacements) {
        getMessageList(path, replacements).forEach(player::sendMessage);
    }

    // Reload Messages
    public void reload() {
        loadMessages();
    }

    // Verbose-Message an Console senden (nur wenn verbose_logging aktiviert)
    public void sendVerbose(String path, String... replacements) {
        if (plugin.getConfig().getBoolean("verbose_logging", false)) {
            plugin.getLogger().info(getMessage(path, replacements));
        }
    }
}