package com.altran.android.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.altran.android.stockhawk.DetailActivity;
import com.altran.android.stockhawk.R;
import com.altran.android.stockhawk.service.StockTaskService;

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

      //Set action for pending intent (click)
      //a Fill-in intent is set in the RemoteViewsFactory to handle individual item clicks
      Intent detailIntent = new Intent(context, DetailActivity.class);
      PendingIntent pendingIntent = TaskStackBuilder.create(context)
              .addNextIntentWithParentStack(detailIntent)
              .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
      remoteViews.setPendingIntentTemplate(R.id.widget_listview, pendingIntent);

      // The empty view is displayed when the collection has no items.
      // It should be a sibling
      // of the collection view.
      remoteViews.setEmptyView(R.id.widget_listview, R.id.widget_empty);

      appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    super.onReceive(context, intent);
    if (StockTaskService.ACTION_DATA_UPDATED.equals(intent.getAction())) {
      AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
      int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
              new ComponentName(context, getClass()));
      appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_listview);
    }
  }
}
