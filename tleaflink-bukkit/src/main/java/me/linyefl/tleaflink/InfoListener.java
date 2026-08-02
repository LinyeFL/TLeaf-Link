package me.linyefl.tleaflink;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class InfoListener implements Listener {

    private final TLeafLink plugin;

    public InfoListener(TLeafLink plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!plugin.isLinked()) {
            return; // 未连接 velocity 不上报
        }
        Player player = event.getEntity();
        Location loc = player.getLocation();
        String content = player.getName() + " 死在了 "
                + loc.getWorld().getName() + " 世界 ("
                + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + ")";
        send("death", content);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAdvancement(PlayerAdvancementDoneEvent event) {
        if (!plugin.isLinked()) {
            return;
        }
        Player player = event.getPlayer();
        String key = event.getAdvancement().getKey().getKey(); // 形如 story/root
        send("advancement", player.getName() + " 获得了进度 " + key);
    }

    private void send(String type, String content) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bos);
        try {
            out.writeUTF(type);
            out.writeUTF(content);
        } catch (IOException e) {
            return;
        }
        plugin.getServer().sendPluginMessage(plugin, TLeafLink.CHANNEL_INFO, bos.toByteArray());
    }
}