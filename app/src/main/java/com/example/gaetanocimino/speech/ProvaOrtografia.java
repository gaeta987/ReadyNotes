package com.example.gaetanocimino.speech;

import android.util.Log;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import com.google.gson.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;

public class ProvaOrtografia {
	
	static String host = "https://api.cognitive.microsoft.com";
	static String path = "/bing/v7.0/spellcheck";

	static String key = "";

	static String mkt = "en-US";
	static String mode = "proof";

	//Esegue una richiesta POST all'URL specificato per effettuare il controllo ortografico sul testo passato come parametro
	public static String check (String text) throws Exception {
		String params = "?mkt=" + mkt + "&mode=" + mode;
		URL url = new URL(host + path + params);
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection.setRequestProperty("Ocp-Apim-Subscription-Key", key);
		connection.setDoOutput(true);

		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.writeBytes("text=" + text);
		wr.flush();
		wr.close();

		//Restituisce una stringa json contenente i potenziali errori presenti nel testo
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line = in.readLine();

		JSONObject json = new JSONObject(line);

		JSONArray offset = (JSONArray) json.get("flaggedTokens");

		for(int j = 0;j<offset.length();j++) {
			JSONObject oneObject = offset.getJSONObject(j);

			Integer off = (Integer) oneObject.get("offset");
			String token = (String) oneObject.get("token");

			JSONArray suggestions = (JSONArray) oneObject.get("suggestions");

			ArrayList<Double> scores = new ArrayList<Double>();
			ArrayList<String> suggestion = new ArrayList<String>();

			for (int i=0; i < suggestions.length(); i++){
				try {
					JSONObject oneObj = suggestions.getJSONObject(i);
					scores.add(oneObj.getDouble("score"));
					suggestion.add(oneObj.getString("suggestion"));
				} catch (JSONException e) {
					//Errore
				}
			}

			int position = max(scores);
			String sugg = suggestion.get(position);

			char[] testo = text.toCharArray();

			for(int i = off;i<token.length();i++){
				testo[i] = sugg.charAt(i);
			}

			text = testo.toString();
		}

		in.close();

		Log.d("ORTOGRAFIA",text);

		return text;
	}

	//Calcola il suggerimento con lo score più elevato per uno specifico token
    public static int max(ArrayList<Double> array) {
        Double max = 0.0;
        int position = 0;
        for(int i = 0;i<array.size();i++) {
            if(array.get(i) > max) {
                max = array.get(i);
                position = i;
            }
        }

        return position;
    }
}
