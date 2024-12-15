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

package lol.hyper.buildnotifier.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import lol.hyper.buildnotifier.core.VelocityHelper;
import lol.hyper.buildnotifier.velocity.events.PlayerJoin;
import lol.hyper.githubreleaseapi.GitHubRelease;
import lol.hyper.githubreleaseapi.GitHubReleaseAPI;
import lombok.Getter;
import org.bstats.velocity.Metrics;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Plugin(
        id = "buildnotifiervelocity",
        name = "BuildNotifier-Velocity",
        version = "1.0.2",
        authors = {"hyperdefined"},
        description = "Automatically check for Paper/Velocity updates.",
        url = "https://github.com/hyperdefined/BuildNotifier",
        dependencies = {}
)
public final class BuildNotifierVelocity {

    private final Logger logger;
    public final ProxyServer server;
    public int buildNumber = -1;
    public VelocityHelper velocityHelper;
    private final Metrics.Factory metricsFactory;

    @Inject
    public BuildNotifierVelocity(ProxyServer server, Logger logger, Metrics.Factory metricsFactory) {
        this.server = server;
        this.logger = logger;
        this.metricsFactory = metricsFactory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        // get some basic information about the server
        String version = server.getVersion().getVersion();
        System.out.println("Running Velocity version: " + version);
        String[] versionParts = version.split(" ", 2);
        String velocityVersion = versionParts[0];
        String patternString = "b(\\d+)";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(versionParts[1]);

        while (matcher.find()) {
            buildNumber = Integer.parseInt(matcher.group(1));
        }

        // if the regex failed, don't bother checking
        if (buildNumber == -1 || velocityVersion == null) {
            return;
        }

        logger.info("Running Velocity version: " + velocityVersion);
        logger.info("Running Velocity build: " + buildNumber);
        logger.info("Supporting Minecraft versions: " + ProtocolVersion.SUPPORTED_VERSION_STRING);

        velocityHelper = new VelocityHelper(logger, velocityVersion, buildNumber);
        server.getScheduler().buildTask(this, () -> {
            velocityHelper.check();
            int latestVelocityBuild = velocityHelper.getLatestBuild();
            // Server is outdated
            if (buildNumber < latestVelocityBuild) {
                logger.warning("Your Velocity version is outdated. The latest build is " + latestVelocityBuild + ".");
                logger.warning("You are currently " + velocityHelper.getBuildsBehind() + " build(s) behind.");
            }
        }).schedule();

        PlayerJoin playerJoin = new PlayerJoin(this);
        server.getEventManager().register(this, playerJoin);
        server.getScheduler().buildTask(this, this::checkForUpdates).schedule();

        metricsFactory.make(this, 24156);
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
        GitHubRelease current = api.getReleaseByTag("1.0.2");
        GitHubRelease latest = api.getLatestVersion();
        if (current == null) {
            logger.warning("You are running a version that does not exist on GitHub. If you are in a dev environment, you can ignore this. Otherwise, this is a bug!");
            return;
        }
        int buildsBehind = api.getBuildsBehind(current);
        if (buildsBehind == 0) {
            logger.info("You are running the latest version of BuildNotifier-Velocity.");
        } else {
            logger.warning("A new version is available (" + latest.getTagVersion() + ")! You are running version " + current.getTagVersion() + ". You are " + buildsBehind + " version(s) behind.");
        }
    }
}
