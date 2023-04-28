package lol.hyper.paperupdaterpaper;

import lol.hyper.paperupdatercore.PaperPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PaperUpdater extends JavaPlugin {

    public final Logger logger = this.getLogger();

    public PaperPlugin paperPlugin;

    @Override
    public void onEnable() {
        String serverVersion = Bukkit.getServer().getVersion();
        logger.info("Running " + serverVersion);
        String patternString = "git-Paper-(\\w+) \\(MC: (\\d+\\.\\d+\\.\\d+)\\)";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(serverVersion);
        int buildNumber = -1;
        String minecraftVersion = null;
        if (matcher.find()) {
            buildNumber = Integer.parseInt(matcher.group(1));
            minecraftVersion = matcher.group(2);
        }

        if (buildNumber == -1 || minecraftVersion == null) {
            return;
        }

        paperPlugin = new PaperPlugin(this, minecraftVersion, buildNumber);
        int latestPaperBuild = paperPlugin.getLatestBuild();
        // Server is outdated
        if (buildNumber < latestPaperBuild) {
            logger.warning("Your Paper version is outdated. The latest version is " + latestPaperBuild + ".");
            logger.warning("You are currently " + paperPlugin.getBuildsBehind() + " builds behind.");
        }
    }
}
