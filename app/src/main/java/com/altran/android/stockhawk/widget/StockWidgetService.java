package com.altran.android.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.altran.android.stockhawk.R;
import com.altran.android.stockhawk.data.QuoteColumns;
import com.altran.android.stockhawk.data.QuoteProvider;

/**
 * Created by ND on 7/8/2016.
 */
public class StockWidgetService extends RemoteViewsService{
  @Override
  public RemoteViewsFactory onGetViewFactory(Intent intent) {
    return new StockWidgetFactory(this.getApplicationContext(), intent);
  }
}

class StockWidgetFactory implements RemoteViewsService.RemoteViewsFactory {
  private Context mContext;
  private int mAppWidgetId;
  private Cursor mCursor;

  private static final String[] DETAIL_COLUMNS = {
          QuoteColumns.SYMBOL,
          QuoteColumns.CHANGE,
          QuoteColumns.BIDPRICE,
          QuoteColumns.ISUP
  };

  // These indices are tied to DETAIL_COLUMNS.  If DETAIL_COLUMNS changes, these
  // must change.
  public static final int COL_QUOTE_SYMBOL = 0;
  public static final int COL_QUOTE_CHANGE = 1;
  public static final int COL_QUOTE_BIDPRICE = 2;
  public static final int COL_QUOTE_ISUP = 3;

  public StockWidgetFactory(Context context, Intent intent) {
    mContext = context;
    mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID);
  }

  @Override
  public void onCreate() {
    //No connections made here,  Cursor data is retrieved in onDataSetChanged()
  }

  @Override
  public void onDataSetChanged() {
    // This is triggered when you call AppWidgetManager notifyAppWidgetViewDataChanged
    // on the collection view corresponding to this factory. You can do heaving lifting in
    // here, synchronously. For example, if you need to process an image, fetch something
    // from the network, etc., it is ok to do it here, synchronously. The widget will remain
    // in its current state while work is being done here, so you don't need to worry about
    // locking up the widget.
    String sortOrder = QuoteColumns.SYMBOL + " ASC";

    if(mCursor != null){
      mCursor.close();
    }

    //Get Cursor for all rows
    mCursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
            DETAIL_COLUMNS,
            null,
            null,
            sortOrder);
  }

  @Override
  public void onDestroy() {
    // In onDestroy() you should tear down anything that was setup for your data source,
    // eg. cursors, connections, etc.
    if (mCursor != null) {
      mCursor.close();
    }
  }

  @Override
  public int getCount() {
    return mCursor == null ? 0 : mCursor.getCount();
  }

  @Override
  public RemoteViews getViewAt(int position) {
    if (position == AdapterView.INVALID_POSITION ||
            mCursor == null || !mCursor.moveToPosition(position)) {
      return null;
    }

    RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);
    if(mCursor.moveToPosition(position)) {
      remoteViews.setTextViewText(R.id.widget_symbol, mCursor.getString(COL_QUOTE_SYMBOL));
      remoteViews.setTextViewText(R.id.widget_bid_price, mCursor.getString(COL_QUOTE_BIDPRICE));
      remoteViews.setTextViewText(R.id.widget_change, mCursor.getString(COL_QUOTE_CHANGE));
    }

    if (mCursor.getInt(COL_QUOTE_ISUP) == 1){
      remoteViews.setInt(R.id.widget_change, "setBackgroundResource", R.drawable.percent_change_pill_green);
    } else{
      remoteViews.setInt(R.id.widget_change, "setBackgroundResource", R.drawable.percent_change_pill_red);
    }

    // Next, set a fill-intent, which will be used to fill in the pending intent template
    // that is set on the collection view in StockWidgetProvider.
    Intent fillInIntent = new Intent();
    mCursor.moveToPosition(position);
    String symbol = mCursor.getString(COL_QUOTE_SYMBOL);
    Uri uri = QuoteProvider.Quotes.withSymbol(symbol);
    fillInIntent.setData(uri);
    // Make it possible to distinguish the individual on-click
    // action of a given item
    remoteViews.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);

    return remoteViews;
  }

  @Override
  public RemoteViews getLoadingView() {
    // You can create a custom loading view (for instance when getViewAt() is slow.) If you
    // return null here, you will get the default loading view.
    return null;
  }

  @Override
  public int getViewTypeCount() {
    return 1;
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public boolean hasStableIds() {
    return true;
  }
}
