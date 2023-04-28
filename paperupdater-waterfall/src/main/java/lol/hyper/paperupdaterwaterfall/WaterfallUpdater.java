package lol.hyper.paperupdaterwaterfall;

import lol.hyper.paperupdatercore.WaterfallPlugin;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class WaterfallUpdater extends Plugin {

    private final Logger logger = this.getLogger();

    public WaterfallPlugin waterfallPlugin;

    @Override
    public void onEnable() {
        // get some basic information about the server
        String serverVersion = this.getProxy().getVersion();
        logger.info("Running " + serverVersion);
        // use regex to get the build and MC version
        // if there is a better way please show me :)
        String patternString = ":(\\d+\\.\\d+)-.*:(\\d+)$";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(serverVersion);
        int buildNumber = -1;
        String minecraftVersion = null;
        if (matcher.find()) {
            minecraftVersion = matcher.group(1);
            buildNumber = Integer.parseInt(matcher.group(2));
        }

        // if the regex failed, don't bother checking
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
