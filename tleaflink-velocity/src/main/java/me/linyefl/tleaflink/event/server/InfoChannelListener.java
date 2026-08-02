package me.linyefl.tleaflink.event.server;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import me.linyefl.tleaflink.TLeafLink;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * velocity 端：接收子服（bukkit）上报的死亡/成就事件，转发到 QQ/KOOK。
 */
public class InfoChannelListener {

    public static final String CHANNEL_INFO = "tleaflink:info"; // 子服 -> 代理
    public static final String CHANNEL_HELLO = "tleaflink:hello"; // 代理 -> 子服

    private final TLeafLink plugin;

    public InfoChannelListener(TLeafLink plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        // 关键：只处理来自子服的插件消息（ServerConnection），玩家客户端的消息忽略
        if (!(event.getSource() instanceof ServerConnection)) {
            return;
        }
        if (!event.getIdentifier().getId().equals(CHANNEL_INFO)) {
            return;
        }
        // 消息已被消费，不再转发给玩家客户端
        event.setResult(PluginMessageEvent.ForwardResult.handled());

        ServerConnection connection = (ServerConnection) event.getSource();
        String serverName = connection.getServerInfo().getName();

        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
            String type = in.readUTF();
            String content = in.readUTF();
            forward(serverName, type, content);
        } catch (IOException e) {
            plugin.getLogger().warn("解析子服上报消息失败: {}", e.getMessage());
        }
    }

    private void forward(String serverName, String type, String content) {
        String displayName = ServerEvent.getServerDisplayName(serverName);
        // bukkit 端新格式：玩家名 + \u0000 + 内容（成就为中文译名，死亡为去掉玩家名的描述）
        String[] parts = content.split("\u0000", 2);
        String message;
        if (parts.length < 2) {
            // 兼容旧格式上报
            String label = "death".equals(type) ? "死亡" : ("advancement".equals(type) ? "进度" : type);
            message = ServerEvent.format("[{label}] {content}（{server}）",
                    "{label}", label, "{content}", content, "{server}", displayName);
        } else if ("death".equals(type)) {
            message = "[死亡] " + displayName + "中 " + parts[0] + " \"" + parts[1] + "\"";
        } else if ("advancement".equals(type)) {
            message = "[进度] " + parts[0] + " 在" + displayName + "获得了成就 " + parts[1];
        } else {
            message = ServerEvent.format("[{label}] {content}（{server}）",
                    "{label}", type, "{content}", content, "{server}", displayName);
        }
        // 复用通知转发（受各群 /通知 开关控制）
        ServerEvent.sendNotificationToAllPlatforms(message);
    }

    /** 每 30 秒向所有子服广播握手，bukkit 端据此判断连接状态 */
    public void startHelloTask() {
        ChannelIdentifier hello = new LegacyChannelIdentifier(CHANNEL_HELLO);
        plugin.getServer().getScheduler().buildTask(plugin, () -> {
            byte[] data = new byte[0];
            for (RegisteredServer target : plugin.getServer().getAllServers()) {
                target.sendPluginMessage(hello, data);
            }
        }).repeat(30L, TimeUnit.SECONDS).schedule();
    }
}