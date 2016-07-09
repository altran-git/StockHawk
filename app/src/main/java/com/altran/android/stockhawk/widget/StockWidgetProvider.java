package com.altran.android.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.altran.android.stockhawk.R;

/**
 * Created by ND on 7/8/2016.
 */
public class StockWidgetProvider extends AppWidgetProvider{
  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    super.onUpdate(context, appWidgetManager, appWidgetIds);

    for (int appWidgetId : appWidgetIds) {
      // Set up the intent that starts the StackViewService, which will
      // provide the views for this collection.
      Intent intent = new Intent(context, StockWidgetService.class);
      // Add the app widget ID to the intent extras.
      intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
      intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
      // Instantiate the RemoteViews object for the app widget layout.
      RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_main);
      // Set up the RemoteViews object to use a RemoteViews adapter.
      // This adapter connects
      // to a RemoteViewsService  through the specified intent.
      // This is how you populate the data.
      remoteViews.setRemoteAdapter(R.id.widget_listview, intent);

      // The empty view is displayed when the collection has no items.
      // It should be a sibling
      // of the collection view.
      remoteViews.setEmptyView(R.id.widget_listview, R.id.widget_empty);

      appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }
  }
}
