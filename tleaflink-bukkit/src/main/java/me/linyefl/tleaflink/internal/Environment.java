package me.linyefl.tleaflink.internal;

import me.linyefl.tleaflink.TLeafLink;
import me.linyefl.tleaflink.hook.AuthMeHook;
import me.linyefl.tleaflink.hook.GriefDefenderHook;
import me.linyefl.tleaflink.hook.QuickShopHook;
import me.linyefl.tleaflink.hook.ResidenceHook;

import java.util.Iterator;

public final class Environment {
    private final TLeafLink plugin = TLeafLink.INSTANCE;
    public final String name = plugin.getDescription().getName();
    public final String version = plugin.getDescription().getVersion();
    public final String author = getAuthorString();
    public final String authme = getPluginHooked(AuthMeHook.hasAuthMe);
    public final String griefdefender = getPluginHooked(GriefDefenderHook.hasGriefDefender);
    public final String quickshop = getPluginHooked(QuickShopHook.hasQs);
    public final String quickshophikari = getPluginHooked(QuickShopHook.hasQsHikari);
    public final String residence = getPluginHooked(ResidenceHook.hasRes);

    private String getPluginHooked(boolean hooked){
        if (hooked){
            return "§2True";
        } else {
            return "§cFalse";
        }
    }

    private String getAuthorString() {
        Iterator<String> authors = plugin.getDescription().getAuthors().iterator();
        StringBuilder stringBuilder = new StringBuilder();
        while(authors.hasNext()){
            stringBuilder.append(authors.next()).append(", ");
        }
        return stringBuilder.substring(0, stringBuilder.lastIndexOf(",")-1);
    }
}
