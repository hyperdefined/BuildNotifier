package lol.hyper.paperupdaterwaterfall.events;

import lol.hyper.paperupdatercore.WaterfallPlugin;
import lol.hyper.paperupdaterwaterfall.WaterfallUpdater;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerJoin implements Listener {

    private final WaterfallUpdater waterfallUpdater;

    public PlayerJoin(WaterfallUpdater waterfallUpdater) {
        this.waterfallUpdater = waterfallUpdater;
    }

    @EventHandler
    public void onPing(ServerSwitchEvent event) {
        // player is joining the proxy
        if (event.getFrom() != null) {
            return;
        }

        ProxiedPlayer player = event.getPlayer();
        if (!player.hasPermission("paperupdater.message")) {
            return;
        }

        int latestPaperBuild = waterfallUpdater.waterfallPlugin.getLatestBuild();
        int buildsBehind = waterfallUpdater.waterfallPlugin.getBuildsBehind();
        if (waterfallUpdater.buildNumber < latestPaperBuild) {
            BaseComponent[] messageOne = new ComponentBuilder("Your Waterfall version is outdated. The latest build is " + latestPaperBuild + ".").color(ChatColor.YELLOW).create();
            BaseComponent[] messageTwo = new ComponentBuilder("You are currently " + buildsBehind + " build(s) behind.").color(ChatColor.YELLOW).create();
            player.setTabHeader(messageOne, messageTwo);
        }
    }
}
