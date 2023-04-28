package lol.hyper.paperupdaterpaper.events;

import lol.hyper.paperupdaterpaper.PaperUpdater;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {

    private final PaperUpdater paperUpdater;

    public PlayerJoin(PaperUpdater paperUpdater) {
        this.paperUpdater = paperUpdater;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("paperupdater.message")) {
            return;
        }

        int latestPaperBuild = paperUpdater.paperPlugin.getLatestBuild();
        int buildsBehind = paperUpdater.paperPlugin.getBuildsBehind();
        if (paperUpdater.buildNumber < latestPaperBuild) {
            player.sendMessage(Component.text("Your Paper version is outdated. The latest version is " + latestPaperBuild + ".").color(NamedTextColor.YELLOW));
            player.sendMessage(Component.text("You are currently " + buildsBehind + " builds behind.").color(NamedTextColor.YELLOW));
        }
    }
}
