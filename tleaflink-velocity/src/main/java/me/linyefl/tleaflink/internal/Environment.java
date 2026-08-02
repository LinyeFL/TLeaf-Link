package me.linyefl.tleaflink.internal;

import me.linyefl.tleaflink.TLeafLink;

import java.util.Iterator;

public final class Environment {
    private final TLeafLink plugin = TLeafLink.INSTANCE;
    public final String name = plugin.getPluginContainer().getDescription().getName().get();
    public final String version = plugin.getPluginContainer().getDescription().getVersion().get();
    public final String author = getAuthorString();


    private String getAuthorString() {
        Iterator<String> authors = plugin.getPluginContainer().getDescription().getAuthors().iterator();
        StringBuilder stringBuilder = new StringBuilder();
        while(authors.hasNext()){
            stringBuilder.append(authors.next()).append(", ");
        }
        return stringBuilder.substring(0, stringBuilder.lastIndexOf(",")-1);
    }
}
