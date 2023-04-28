package lol.hyper.paperupdatervelocity.events;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.proxy.Player;
import lol.hyper.paperupdatervelocity.VelocityUpdater;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class PlayerJoin {

    private final VelocityUpdater velocityUpdater;

    public PlayerJoin(VelocityUpdater velocityUpdater) {
        this.velocityUpdater = velocityUpdater;
    }

    @SuppressWarnings("UnstableApiUsage")
    @Subscribe(order = PostOrder.FIRST)
    public void onPlayerLogin(ServerPostConnectEvent event) {
        // player is joining the proxy
        if (event.getPreviousServer() != null) {
            return;
        }

        Player player = event.getPlayer();
        if (!player.hasPermission("paperupdater.message")) {
            return;
        }

        int latestVelocityBuild = velocityUpdater.velocityPlugin.getLatestBuild();
        int buildsBehind = velocityUpdater.velocityPlugin.getBuildsBehind();
        if (velocityUpdater.buildNumber < latestVelocityBuild) {
            player.sendMessage(Component.text("Your Velocity version is outdated. The latest version is " + latestVelocityBuild + ".").color(NamedTextColor.YELLOW));
            player.sendMessage(Component.text("You are currently " + buildsBehind + " builds behind.").color(NamedTextColor.YELLOW));
        }
    }
}
