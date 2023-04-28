/*
 * This file is part of PaperUpdater.
 *
 * PaperUpdater is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PaperUpdater is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PaperUpdater.  If not, see <https://www.gnu.org/licenses/>.
 */

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

    /**
     * Creates a PaperPlugin object, which will load version information.
     *
     * @param plugin        The plugin.
     * @param serverVersion The Minecraft server version.
     * @param paperBuild    The current paper build.
     */
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
