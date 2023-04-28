package lol.hyper.paperupdatercore;

import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.logging.Logger;

public class PaperPlugin {

    private final Logger pluginLogger;
    private final String serverVersion;
    private final int paperBuild;

    private int latestBuild;
    private int buildsBehind;

    public PaperPlugin(JavaPlugin plugin, String serverVersion, int paperBuild) {
        this.pluginLogger = plugin.getLogger();
        this.serverVersion = serverVersion;
        this.paperBuild = paperBuild;
        check();
    }

    /**
     * Checks the Paper version against the API.
     */
    public void check() {
        JSONObject versionData;

        try {
            versionData = JSONReader.requestJSON("https://api.papermc.io/v2/projects/paper/versions/" + serverVersion);
            if (versionData == null) {
                pluginLogger.warning("Unable to lookup version data. URL " + "https://api.papermc.io/v2/projects/paper/versions/" + serverVersion);
                return;
            }
        } catch (IOException exception) {
            pluginLogger.warning("Unable to lookup version data. URL " + "https://api.papermc.io/v2/projects/paper/versions/" + serverVersion);
            exception.printStackTrace();
            return;
        }
        JSONArray allBuilds = versionData.getJSONArray("builds");
        latestBuild = allBuilds.getInt(allBuilds.length() - 1);
        buildsBehind = latestBuild - paperBuild;
    }

    public int getBuildsBehind() {
        return buildsBehind;
    }

    public int getLatestBuild() {
        return latestBuild;
    }
}
