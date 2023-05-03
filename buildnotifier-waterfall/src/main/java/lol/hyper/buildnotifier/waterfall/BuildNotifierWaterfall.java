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

package lol.hyper.buildnotifier.waterfall;

import lol.hyper.buildnotifier.waterfall.events.PlayerJoin;
import lol.hyper.buildnotifier.core.WaterfallHelper;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class BuildNotifierWaterfall extends Plugin {

    private final Logger logger = this.getLogger();
    public int buildNumber = -1;
    public WaterfallHelper waterfallHelper;

    @Override
    public void onEnable() {
        // get some basic information about the server
        String serverVersion = this.getProxy().getVersion();
        // use regex to get the build and MC version
        // if there is a better way please show me :)
        String patternString = ":(\\d+\\.\\d+)-.*:(\\d+)$";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(serverVersion);
        String minecraftVersion = null;
        if (matcher.find()) {
            minecraftVersion = matcher.group(1);
            buildNumber = Integer.parseInt(matcher.group(2));
        }

        // if the regex failed, don't bother checking
        if (buildNumber == -1 || minecraftVersion == null) {
            return;
        }

        logger.info("Running Minecraft version: " + minecraftVersion);
        logger.info("Running Waterfall build: " + buildNumber);

        waterfallHelper = new WaterfallHelper(this, minecraftVersion, buildNumber);
        int latestWaterfallBuild = waterfallHelper.getLatestBuild();
        // Server is outdated
        if (buildNumber < latestWaterfallBuild) {
            logger.warning("Your Waterfall version is outdated. The latest build is " + latestWaterfallBuild + ".");
            logger.warning("You are currently " + waterfallHelper.getBuildsBehind() + " build(s) behind.");
        }

        PlayerJoin playerJoin = new PlayerJoin(this);
        ProxyServer.getInstance().getPluginManager().registerListener(this, playerJoin);
    }
}
