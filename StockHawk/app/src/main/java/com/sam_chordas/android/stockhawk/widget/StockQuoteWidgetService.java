package com.sam_chordas.android.stockhawk.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * @author Thomas Kioko
 */
public class StockQuoteWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StockQuoteWidgetFactory(getApplicationContext(),intent);
    }
}
