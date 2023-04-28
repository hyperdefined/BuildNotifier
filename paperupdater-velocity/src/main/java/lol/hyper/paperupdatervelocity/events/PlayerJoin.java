/*
 * This file is part of PaperUpdater.
 *
 * PaperUpdater is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PaperUpdater is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PaperUpdater.  If not, see <https://www.gnu.org/licenses/>.
 */

package lol.hyper.paperupdatervelocity.events;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.proxy.Player;
import lol.hyper.paperupdatervelocity.VelocityUpdater;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.concurrent.TimeUnit;

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
            velocityUpdater.server.getScheduler().buildTask(velocityUpdater, (task) -> {
                player.sendMessage(Component.text("Your Velocity version is outdated. The latest version is " + latestVelocityBuild + ".").color(NamedTextColor.YELLOW));
                player.sendMessage(Component.text("You are currently " + buildsBehind + " builds behind.").color(NamedTextColor.YELLOW));
            }).delay(10, TimeUnit.SECONDS).schedule();
        }
    }
}
