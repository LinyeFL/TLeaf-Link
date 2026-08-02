package me.linyefl.tleaflink.hook;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.chat.ChatManager;
import me.linyefl.tleaflink.TLeafLink;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class ResidenceHook {

    public static Boolean hasRes;

    public static ChatManager resChatApi;

    public static void hookRes() {

        Plugin residence = Bukkit.getPluginManager().getPlugin("Residence");
        try {
            if (residence != null) {
                hasRes = true;
                resChatApi = Residence.getInstance().getChatManager();
                TLeafLink.INSTANCE.getLogger().info("Residence 关联成功");
            }else{
                hasRes = false;
                TLeafLink.INSTANCE.getLogger().info("Residence 关联失败");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}

