package com.example.stockassistant;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.lang.*;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Locale;

import static java.lang.String.valueOf;

public class StockAdapter extends RecyclerView.Adapter<StockViewHolder> {

    private final ArrayList<Stock> stockArrayList;
    private final MainActivity mainActivity;

    public StockAdapter(ArrayList<Stock> stockArrayList, MainActivity mainActivity) {
        this.stockArrayList = stockArrayList;
        this.mainActivity = mainActivity;
    }


    @Override
    public StockViewHolder onCreateViewHolder( ViewGroup viewGroup, int viewType) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.stock_holder, viewGroup, false);
        view.setOnClickListener(mainActivity);
        view.setOnLongClickListener(mainActivity);

        return new StockViewHolder(view);
    }

    @Override
    public void onBindViewHolder( StockViewHolder stockViewHolder, int position) {

        Stock stock = stockArrayList.get(position);
        String symbol, color;
        if(stock.getStockPriceChange()>=0.0){
            symbol="▲";
            color="GREEN";
            ChangeColor(stockViewHolder,stock,color,symbol);
        }else {
            symbol="▼";
            color="RED";
        }
        ChangeColor(stockViewHolder,stock,color,symbol);

    }
    public void ChangeColor(StockViewHolder stockViewHolder, Stock stock, String color, String symbol){
        if(color.equals("GREEN")){
            stockViewHolder.Price.setTextColor(Color.GREEN);
            stockViewHolder.PriceChange.setTextColor(Color.GREEN);
            stockViewHolder.Name.setTextColor(Color.GREEN);
            stockViewHolder.PriceChangePercentage.setTextColor(Color.GREEN);
            stockViewHolder.Symbol.setTextColor(Color.GREEN);
        }else{
            stockViewHolder.Price.setTextColor(Color.RED);
            stockViewHolder.PriceChange.setTextColor(Color.RED);
            stockViewHolder.Name.setTextColor(Color.RED);
            stockViewHolder.PriceChangePercentage.setTextColor(Color.RED);
            stockViewHolder.Symbol.setTextColor(Color.RED);
        }
        String arrow = symbol;
        String price_change = valueOf(stock.getStockPriceChange());
        String up=arrow+price_change;
        stockViewHolder.Symbol.setText(stock.getStockSymbol());
        stockViewHolder.Name.setText(stock.getStockCompanyName());
        stockViewHolder.PriceChange.setText(up);
        stockViewHolder.Price.setText(String.format(Locale.US,"%.2f",stock.getStockLatestPrice()));
        stockViewHolder.PriceChangePercentage.setText(String.format(Locale.US,"(%.2f%%)",stock.getStockChangePercentage()));

    }

    @Override
    public int getItemCount() {
        return stockArrayList.size();
    }
}
