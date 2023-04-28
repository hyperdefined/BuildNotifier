package lol.hyper.paperupdatercore;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.stream.Collectors;

public class JSONReader {

    /**
     * Get a JSONObject from a URL.
     *
     * @param url The URL to get JSON from.
     * @return The response JSONObject.
     */
    public static JSONObject requestJSON(String url) throws IOException {
        URLConnection conn = new URL(url).openConnection();
        conn.setRequestProperty("User-Agent", "PaperUpdater");
        conn.connect();

        InputStream in = conn.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String rawJSON = reader.lines().collect(Collectors.joining(System.lineSeparator()));
        reader.close();

        if (rawJSON.isEmpty()) {
            return null;
        }
        return new JSONObject(rawJSON);
    }
}
