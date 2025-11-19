# OpManager

![Version](https://img.shields.io/badge/version-4.0-blue)
![Minecraft](https://img.shields.io/badge/minecraft-1.21-green)
![License](https://img.shields.io/badge/license-GPL%20v3-blue)

**A safe and lightweight OP management plugin that saves and restores player data when granting temporary operator permissions.**

Created by **RedCity Industries | Gamer100309**

---

## 📖 Description

OpManager is a Minecraft plugin designed for server administrators who need to temporarily grant themselves OP permissions without losing their current gameplay progress. Unlike traditional OP commands, OpManager saves your inventory, position, gamemode, and more before granting OP, then restores everything when you're done.

Perfect for:
- 🛠️ Server maintenance without losing your survival progress
- 🎮 Switching between admin and player modes seamlessly
- 🔒 Secure temporary OP access with whitelist support
- 💾 Multiple storage strategies for different server needs

---

## ✨ Features

### Core Features
- **🎒 Complete Data Backup**: Saves inventory, armor, offhand, position, gamemode, XP, health, hunger, and potion effects
- **🔄 One-Click Restore**: Automatically restores everything when you remove OP
- **📍 Return Command**: Teleport back to your saved position without removing OP
- **🎯 Inventory Recovery**: Restore only your inventory while keeping OP active
- **🔒 Whitelist System**: Control who can use the plugin
- **🌐 Multi-Language**: Built-in English and German support (easily expandable)
- **💾 Flexible Storage**: Choose between RAM-only, disk-only, or hybrid storage
- **👻 Vanish Integration**: Optional automatic vanish disable on /opoff

### Safety Features
- **⚠️ Item Duplication Warning**: Alerts players about potential duplication if they die during OP sessions
- **🔍 Verbose Logging**: Detailed debug mode for troubleshooting
- **📁 Automatic Cleanup**: Temporary files are deleted after /opoff
- **🛡️ Data Integrity**: Deep cloning of items prevents unintended modifications

---

## 📦 Installation

1. Download the latest `OpManager.jar` from [Releases](https://github.com/Gamer100309/OpManager/releases)
2. Place the file in your server's `plugins/` folder
3. Restart your server
4. Configure `plugins/OpManager/config.yml` to your needs
5. Add trusted players to the whitelist
6. Reload with `/reload` or restart the server

---

## 🎮 Commands

| Command | Description | Aliases |
|---------|-------------|---------|
| `/opon` | Grants OP and saves your current player data | `/op-on`, `/enableop` |
| `/opoff` | Removes OP and restores your saved player data | `/op-off`, `/disableop` |
| `/opreturnback` | Teleports you back to your saved position (keeps OP) | `/opreturn`, `/opback` |
| `/opRestoreInventory` | Restores your saved inventory without removing OP | `/oprestore`, `/opinv` |

---

## ⚙️ Configuration

### Quick Setup

```yaml
# Set your language
language: en  # or 'de' for German

# Enable/disable inventory saving
inventory_save: true  # false = only saves position & gamemode

# Whitelist protection
whitelist_enabled: true
whitelist:
  - YourUsername
  - TrustedAdmin

# Storage strategy
storage_strategy: DISK_AND_MEMORY  # MEMORY_ONLY, DISK_ONLY, or DISK_AND_MEMORY
```

### Storage Strategies

| Strategy | Speed | Data Loss Risk | Use Case |
|----------|-------|----------------|----------|
| `MEMORY_ONLY` | ⚡ Fastest | ⚠️ High (crashes, restarts) | Testing only |
| `DISK_ONLY` | 🐌 Slower | ✅ Low | Servers with frequent restarts |
| `DISK_AND_MEMORY` | ⚡ Fast | ✅ Very Low | **Recommended** for most servers |

### Vanish Integration

```yaml
# WARNING: Only enable if your vanish plugin has a dedicated "OFF" command
auto_disable_vanish: false
vanish_disable_command: "vanish off {player}"
```

⚠️ **Important**: If your vanish plugin uses a **toggle** command (like `/vanish` or `/v`), leave `auto_disable_vanish` set to `false` to avoid accidentally enabling vanish when you meant to disable it!

---

## 🌐 Multi-Language Support

OpManager supports multiple languages out of the box:
- 🇬🇧 English (`messages_en.yml`)
- 🇩🇪 German (`messages_de.yml`)

### Adding Your Own Language

1. Copy `messages_en.yml` or `messages_de.yml`
2. Rename it to `messages_<code>.yml` (e.g., `messages_fr.yml` for French)
3. Translate all messages in the file
4. Set `language: fr` in `config.yml`
5. Reload the plugin

---

## 🔒 Security & Permissions

### Whitelist System

OpManager uses a **whitelist-based permission system**:

```yaml
whitelist_enabled: true  # Set to false to allow ALL players (not recommended!)
whitelist:
  - AdminName
  - TrustedModerator
```

⚠️ **Security Warning**: If `whitelist_enabled: false`, **ANY player** can grant themselves OP!

### Best Practices

1. ✅ Always keep `whitelist_enabled: true` in production
2. ✅ Only add trusted administrators to the whitelist
3. ✅ Regularly review your whitelist
4. ✅ Use `DISK_AND_MEMORY` storage for data safety

---

## ⚠️ Important Warnings

### Item Duplication Risk

If you **die during an active OP session**, your items will drop at your death location. When you use `/opoff`, your saved items will be restored. This means you could pick up the dropped items AND have your saved items, resulting in **item duplication**!

**To prevent duplication:**
1. Use `/opoff` FIRST (restores your saved items)
2. Then decide: ignore the dropped items or pick them up

**The plugin will warn you in chat if you die during an OP session.**

### Vanish Toggle Warning

If your vanish plugin uses a **toggle command** (one command to turn on/off), setting `auto_disable_vanish: true` could accidentally **enable** vanish instead of disabling it!

**Safe commands** (dedicated OFF):
- `vanish off {player}` ✅
- `sv off {player}` ✅

**Risky commands** (toggle):
- `vanish {player}` ❌
- `v {player}` ❌

---

## 💾 Performance & Resource Usage

### RAM Usage

OpManager is **extremely lightweight**:

| Scenario | RAM Usage |
|----------|-----------|
| 1 player with `/opon` | ~40 KB (worst case) |
| 10 players simultaneously | ~400 KB |
| 100 players simultaneously | ~4 MB |

**In practice**, most sessions use only **10-20 KB** per player due to partially filled inventories and fewer enchantments.

### Comparison

- **OpManager (1 player)**: 40 KB
- One loaded Minecraft chunk: ~200 KB
- Typical Minecraft server: 2-8 GB

**Conclusion**: OpManager's memory footprint is negligible compared to your server's overall resource usage.

---

## 🐛 Troubleshooting

### Enable Verbose Logging

For detailed debug information, enable verbose logging:

```yaml
verbose_logging: true
```

This will show detailed information about:
- Data saving/loading operations
- Inventory copying steps
- Position restoration
- Storage operations

### Common Issues

**Q: My data wasn't saved!**  
A: Check if you're on the whitelist and if `inventory_save: true` in config.yml

**Q: Items duplicated after death!**  
A: This is expected behavior. Always use `/opoff` before collecting dropped items.

**Q: Vanish turned ON instead of OFF!**  
A: Your vanish plugin uses a toggle command. Set `auto_disable_vanish: false` in config.yml

**Q: Data lost after server crash!**  
A: If using `MEMORY_ONLY` storage, this is expected. Switch to `DISK_AND_MEMORY` for safety.

---

## 📊 Technical Details

### What Gets Saved

When you run `/opon` with `inventory_save: true`:
- ✅ Full inventory (36 slots)
- ✅ Armor (4 slots)
- ✅ Offhand (1 slot)
- ✅ Position (world, x, y, z, yaw, pitch)
- ✅ Gamemode (survival, creative, adventure, spectator)
- ✅ Experience (level and progress)
- ✅ Health
- ✅ Hunger & saturation
- ✅ Potion effects (type, duration, amplifier)

With `inventory_save: false`:
- ✅ Position
- ✅ Gamemode
- ❌ Everything else

### Storage Locations

- **RAM**: Stored in Java HashMap (fastest, volatile)
- **Disk**: `plugins/OpManager/playerdata/<username>.yml` (persistent, auto-deleted after `/opoff`)

---

## 📝 Example Workflow

```
1. You're playing survival: /opon
   → Your survival data is saved
   → You receive OP permissions
   
2. Do admin work in creative mode
   → Build, teleport, use WorldEdit, etc.
   
3. Done with admin tasks: /opoff
   → OP is removed
   → You're teleported back to your survival location
   → Your survival inventory, gamemode, XP, etc. are restored
   → Back to playing survival exactly where you left off!
```

---

## 🤝 Contributing

Contributions are welcome! Feel free to:
- 🐛 Report bugs via [GitHub Issues](https://github.com/Gamer100309/OpManager/issues)
- 💡 Suggest features via [GitHub Issues](https://github.com/Gamer100309/OpManager/issues)
- 🌐 Submit translations via Pull Requests
- 📝 Improve documentation via Pull Requests

**Pull Requests are welcome!** Please make sure to:
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📜 License

This project is licensed under the **GNU General Public License v3.0**.

**What this means:**
- ✅ You can use, modify, and distribute this plugin freely
- ✅ You must share your modifications under GPL v3 as well
- ✅ You must provide source code when distributing
- ❌ You cannot sell this plugin without providing the source code

See the [LICENSE](LICENSE.txt) file for full details or visit https://www.gnu.org/licenses/gpl-3.0.html

---

## 📞 Support

- **Bug Reports & Feature Requests**: [GitHub Issues](https://github.com/Gamer100309/OpManager/issues)
- **Source Code**: [GitHub Repository](https://github.com/Gamer100309/OpManager)

For general questions, feel free to open a discussion on GitHub!

---

## 🎉 Credits

**Created by**: RedCity Industries | Gamer100309  
**Version**: 4.0  
**Minecraft Version**: 1.21+  
**API Version**: 1.21

---

## ⭐ Star History

If you find this plugin useful, please consider giving it a star on [GitHub](https://github.com/Gamer100309/OpManager)!

---

**Made with ❤️ by RedCity Industries**
