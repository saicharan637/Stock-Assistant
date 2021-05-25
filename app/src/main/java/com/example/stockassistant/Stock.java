package com.example.stockassistant;

import java.io.Serializable;
import java.util.Objects;

public class Stock implements Serializable {

    private String StockSymbol;
    private String StockCompanyName;
    private double StockLatestPrice;
    private double StockPriceChange;
    private double StockChangePercentage;

    public String getStockSymbol() {
        return StockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        StockSymbol = stockSymbol;
    }

    public String getStockCompanyName() {
        return StockCompanyName;
    }

    public void setStockCompanyName(String stockCompanyName) {
        StockCompanyName = stockCompanyName;
    }

    public double getStockLatestPrice() {
        return StockLatestPrice;
    }

    public void setStockLatestPrice(double stockLatestPrice) {
        StockLatestPrice = stockLatestPrice;
    }

    public double getStockPriceChange() {
        return StockPriceChange;
    }

    public void setStockPriceChange(double stockPriceChange) {
        StockPriceChange = stockPriceChange;
    }

    public double getStockChangePercentage() {
        return StockChangePercentage;
    }

    public void setStockChangePercentage(double stockChangePercentage) {
        StockChangePercentage = stockChangePercentage;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "StockSymbol='" + StockSymbol + '\'' +
                ", StockCompanyName='" + StockCompanyName + '\'' +
                ", StockLatestPrice=" + StockLatestPrice +
                ", StockPriceChange=" + StockPriceChange +
                ", StockChangePercentage=" + StockChangePercentage +
                '}';
    }
    @Override
    public boolean equals(Object obj) {
        boolean same = false;
        if (obj == null || obj.getClass() != getClass()) {
        }else {
            Stock stock = (Stock) obj;
            if (this.StockSymbol.equals(stock.StockSymbol))
                same = true;
        }
        return same;
    }

    @Override
    public int hashCode() {
        return Objects.hash(StockSymbol, StockCompanyName, StockLatestPrice, StockPriceChange, StockChangePercentage);
    }
}
