package com.example.stockassistant;

import android.net.Uri;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class StockDataDownloader implements Runnable {

    private static final String TAG = "StockData";
    private final MainActivity mainActivity;
    private final String StockSymbol;
    private static final String START = "https://cloud.iexapis.com/stable/stock/";
    private static final String END ="/quote?token=";
    private static final String API_KEY = "pk_c1db1ac31d754d33ad18484c3587b77b";

    public StockDataDownloader(MainActivity mainActivity, String StockSymbol) {
        this.mainActivity = mainActivity;
        this.StockSymbol = StockSymbol;
    }

    @Override
    public void run() {
        String URL = START + StockSymbol + END + API_KEY;
        Uri uri = Uri.parse(URL);
        String url_string = uri.toString();
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(url_string);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            InputStream inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while((line = bufferedReader.readLine())!=null){
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        handleResults(sb.toString());

    }

    private void handleResults(String s) {
        Stock stock = parseJSON(s);
        mainActivity.runOnUiThread(() -> mainActivity.StockData(stock));

    }

    private Stock parseJSON(String s) {
        Stock stock = new Stock();
        try {
            JSONObject object = new JSONObject(s);
            String symbol = object.getString("symbol");
            String name = object.getString("companyName");
            double price = object.getDouble("latestPrice");
            double priceChange = object.getDouble("change");
            double changePercentage = object.getDouble("changePercent");
            stock.setStockCompanyName(name);
            stock.setStockSymbol(symbol);
            stock.setStockLatestPrice(price);
            stock.setStockPriceChange(priceChange);
            stock.setStockChangePercentage(changePercentage);
            return stock;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


}
