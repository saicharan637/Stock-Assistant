package com.example.stockassistant;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener{

    public String TAG="main";
    public RecyclerView recyclerView;
    public SwipeRefreshLayout swipeRefreshLayout;
    public ArrayList<Stock> StocksList = new ArrayList<>();
    public HashMap<String,String> hm = new HashMap<>();
    private StockAdapter stockAdapter;
    String stock_website = "http://www.marketwatch.com/investing/stock/";
    DatabaseHandler databaseHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Stock Watch");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
        recyclerView = findViewById(R.id.recycler);
        stockAdapter = new StockAdapter(StocksList, this);
        recyclerView.setAdapter(stockAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        swipeRefreshLayout = findViewById(R.id.SwipeRefreshLayout);
        databaseHandler = new DatabaseHandler(this);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (doInternetCheck()) {
                    doRefresh();
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    NoInternet("Refresh");
                }
            }
        });
        StockNameDownloader stockNameDownloader = new StockNameDownloader(this);
        new Thread(stockNameDownloader).start();
        ArrayList<Stock> TempList = databaseHandler.LoadStocks();
        if (doInternetCheck()) {
            Log.d(TAG, "yes it is here: ");
            for (int i = 0; i < TempList.size(); i++) {
                String symbol = TempList.get(i).getStockSymbol();
                Log.d(TAG, "yes it is here: "+symbol);
                StockDataDownloader stockDataDownloader = new StockDataDownloader(this, symbol);
                new Thread(stockDataDownloader).start();
            }
        } else {
            Log.d(TAG, "yes it is in else: ");
            NoInternet("App Start");
            StocksList.addAll(TempList);
            Collections.sort(StocksList, (s1, s2) -> s1.getStockSymbol().compareTo(s2.getStockSymbol()));
            stockAdapter.notifyDataSetChanged();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        stockAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseHandler.shutDown();
    }
    public void StockName(HashMap<String, String> hm) {
        if (hm != null && !hm.isEmpty())
            this.hm = hm;
    }

    private void doRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        ArrayList<Stock> arr = databaseHandler.LoadStocks();
        for (int stock = 0; stock < arr.size(); stock++) {
            String symbol = arr.get(stock).getStockSymbol();
            StockDataDownloader stockDataDownloader = new StockDataDownloader(this, symbol);
            new Thread(stockDataDownloader).start();
        }
    }
    private boolean doInternetCheck() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
        }
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
    public void NoInternet(String stage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Network Connection");
        if (stage.equals("Add a Stock"))
            builder.setMessage("Stocks Cannot be Added without A Network Connection");
        else if (stage.equals("Refresh"))
            builder.setMessage("Stocks Cannot be Updated without A Network Connection");
        else
            builder.setMessage("Stocks Cannot be Added/Updated without A Network Connection");
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        
    }
    public void StockData(Stock stock) {
        if (stock != null) {
            int index = StocksList.indexOf(stock);
            if (index > -1)
                StocksList.remove(index);
            StocksList.add(stock);
            Collections.sort(StocksList, new Comparator<Stock>() {
                @Override
                public int compare(Stock o1, Stock o2) {
                    return o1.getStockSymbol().compareTo(o2.getStockSymbol());
                }
            });
            stockAdapter.notifyDataSetChanged();
        }
    }

    private void addStockDialog() {
        if (hm == null) {
            StockNameDownloader stockNameDownloader = new StockNameDownloader(this);
            new Thread(stockNameDownloader).start();
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View customLayout = getLayoutInflater().inflate(R.layout.enter_stock, null);
        EditText stocksymbol = customLayout.findViewById(R.id.enter_stock);
        stocksymbol.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        builder.setView(customLayout);
        builder.setTitle("Stock Selection");
        builder.setMessage("Please Enter a Stock Symbol:");
        builder.setCancelable(false);
        doInternetCheck();

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String stocksym = stocksymbol.getText().toString();
                if(stocksym.trim().isEmpty())
                {
                    Toast.makeText(MainActivity.this, "Please Enter a Valid Stock Name", Toast.LENGTH_LONG).show();
                }
                else if (doInternetCheck())
                {
                    ArrayList<String> stockResults = SearchALLStockS(stocksym.trim());
                    Log.d(TAG,"it is"+stockResults.toString());
                    if (!stockResults.isEmpty())
                    {
                        Log.i("main", "stock Added");
                        ArrayList<String> stocks = new ArrayList<>(stockResults);
                        Log.d(TAG,"this"+stocks.toString());
                        if (stocks.size() == 1)
                        {
                            if (CheckDuplicateStocks(stocks.get(0)))
                            {
                                if(CheckDuplicateStocks(stocks.get(0)))
                                    DuplicateStock(stocksym);
                            }else{
                                AddNewStock(stocks.get(0));
                            }
                        }
                        else
                            MultipleStocks(stocksym, stocks, stocks.size());
                    }
                    else
                        StockNotFound(stocksym);
                }
                else{
                    NoInternet("Add a Stock");
                }
            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private ArrayList<String> SearchALLStockS(String s) {
        ArrayList<String> sList = new ArrayList<>();
        if (hm != null && !hm.isEmpty()) {
            for (String symbol : hm.keySet()) {
                String name = hm.get(symbol);
                if (symbol.toUpperCase().startsWith(s.toUpperCase()))
                    sList.add(symbol + " - " + name);
                else {
                    assert name != null;
                    if (name.toUpperCase().startsWith(s.toUpperCase()))
                        sList.add(symbol + " - " + name);
                }
            }
        }
        return sList;
    }

    private void DuplicateStock(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.icon);
        builder.setTitle("Duplicate Stock");
        builder.setMessage("Stock Symbol " + s + " is already displayed");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void AddNewStock(String s) {
        String symbol = s.split("-")[0].trim();
        StockDataDownloader stockDataDownloader = new StockDataDownloader(this, symbol);
        new Thread(stockDataDownloader).start();
        Stock stock = new Stock();
        stock.setStockSymbol(symbol);
        stock.setStockCompanyName(hm.get(symbol));
        databaseHandler.addStock(stock);
    }

    private void MultipleStocks(final String s, ArrayList<String> stockOptions, int size) {
        final String[] strings = new String[size];
        for (int i = 0; i < strings.length; i++)
            strings[i] = stockOptions.get(i);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Make a Selection");
        builder.setItems(strings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (CheckDuplicateStocks(strings[which]))
                    DuplicateStock(s);
                else
                    AddNewStock(strings[which]);
            }
        });

        builder.setNegativeButton("NEVERMIND", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void StockNotFound(String symbol) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Stock Not Found: "+ symbol);
        builder.setMessage("Data for stock symbol");
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private boolean CheckDuplicateStocks(String s) {
        String sym = s.split("-")[0].trim();
        Stock stock = new Stock();
        stock.setStockSymbol(sym);
        boolean a =StocksList.contains(stock);
        Log.d(TAG, "run: " + StocksList.toString()+"and :"+stock.toString());
        return a;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_stock) {
            if (item.getItemId() == R.id.add_stock) {
                if (doInternetCheck()) {
                    addStockDialog();
                } else {
                    NoInternet("Add a Stock");
                }
            }

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        int position = recyclerView.getChildLayoutPosition(v);
        String marketWatchURL = stock_website +  StocksList.get(position).getStockSymbol();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(marketWatchURL));
        startActivity(intent);

    }

    @Override
    public boolean onLongClick(View v) {
        TextView stockSymbol = v.findViewById(R.id.symbol);
        String stockSymbolText = stockSymbol.getText().toString().trim();
        final int position = recyclerView.getChildLayoutPosition(v);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Stock");
        builder.setIcon(R.drawable.delete);
        builder.setMessage("Delete Stock Symbol " + stockSymbolText + "?");

        builder.setPositiveButton("DELETE", (dialog, which) -> {
            databaseHandler.deleteStock(StocksList.get(position).getStockSymbol());
            StocksList.remove(position);
            stockAdapter.notifyDataSetChanged();
        }).setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
        return false;
    }

}