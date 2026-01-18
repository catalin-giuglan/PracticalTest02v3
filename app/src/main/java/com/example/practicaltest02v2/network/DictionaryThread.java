package com.example.practicaltest02v2.network;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DictionaryThread extends Thread {

    private Context context;
    private String word;

    public static final String ACTION_DICTIONARY = "ro.pub.cs.systems.eim.practicaltest02v2.DICTIONARY";
    public static final String DATA_DEFINITION = "definition";

    public DictionaryThread(Context context, String word) {
        this.context = context;
        this.word = word;
    }

    @Override
    public void run() {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            // 3a) Request HTTP
            String urlString = "https://api.dictionaryapi.dev/api/v2/entries/en/" + word;
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String response = sb.toString();

            // 3a) Logare raspuns complet
            Log.d("[DictionaryThread]", "Full JSON: " + response);

            // 3b) Parsare JSON pentru prima definitie
            // Root este un JSONArray
            JSONArray root = new JSONArray(response);
            JSONObject firstEntry = root.getJSONObject(0);
            JSONArray meanings = firstEntry.getJSONArray("meanings");
            JSONObject firstMeaning = meanings.getJSONObject(0);
            JSONArray definitions = firstMeaning.getJSONArray("definitions");
            JSONObject firstDefinitionObj = definitions.getJSONObject(0);

            String definition = firstDefinitionObj.getString("definition");

            // 3b) Logare definitie
            Log.d("[DictionaryThread]", "Definition found: " + definition);

            // 3c) Trimite Broadcast
            Intent intent = new Intent();
            intent.setAction(ACTION_DICTIONARY);
            intent.putExtra(DATA_DEFINITION, definition);
            context.sendBroadcast(intent);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("[DictionaryThread]", "Error: " + e.getMessage());
        } finally {
            if (connection != null) connection.disconnect();
            try { if (reader != null) reader.close(); } catch (Exception e) {}
        }
    }
}