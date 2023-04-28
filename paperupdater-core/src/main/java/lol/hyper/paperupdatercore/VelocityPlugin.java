package lol.hyper.paperupdatercore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.logging.Logger;

public class VelocityPlugin {

    private final Logger pluginLogger;
    private final String velocityVersion;
    private final int velocityBuild;

    private int buildsBehind;
    private int latestBuild;

    /**
     * Creates a VelocityPlugin object, which will load version information.
     *
     * @param pluginLogger    The plugin logger.
     * @param velocityVersion The Velocity version.
     * @param velocityBuild   The current Velocity build.
     */
    public VelocityPlugin(Logger pluginLogger, String velocityVersion, int velocityBuild) {
        this.pluginLogger = pluginLogger;
        this.velocityVersion = velocityVersion;
        this.velocityBuild = velocityBuild;
        check();
    }

    /**
     * Checks the Paper version against the API.
     */
    public void check() {
        JSONObject versionData;

        try {
            versionData = JSONReader.requestJSON("https://api.papermc.io/v2/projects/velocity/versions/" + velocityVersion);
            if (versionData == null) {
                pluginLogger.warning("Unable to lookup version data. URL " + "https://api.papermc.io/v2/projects/velocity/versions/" + velocityVersion);
                return;
            }
        } catch (IOException exception) {
            pluginLogger.warning("Unable to lookup version data. URL " + "https://api.papermc.io/v2/projects/velocity/versions/" + velocityVersion);
            exception.printStackTrace();
            return;
        }
        JSONArray allBuilds = versionData.getJSONArray("builds");
        latestBuild = allBuilds.getInt(allBuilds.length() - 1);
        buildsBehind = latestBuild - velocityBuild;
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
