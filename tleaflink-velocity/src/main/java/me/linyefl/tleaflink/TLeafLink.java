package me.linyefl.tleaflink;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import me.linyefl.tleaflink.bot.Bot;
import me.linyefl.tleaflink.bot.KookBot;
import me.linyefl.tleaflink.bot.QQBot;
import me.linyefl.tleaflink.command.Commands;
import me.linyefl.tleaflink.command.QqReplyCommand;
import me.linyefl.tleaflink.config.VelocityConfig;
import me.linyefl.tleaflink.event.server.InfoChannelListener;
import me.linyefl.tleaflink.event.server.ServerEvent;
import me.linyefl.tleaflink.internal.Config;
import me.linyefl.tleaflink.internal.Dependencies;
import me.linyefl.tleaflink.internal.Environment;
import me.linyefl.tleaflink.internal.database.Database;
import me.linyefl.tleaflink.internal.database.DatabaseManager;
import me.linyefl.tleaflink.internal.maven.LibraryLoader;
import me.linyefl.tleaflink.metrics.Metrics;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Path;

@Plugin(id = "tleaflink", name = "TLeaf-Link", version = "@version@",
        url = "https://github.com/LinyeFL/TLeaf-Link", description = "TLeaf-Link 跨服聊天机器人", authors = {"LinyeFL"})
public class TLeafLink {

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private PluginContainer pluginContainer;
    private final Metrics.Factory metricsFactory;
    public VelocityConfig vconf;
    private static Bot bot;
    private static QQBot qqBot;
    private static KookBot kookBot;
    private static Environment environment;

    public static TLeafLink INSTANCE;
    private static Database database;

    @Inject
    public TLeafLink(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory, Metrics.Factory metricsFactory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.metricsFactory = metricsFactory;

        INSTANCE = this;

        try {
            vconf = new VelocityConfig(this);
            vconf.loadConfig();
            logger.info("配置文件获取成功");
        }catch (Exception e) {
            getLogger().warn("An error occurred while loading plugin.");
            e.printStackTrace();
        }

        LibraryLoader.loadAll(Dependencies.class);
        logger.info("TLeaf-Link 依赖已加载完成");

        logger.info("TLeaf-Link 加载完成");
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        DatabaseManager.start();
        server.getEventManager().register(this, new ServerEvent());
        logger.info("服务器事件监听器注册成功");
        // 注册插件消息通道 + 事件监听器（子服死亡/成就上报）
        server.getChannelRegistrar().register(
                server.getChannelRegistrar().getOrCreate(InfoChannelListener.CHANNEL_INFO)
        );
        InfoChannelListener infoListener = new InfoChannelListener(this);
        server.getEventManager().register(this, infoListener);
        infoListener.startHelloTask();
        logger.info("插件消息通道监听器注册成功");
        CommandManager manager = server.getCommandManager();
        CommandMeta linearbot = manager.metaBuilder("tleaflink").aliases("tll").build();
        manager.register(linearbot, new Commands(this));
        logger.info("插件命令监听器注册成功");
        CommandMeta qqReply = manager.metaBuilder("qqreply").build();
        manager.register(qqReply, new QqReplyCommand());

        pluginContainer = server.getPluginManager().fromInstance(this).orElseThrow(
                () -> new IllegalArgumentException("The provided instance is not a plugin"));

        getServer().getScheduler().buildTask(this, () -> {
            String mode = Config.bot.Bot.Mode;
            boolean qqMode = mode.equals("go-cqhttp") || mode.equals("both");
            boolean kookMode = mode.equals("kook") || mode.equals("both");

            if (qqMode) {
                qqBot = new QQBot(INSTANCE);
                qqBot.start();
                bot = qqBot;
                getLogger().info("已启动go-cqhttp服务");
            }
            if (kookMode) {
                kookBot = new KookBot(this);
                kookBot.start();
                if (bot == null) bot = kookBot;
                getLogger().info("已启动kook服务");
            }
            if (!qqMode && !kookMode) {
                getLogger().warn("无法启动服务，请检查配置文件");
            }
        }).schedule();

        int pluginId = 19428;
        Metrics metrics = metricsFactory.make(this, pluginId);
        metrics.addCustomChart(new Metrics.SimplePie("chart_id", () -> "value"));

        environment = new Environment();
        logger.info("TLeaf-Link 已启动");

    }

    @Subscribe(order = PostOrder.FIRST)
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if (qqBot != null) {
            qqBot.shutdown();
            getLogger().info("已关闭go-cqhttp服务");
        }
        if (kookBot != null) {
            kookBot.shutdown();
            getLogger().info("已关闭kook服务");
        }
        if (qqBot == null && kookBot == null) {
            getLogger().warn("无法正常关闭服务，将在服务器关闭后强制关闭");
        }
        DatabaseManager.close();
        getLogger().info("TLeaf-Link 已关闭");
    }

    public Logger getLogger() {
        return logger;
    }

    public File getDataFolder() {
        return dataDirectory.toFile();
    }
    public ProxyServer getServer() {
        return server;
    }

    public PluginContainer getPluginContainer(){
        return pluginContainer;
    }

    public static Bot getBot() {
        return bot;
    }

    public static QQBot getQQBot() {
        return qqBot;
    }

    public static KookBot getKookBot() {
        return kookBot;
    }

    public static Database getDatabase() {
        return database;
    }
    public void setDatabase(Database database) {
        TLeafLink.database = database;
    }
    public Environment getEnvironment() {return environment;}
}