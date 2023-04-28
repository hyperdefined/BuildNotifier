package lol.hyper.paperupdatervelocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import lol.hyper.paperupdatercore.VelocityPlugin;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Plugin(
        id = "paperupdater",
        name = "PaperUpdater",
        version = "1.0",
        authors = {"hyperdefined"},
        description = "Automatically check for Paper/Waterfall/Velocity updates.",
        url = "https://github.com/hyperdefined/PaperUpdater"
)
public final class VelocityUpdater {

    private final Logger logger;
    private final ProxyServer server;

    @Inject
    public VelocityUpdater(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        // get some basic information about the server
        String version = server.getVersion().getVersion();
        String[] versionParts = version.split(" ", 2);
        String velocityVersion = versionParts[0];
        logger.info("Running " + version);
        // use regex to get the build
        String patternString = "b\\d+";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(versionParts[1]);
        int buildNumber = -1;
        if (matcher.find()) {
            buildNumber = Integer.parseInt(matcher.group().replace("b", ""));
        }

        // if the regex failed, don't bother checking
        if (buildNumber == -1 || velocityVersion == null) {
            return;
        }

        VelocityPlugin velocityPlugin = new VelocityPlugin(logger, velocityVersion, buildNumber);
        int latestVelocityBuild = velocityPlugin.getLatestBuild();
        // Server is outdated
        if (buildNumber < latestVelocityBuild) {
            logger.warning("Your Velocity version is outdated. The latest version is " + latestVelocityBuild + ".");
            logger.warning("You are currently " + velocityPlugin.getBuildsBehind() + " builds behind.");
        }
    }
}
