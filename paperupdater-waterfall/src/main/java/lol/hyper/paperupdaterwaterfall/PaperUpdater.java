package lol.hyper.paperupdaterwaterfall;

import lol.hyper.paperupdatercore.WaterfallPlugin;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PaperUpdater extends Plugin {

    private final Logger logger = this.getLogger();

    public WaterfallPlugin waterfallPlugin;

    @Override
    public void onEnable() {
        String serverVersion = this.getProxy().getVersion();
        logger.info("Running " + serverVersion);
        String patternString = ":(\\d+\\.\\d+)-.*:(\\d+)$";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(serverVersion);
        int buildNumber = -1;
        String minecraftVersion = null;
        if (matcher.find()) {
            minecraftVersion = matcher.group(1);
            buildNumber = Integer.parseInt(matcher.group(2));
        }

        if (buildNumber == -1 || minecraftVersion == null) {
            return;
        }

        waterfallPlugin = new WaterfallPlugin(this, minecraftVersion, buildNumber);
        int latestWaterfallBuild = waterfallPlugin.getLatestBuild();
        // Server is outdated
        if (buildNumber < latestWaterfallBuild) {
            logger.warning("Your Waterfall version is outdated. The latest version is " + latestWaterfallBuild + ".");
            logger.warning("You are currently " + waterfallPlugin.getBuildsBehind() + " builds behind.");
        }
    }
}
