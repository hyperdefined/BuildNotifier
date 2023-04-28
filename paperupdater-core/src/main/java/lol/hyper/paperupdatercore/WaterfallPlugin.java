package lol.hyper.paperupdatercore;

import net.md_5.bungee.api.plugin.Plugin;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.logging.Logger;

public class WaterfallPlugin {

    private final Logger pluginLogger;
    private final String serverVersion;
    private final int waterfallBuild;

    private int latestBuild;
    private int buildsBehind;

    /**
     * Creates a WaterfallPlugin object, which will load version information.
     *
     * @param plugin         The plugin.
     * @param serverVersion  The Minecraft server version.
     * @param waterfallBuild The current Waterfall build.
     */
    public WaterfallPlugin(Plugin plugin, String serverVersion, int waterfallBuild) {
        this.pluginLogger = plugin.getLogger();
        this.serverVersion = serverVersion;
        this.waterfallBuild = waterfallBuild;
        check();
    }

    /**
     * Checks the Paper version against the API.
     */
    public void check() {
        JSONObject versionData;

        try {
            versionData = JSONReader.requestJSON("https://api.papermc.io/v2/projects/waterfall/versions/" + serverVersion);
            if (versionData == null) {
                pluginLogger.warning("Unable to lookup version data. URL " + "https://api.papermc.io/v2/projects/waterfall/versions/" + serverVersion);
                return;
            }
        } catch (IOException exception) {
            pluginLogger.warning("Unable to lookup version data. URL " + "https://api.papermc.io/v2/projects/waterfall/versions/" + serverVersion);
            exception.printStackTrace();
            return;
        }
        JSONArray allBuilds = versionData.getJSONArray("builds");
        latestBuild = allBuilds.getInt(allBuilds.length() - 1);
        buildsBehind = latestBuild - waterfallBuild;
    }

    /**
     * Returns how many builds behind.
     *
     * @return The number behind.
     */
    public int getBuildsBehind() {
        return buildsBehind;
    }

    /**
     * Returns the latest build.
     *
     * @return The latest behind.
     */
    public int getLatestBuild() {
        return latestBuild;
    }
}
