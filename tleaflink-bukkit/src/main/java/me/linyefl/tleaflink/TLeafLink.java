package me.linyefl.tleaflink;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

public final class TLeafLink extends JavaPlugin implements PluginMessageListener {

    // 子服 -> 代理：事件上报通道
    public static final String CHANNEL_INFO = "tleaflink:info";
    // 代理 -> 子服：握手通道
    public static final String CHANNEL_HELLO = "tleaflink:hello";

    public static TLeafLink INSTANCE;

    private boolean linked = false;
    private long lastHelloAt = 0;

    @Override
    public void onEnable() {
        INSTANCE = this;

        // 出站通道：发给 velocity 的事件数据
        getServer().getMessenger().registerOutgoingPluginChannel(this, CHANNEL_INFO);
        // 入站通道：接收 velocity 的握手消息
        getServer().getMessenger().registerIncomingPluginChannel(this, CHANNEL_HELLO, this);

        // 注册事件监听器（死亡、成就上报）
        getServer().getPluginManager().registerEvents(new InfoListener(this), this);

        // 每 5 秒检查一次与 velocity 的连接状态
        getServer().getScheduler().runTaskTimerAsynchronously(this, this::checkLink, 100L, 100L);

        getLogger().info("TLeaf-Link（bukkit 信息收集端）已启动，等待 velocity 连接...");
    }

    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        getServer().getMessenger().unregisterIncomingPluginChannel(this);
        getLogger().info("TLeaf-Link 已关闭");
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals(CHANNEL_HELLO)) {
            return;
        }
        linked = true;
        lastHelloAt = System.currentTimeMillis();
        getLogger().info("已连接到 TLeaf-Link velocity 端");
    }

    private void checkLink() {
        // velocity 每 30 秒发一次握手，超过 90 秒没收到视为断开
        if (linked && System.currentTimeMillis() - lastHelloAt > 90_000) {
            linked = false;
            getLogger().warning("与 TLeaf-Link velocity 端的连接已断开");
        }
    }

    public boolean isLinked() {
        return linked;
    }
}