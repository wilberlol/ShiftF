package io.github.wilberlol.shiftf;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Shift + F: run a configurable command when a sneaking player presses the
 * swap-hand key, and (by default) cancel the swap so the items stay in place.
 */
public final class ShiftFPlugin extends JavaPlugin implements Listener {

    private String command;
    private boolean runAsConsole;
    private boolean cancelSwap;
    private boolean cancelEvenWhenBlocked;
    private long cooldownMs;
    private String permission;
    private Set<String> worlds = new HashSet<>();
    private boolean worldsIsWhitelist;
    private String cooldownMessage;

    private final Map<UUID, Long> lastUse = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadSettings();
        getServer().getPluginManager().registerEvents(this, this);
    }

    private void loadSettings() {
        reloadConfig();
        FileConfiguration c = getConfig();
        command = c.getString("command", "").trim();
        runAsConsole = "console".equalsIgnoreCase(c.getString("run-as", "player"));
        cancelSwap = c.getBoolean("cancel-swap", true);
        cancelEvenWhenBlocked = c.getBoolean("cancel-swap-even-when-blocked", false);
        cooldownMs = Math.max(0L, c.getLong("cooldown-ms", 0L));
        permission = c.getString("permission", "").trim();
        worldsIsWhitelist = c.getBoolean("worlds-as-whitelist", false);
        worlds = new HashSet<>();
        List<String> list = c.getStringList("worlds");
        for (String w : list) {
            worlds.add(w.toLowerCase(Locale.ROOT));
        }
        cooldownMessage = ChatColor.translateAlternateColorCodes('&', c.getString("cooldown-message", ""));
        if (command.startsWith("/")) {
            command = command.substring(1);
        }
    }

    private boolean worldAllowed(Player p) {
        if (worlds.isEmpty()) {
            return true;
        }
        boolean listed = worlds.contains(p.getWorld().getName().toLowerCase(Locale.ROOT));
        return worldsIsWhitelist == listed;
    }

    /**
     * HIGHEST so that we get the final say after other plugins, and
     * ignoreCancelled=false so we still run when another plugin cancelled it.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSwap(PlayerSwapHandItemsEvent event) {
        Player p = event.getPlayer();
        if (!p.isSneaking()) {
            return; // plain F keeps vanilla behaviour
        }

        boolean allowed = !command.isEmpty()
                && worldAllowed(p)
                && (permission.isEmpty() || p.hasPermission(permission));

        if (!allowed) {
            if (cancelSwap && cancelEvenWhenBlocked) {
                event.setCancelled(true);
            }
            return;
        }

        if (cancelSwap) {
            event.setCancelled(true);
        }

        if (cooldownMs > 0) {
            long now = System.currentTimeMillis();
            Long last = lastUse.get(p.getUniqueId());
            if (last != null && now - last < cooldownMs) {
                if (!cooldownMessage.isEmpty()) {
                    p.sendMessage(cooldownMessage);
                }
                return;
            }
            lastUse.put(p.getUniqueId(), now);
        }

        String cmd = command.replace("%player%", p.getName());
        if (runAsConsole) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        } else {
            p.performCommand(cmd);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        lastUse.remove(event.getPlayer().getUniqueId());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("shiftfreload")) {
            loadSettings();
            sender.sendMessage(ChatColor.GREEN + "[ShiftF] 設定已重新載入 / config reloaded.");
            return true;
        }
        return false;
    }
}
