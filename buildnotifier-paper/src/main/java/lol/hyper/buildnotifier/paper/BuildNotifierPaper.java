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

package lol.hyper.buildnotifier.paper;

import lol.hyper.buildnotifier.paper.events.PlayerJoin;
import lol.hyper.buildnotifier.core.PaperPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class BuildNotifierPaper extends JavaPlugin {

    public final Logger logger = this.getLogger();
    public int buildNumber = -1;

    public PaperPlugin paperPlugin;

    @Override
    public void onEnable() {
        // get some basic information about the server
        String serverVersion = Bukkit.getServer().getVersion();
        logger.info("Running " + serverVersion);
        // use regex to get the build and MC version
        // if there is a better way please show me :)
        String patternString = "git-Paper-(\\w+) \\(MC: (\\d+\\.\\d+\\.\\d+)\\)";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(serverVersion);
        String minecraftVersion = null;
        if (matcher.find()) {
            buildNumber = Integer.parseInt(matcher.group(1));
            minecraftVersion = matcher.group(2);
        }

        // if the regex failed, don't bother checking
        if (buildNumber == -1 || minecraftVersion == null) {
            return;
        }

        paperPlugin = new PaperPlugin(this, minecraftVersion, buildNumber);
        int latestPaperBuild = paperPlugin.getLatestBuild();
        // Server is outdated
        if (buildNumber < latestPaperBuild) {
            logger.warning("Your Paper version is outdated. The latest build is " + latestPaperBuild + ".");
            logger.warning("You are currently " + paperPlugin.getBuildsBehind() + " build(s) behind.");
        }

        PlayerJoin playerJoin = new PlayerJoin(this);
        Bukkit.getServer().getPluginManager().registerEvents(playerJoin, this);
    }
}
