package com.example.stockassistant;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import javax.net.ssl.HttpsURLConnection;

public class StockNameDownloader implements Runnable{

    private static final String TAG="StockName";
    private final MainActivity mainActivity;
    private final String URL ="https://api.iextrading.com/1.0/ref-data/symbols";

    public StockNameDownloader(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void run() {
        Uri dataUri = Uri.parse(URL);
        String urlToUse = dataUri.toString();
        Log.d(TAG, "run: " + urlToUse);
        StringBuilder sb = new StringBuilder();

        try {
            URL url = new URL(urlToUse);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            if (conn.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                handleResults(null);
                return;
            }
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line=reader.readLine())!=null) {
                sb.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        handleResults(sb.toString());
    }

    private HashMap<String,String> parseJSON(String s){

        HashMap<String,String> StockSymbol = new HashMap<>();
        try{
            JSONArray jObjMain = new JSONArray(s);
            for(int i=0;i<jObjMain.length();i++){
                JSONObject jsonObject = (JSONObject) jObjMain.get(i);
                String symbol=jsonObject.getString("symbol");
                String name=jsonObject.getString("name");
                StockSymbol.put(symbol,name);
            }
            return StockSymbol;
        }catch (Exception e){
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();

        }
        return null;
    }
    private void handleResults(String s) {

        HashMap<String, String> StockName = parseJSON(s);
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainActivity.StockName(StockName);
            }
        });

    }



}
