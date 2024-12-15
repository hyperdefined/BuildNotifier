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

import io.papermc.paper.ServerBuildInfo;
import lol.hyper.buildnotifier.core.PaperHelper;
import lol.hyper.buildnotifier.paper.events.PlayerJoin;
import lol.hyper.githubreleaseapi.GitHubRelease;
import lol.hyper.githubreleaseapi.GitHubReleaseAPI;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.logging.Logger;

public final class BuildNotifierPaper extends JavaPlugin {

    public final Logger logger = this.getLogger();
    public int buildNumber = -1;
    public PaperHelper paperHelper;

    @Override
    public void onEnable() {
        // get some basic information about the server
        ServerBuildInfo build = ServerBuildInfo.buildInfo();
        buildNumber = build.buildNumber().orElse(-1);
        String minecraftVersion = build.minecraftVersionId();
        // if we can't get the build information, skip
        if (buildNumber == -1) {
            return;
        }

        logger.info("Running Minecraft version: " + minecraftVersion);
        logger.info("Running Paper build: " + buildNumber);

        paperHelper = new PaperHelper(this, minecraftVersion, buildNumber);
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            paperHelper.check();
            int latestPaperBuild = paperHelper.getLatestBuild();
            // Server is outdated
            if (buildNumber < latestPaperBuild) {
                logger.warning("Your Paper version is outdated. The latest build is " + latestPaperBuild + ".");
                logger.warning("You are currently " + paperHelper.getBuildsBehind() + " build(s) behind.");
            }
        });

        PlayerJoin playerJoin = new PlayerJoin(this);
        Bukkit.getServer().getPluginManager().registerEvents(playerJoin, this);
        Bukkit.getAsyncScheduler().runNow(this, scheduledTask -> checkForUpdates());

        new Metrics(this, 24155);
    }

    public void checkForUpdates() {
        GitHubReleaseAPI api;
        try {
            api = new GitHubReleaseAPI("BuildNotifier", "hyperdefined");
        } catch (IOException e) {
            logger.warning("Unable to check updates!");
            e.printStackTrace();
            return;
        }
        GitHubRelease current = api.getReleaseByTag(this.getDescription().getVersion());
        GitHubRelease latest = api.getLatestVersion();
        if (current == null) {
            logger.warning("You are running a version that does not exist on GitHub. If you are in a dev environment, you can ignore this. Otherwise, this is a bug!");
            return;
        }
        int buildsBehind = api.getBuildsBehind(current);
        if (buildsBehind == 0) {
            logger.info("You are running the latest version of BuildNotifier-Paper.");
        } else {
            logger.warning("A new version is available (" + latest.getTagVersion() + ")! You are running version " + current.getTagVersion() + ". You are " + buildsBehind + " version(s) behind.");
        }
    }
}
