package com.sam_chordas.android.stockhawk.interfaces;

import com.sam_chordas.android.stockhawk.models.StockMeta;

/**
 * Interface to interact with the callee class to notify regarding success, or errors if any.
 *
 * @author Thomas Kioko
 */
public interface HistoricalDataCallback {
    void onSuccess(StockMeta stockMeta);

    void onFailure();
}
