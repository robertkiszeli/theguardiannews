package com.robertkiszelirk.guardiannewsapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();
    // Number of pages
    private static int pages = 1;

    // Blank method
    private QueryUtils() {

    }

    // Get JSON file from HTTP
    static ArrayList<BaseArticleData> fetchArticleData(final String requestUrl) {
        // Set URL
        URL url = createUrl(requestUrl);
        // Create JSON
        String jsonResponse;
        // Fill JSON
        jsonResponse = makeHttpRequest(url);
        // Extract JSON file
        return extractArticlesFromJson(jsonResponse);
    }

    private static ArrayList<BaseArticleData> extractArticlesFromJson(String jsonResponse) {
        // If JSON is empty then return
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
        // Create base articles
        ArrayList<BaseArticleData> articles = new ArrayList<>();
        // Try to extract JSON
        try {
            // Set main JSON object
            JSONObject responseObjMain = new JSONObject(jsonResponse);
            // Set response JSON object
            JSONObject responseObj = responseObjMain.getJSONObject("response");
            // Get number of pages
            pages = responseObj.getInt("pages");
            // Set JSON array of results
            JSONArray resultsArray = responseObj.getJSONArray("results");
            // Iterate through array
            for (int i = 0; i < resultsArray.length(); i++) {
                // Set variables to default
                String sectionName = null;
                String webTitle = null;
                String webPublicationDate = null;
                String shortUrl = null;
                Bitmap thumbnailBitmap = null;
                String contributor = null;
                // Set current JSON object
                JSONObject result = resultsArray.getJSONObject(i);
                // Get section
                if (result.has("sectionName")) {
                    sectionName = result.getString("sectionName");
                }
                // Get title
                if (result.has("webTitle")) {
                    webTitle = result.getString("webTitle");
                }
                // Get publication date
                if (result.has("webPublicationDate")) {
                    webPublicationDate = result.getString("webPublicationDate");
                }
                // Set fields JSON object
                JSONObject fields = result.getJSONObject("fields");
                // Get URL
                if (fields.has("shortUrl")) {
                    shortUrl = fields.getString("shortUrl");
                }
                // Get image
                if (fields.has("thumbnail")) {
                    String thumbnail = fields.getString("thumbnail");
                    URL url = new URL(thumbnail);
                    thumbnailBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                }
                // Set tags JSON array for contributor data
                JSONArray tagsArray = result.getJSONArray("tags");
                for (int j = 0; j < tagsArray.length(); j++) {
                    // Get contributor
                    JSONObject tag = tagsArray.getJSONObject(j);
                    if (tag.has("webTitle")) {
                        contributor = tag.getString("webTitle");
                    } else {
                        contributor = "unnamed";
                    }
                }
                if (tagsArray.length() == 0) {
                    contributor = "unnamed";
                }
                // Set base article
                BaseArticleData articleData = new BaseArticleData(sectionName, webTitle, contributor, thumbnailBitmap, webPublicationDate, shortUrl);
                // Add article to array
                articles.add(articleData);
            }
            // Handle exceptions
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the JSON file", e);
        } catch (MalformedURLException urle) {
            Log.e(LOG_TAG, "Malformed URL has occurred", urle);
        } catch (IOException ioe) {
            Log.e(LOG_TAG, "I/O exception occurred", ioe);
        }
        // Return articles array
        return articles;
    }

    // Make HTTP request
    private static String makeHttpRequest(URL url) {
        // Response JSON
        String jsonResponse = "";
        // Check if URL valid
        if (url == null) {
            return jsonResponse;
        }
        // Set base data
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        // Try to connect to HTTP
        try {
            // Create connection
            urlConnection = (HttpURLConnection) url.openConnection();
            // Max read time
            urlConnection.setReadTimeout(10000);
            // Max connect time
            urlConnection.setConnectTimeout(15000);
            // Request type
            urlConnection.setRequestMethod("GET");
            // Start connection
            urlConnection.connect();
            // Check if connection OK
            if (urlConnection.getResponseCode() == 200) {
                // Read JSON
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromInputStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving JSON data.", e);
        } finally {
            // At end disconnect
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            // At end close stream
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    Log.e(LOG_TAG, "I/O exception occurred");
                }
            }
        }
        // Return JSON file
        return jsonResponse;
    }

    // Read from stream
    private static String readFromInputStream(InputStream inputStream) throws IOException {
        // Create string builder
        StringBuilder readString = new StringBuilder();
        // Check input stream valid
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            // REad stream line by line
            while (line != null) {
                readString.append(line);
                line = bufferedReader.readLine();
            }
        }
        // Return string
        return readString.toString();
    }

    // Create URL
    private static URL createUrl(String stringUrl) {
        // Base URL
        URL url = null;
        // Try create URL check format
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building URL", e);
        }
        // Return URL
        return url;
    }

    // Return number of pages
    static int getPagesCount() {
        return pages;
    }
}
