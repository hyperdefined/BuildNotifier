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

package lol.hyper.buildnotifier.paper.events;

import lol.hyper.buildnotifier.paper.BuildNotifierPaper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {

    private final BuildNotifierPaper buildNotifierPaper;

    public PlayerJoin(BuildNotifierPaper buildNotifierPaper) {
        this.buildNotifierPaper = buildNotifierPaper;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("buildnotifier.message")) {
            return;
        }

        int latestPaperBuild = buildNotifierPaper.paperPlugin.getLatestBuild();
        int buildsBehind = buildNotifierPaper.paperPlugin.getBuildsBehind();
        if (buildNotifierPaper.buildNumber < latestPaperBuild) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(buildNotifierPaper, () -> {
                player.sendMessage(Component.text("Your Paper version is outdated. The latest build is " + latestPaperBuild + ".").color(NamedTextColor.YELLOW));
                player.sendMessage(Component.text("You are currently " + buildsBehind + " build(s) behind.").color(NamedTextColor.YELLOW));
            }, 200);
        }
    }
}
