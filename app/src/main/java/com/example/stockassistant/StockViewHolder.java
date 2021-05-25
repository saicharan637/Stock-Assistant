package com.example.stockassistant;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class StockViewHolder extends RecyclerView.ViewHolder {

    public TextView Price;
    public TextView PriceChange;
    public TextView Name;
    public TextView PriceChangePercentage;
    public TextView Symbol;

    public StockViewHolder(View view) {
        super(view);

        Price = view.findViewById(R.id.price);
        PriceChange= view.findViewById(R.id.price_change);
        Name=view.findViewById(R.id.name);
        PriceChangePercentage=view.findViewById(R.id.price_change_percent);
        Symbol=view.findViewById(R.id.symbol);
    }
}
