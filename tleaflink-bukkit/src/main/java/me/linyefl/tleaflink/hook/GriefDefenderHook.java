package me.linyefl.tleaflink.hook;

import me.linyefl.tleaflink.TLeafLink;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class GriefDefenderHook {


    public static Boolean hasGriefDefender;

    public static void hookGriefDefender() {

        Plugin authMe = Bukkit.getPluginManager().getPlugin("GriefDefender");
        try {
            if (authMe != null) {
                hasGriefDefender = true;
                TLeafLink.INSTANCE.getLogger().info("GriefDefender 关联成功");
            }else{
                hasGriefDefender = false;
                TLeafLink.INSTANCE.getLogger().info("GriefDefender 关联失败");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
