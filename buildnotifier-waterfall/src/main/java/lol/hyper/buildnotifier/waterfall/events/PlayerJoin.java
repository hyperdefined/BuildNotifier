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

package lol.hyper.buildnotifier.waterfall.events;

import lol.hyper.buildnotifier.waterfall.BuildNotifierWaterfall;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.TimeUnit;

public class PlayerJoin implements Listener {

    private final BuildNotifierWaterfall buildNotifierWaterfall;

    public PlayerJoin(BuildNotifierWaterfall buildNotifierWaterfall) {
        this.buildNotifierWaterfall = buildNotifierWaterfall;
    }

    @EventHandler
    public void onPing(ServerSwitchEvent event) {
        // player is joining the proxy
        if (event.getFrom() != null) {
            return;
        }

        ProxiedPlayer player = event.getPlayer();
        if (!player.hasPermission("buildnotifier.message")) {
            return;
        }

        int latestPaperBuild = buildNotifierWaterfall.waterfallHelper.getLatestBuild();
        int buildsBehind = buildNotifierWaterfall.waterfallHelper.getBuildsBehind();
        if (buildNotifierWaterfall.buildNumber < latestPaperBuild) {
            BaseComponent[] messageOne = new ComponentBuilder("Your Waterfall version is outdated. The latest build is " + latestPaperBuild + ".").color(ChatColor.YELLOW).create();
            BaseComponent[] messageTwo = new ComponentBuilder("You are currently " + buildsBehind + " build(s) behind.").color(ChatColor.YELLOW).create();
            ProxyServer.getInstance().getScheduler().schedule(buildNotifierWaterfall, () -> {
                player.sendMessage(ChatMessageType.SYSTEM, messageOne);
                player.sendMessage(ChatMessageType.SYSTEM, messageTwo);
            }, 10, TimeUnit.SECONDS);
        }
    }
}
