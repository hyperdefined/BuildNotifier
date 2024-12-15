/*
 * This file is part of BuildNotifier.
 *
 * BuildNotifier is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BuildNotifier is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BuildNotifier.  If not, see <https://www.gnu.org/licenses/>.
 */

package lol.hyper.buildnotifier.velocity.events;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.proxy.Player;
import lol.hyper.buildnotifier.velocity.BuildNotifierVelocity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.concurrent.TimeUnit;

public class PlayerJoin {

    private final BuildNotifierVelocity buildNotifierVelocity;

    public PlayerJoin(BuildNotifierVelocity buildNotifierVelocity) {
        this.buildNotifierVelocity = buildNotifierVelocity;
    }

    @SuppressWarnings("UnstableApiUsage")
    @Subscribe
    public void onPlayerLogin(ServerPostConnectEvent event) {

        // player is joining the proxy
        if (event.getPreviousServer() != null) {
            return;
        }

        Player player = event.getPlayer();
        if (!player.hasPermission("buildnotifier.message")) {
            return;
        }

        int latestVelocityBuild = buildNotifierVelocity.velocityHelper.getLatestBuild();
        int buildsBehind = buildNotifierVelocity.velocityHelper.getBuildsBehind();
        if (buildNotifierVelocity.buildNumber < latestVelocityBuild) {
            buildNotifierVelocity.server.getScheduler().buildTask(buildNotifierVelocity, (task) -> {
                player.sendMessage(Component.text("Your Velocity version is outdated. The latest version is " + latestVelocityBuild + ".", NamedTextColor.YELLOW));
                player.sendMessage(Component.text("You are currently " + buildsBehind + " builds behind.", NamedTextColor.YELLOW));
            }).delay(10, TimeUnit.SECONDS).schedule();
        }
    }
}
