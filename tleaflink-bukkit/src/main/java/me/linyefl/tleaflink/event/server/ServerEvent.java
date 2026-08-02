package me.linyefl.tleaflink.event.server;

import me.linyefl.tleaflink.TLeafLink;
import me.linyefl.tleaflink.config.Args;
import me.linyefl.tleaflink.config.Config;
import me.linyefl.tleaflink.config.DataBase;
import me.linyefl.tleaflink.hook.AuthMeHook;
import me.linyefl.tleaflink.hook.GriefDefenderHook;
import me.linyefl.tleaflink.hook.QuickShopHook;
import me.linyefl.tleaflink.hook.ResidenceHook;
import me.linyefl.tleaflink.internal.database.DatabaseManager;
import me.linyefl.tleaflink.tool.StringTool;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerEvent implements Listener{

    private TLeafLink plugin;

    public ServerEvent(TLeafLink plugin){
        this.plugin=plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {

        Pattern pattern;
        Matcher matcher;

        if (!Config.Forwarding()){
            return;
        }
        String name = StringTool.filterColor(event.getPlayer().getDisplayName());
        String message = StringTool.filterColor(event.getMessage());
        if (AuthMeHook.hasAuthMe) {if (!AuthMeHook.authMeApi.isAuthenticated(event.getPlayer())) {return;} }
        if (ResidenceHook.hasRes) {if (ResidenceHook.resChatApi.getPlayerChannel(event.getPlayer().getName()) != null) {return;}}
        if (QuickShopHook.hasQs) {if (event.getPlayer() == QsChatEvent.getQsSender() && event.getMessage() == QsChatEvent.getQsMessage()) {return;}}
        if (QuickShopHook.hasQsHikari) {if (event.getPlayer() == QsHikariChatEvent.getQsSender() && event.getMessage() == QsHikariChatEvent.getQsMessage()) {return;}}
        if (GriefDefenderHook.hasGriefDefender) {if (GDClaimEvent.getGDMessage() == event.getMessage()){return;}}
        if (Args.ForwardingMode() == 1) {
            pattern = Pattern.compile(Args.ForwardingPrefix()+".*");
            matcher = pattern.matcher(message);
            if(!matcher.find()){
                return;
            }
            String fmsg = matcher.group().replaceAll(Args.ForwardingPrefix(), "");
            List<Long> groups = Config.getGroupQQs();
            for (long groupID : groups){
                TLeafLink.getBot().sendMsg(true, "[服务器]"+name+":"+fmsg,groupID);
            }
            return;
        }

        List<Long> groups = Config.getGroupQQs();
        for (long groupID : groups){
            TLeafLink.getBot().sendMsg(true, "[服务器]"+name+":"+message,groupID);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPreLogin(AsyncPlayerPreLoginEvent event){
        String name = event.getName();

        if (Config.WhiteList()) {
            TLeafLink.getScheduler().runTaskAsynchronously(() -> {
                long qq;
                qq = (DatabaseManager.getBindId(name, DataBase.type().toLowerCase(), TLeafLink.getDatabase()));
                if (qq == 0L) {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, Args.WhitelistKick());
                    List<Long> groups = Config.getGroupQQs();
                    for (long groupID : groups) {
                        TLeafLink.getBot().sendMsg(true, "玩家" + name + "因为未在白名单中被踢出", groupID);
                    }
                    return;
                }
                for (long groupID : Config.getGroupQQs()) {
                    if(!TLeafLink.getBot().checkUserInGroup(qq, groupID)){
                        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, Args.WhitelistKick());
                        List<Long> groups = Config.getGroupQQs();
                        for (long group : groups) {
                            TLeafLink.getBot().sendMsg(true, "玩家" + name + "因为未在白名单中被踢出", group);
                        }
                        DatabaseManager.removeBind(String.valueOf(qq), DataBase.type().toLowerCase(), TLeafLink.getDatabase());
                        return;
                    }
                }
                event.allow();
            });
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event){

        String name = event.getPlayer().getName();

        if (!Config.JoinAndLeave()){
            return;
        }
        List<Long> groups = Config.getGroupQQs();
        for (long groupID : groups){
            TLeafLink.getBot().sendMsg(true, "玩家"+name+"加入游戏",groupID);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){

        String name = StringTool.filterColor(event.getPlayer().getDisplayName());

        if (!Config.JoinAndLeave()){
            return;
        }
        List<Long> groups = Config.getGroupQQs();
        for (long groupID : groups){
            TLeafLink.getBot().sendMsg(true, "玩家"+name+"退出游戏",groupID);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        if(!Config.DieReport()){
            return;
        }
        Player player=event.getEntity();
        String name= player.getName();
        Location location=player.getLocation();
        int x= (int) location.getX();
        int y= (int) location.getY();
        int z= (int) location.getZ();
        String msg = "死在了"+location.getWorld().getName()+"世界"+"("+x+","+y+","+z+")";
        ServerManager.sendCmd("msg "+name+" "+msg, false);
        List<Long> groups = Config.getGroupQQs();
        for (long groupID : groups){
            TLeafLink.getBot().sendMsg(true, "玩家"+name+msg,groupID);
        }
    }
}
