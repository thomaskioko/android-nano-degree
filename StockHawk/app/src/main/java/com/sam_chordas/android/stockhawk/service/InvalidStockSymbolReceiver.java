package com.sam_chordas.android.stockhawk.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.sam_chordas.android.stockhawk.R;

/**
 * A service to display a toast when there is an invalid symbol
 *
 * @author Thomas Kioko
 */
public class InvalidStockSymbolReceiver extends BroadcastReceiver {
    public InvalidStockSymbolReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, context.getString(R.string.invalid_stock_symbol), Toast.LENGTH_LONG).show();
    }
}
