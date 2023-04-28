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

    public int getBuildsBehind() {
        return buildsBehind;
    }

    public int getLatestBuild() {
        return latestBuild;
    }
}
