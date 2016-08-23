package com.sam_chordas.android.stockhawk.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Thomas Kioko
 */
public class StockSymbol implements Parcelable {
    public String date;
    public double close;


    /**
     * @param date  Date
     * @param close {@link Boolean}
     */
    public StockSymbol(String date, double close) {
        this.date = date;
        this.close = close;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.date);
        dest.writeDouble(this.close);
    }

    /**
     * @param in {@link Parcel}
     */
    protected StockSymbol(Parcel in) {
        this.date = in.readString();
        this.close = in.readDouble();
    }

    public static final Parcelable.Creator<StockSymbol> CREATOR = new Parcelable.Creator<StockSymbol>() {
        @Override
        public StockSymbol createFromParcel(Parcel source) {
            return new StockSymbol(source);
        }

        @Override
        public StockSymbol[] newArray(int size) {
            return new StockSymbol[size];
        }
    };
}
