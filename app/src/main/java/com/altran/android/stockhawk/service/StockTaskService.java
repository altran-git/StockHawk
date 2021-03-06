package com.altran.android.stockhawk.service;

import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.Handler;
import android.os.RemoteException;
import android.widget.Toast;

import com.altran.android.stockhawk.R;
import com.altran.android.stockhawk.data.QuoteColumns;
import com.altran.android.stockhawk.data.QuoteProvider;
import com.altran.android.stockhawk.rest.Utils;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by sam_chordas on 9/30/15.
 * The GCMTask service is primarily for periodic tasks. However, OnRunTask can be called directly
 * and is used for the initialization and adding task as well.
 *
 * Updated by altran
 */
public class StockTaskService extends GcmTaskService{
  private String LOG_TAG = StockTaskService.class.getSimpleName();
  public static final String ACTION_DATA_UPDATED = "com.altran.android.stockhawk.ACTION_DATA_UPDATED";

  private OkHttpClient client = new OkHttpClient();
  private Context mContext;
  private Handler mHandler;
  private StringBuilder mStoredSymbols = new StringBuilder();

  public StockTaskService(){}

  public StockTaskService(Context context, Handler handler){
    this.mContext = context;
    this.mHandler = handler;
  }

  @Override
  public int onRunTask(TaskParams params){
    //Log.d(LOG_TAG, "onRunTask");

    Cursor initQueryCursor;
    if (mContext == null){
      mContext = this;
    }
    StringBuilder urlStringBuilder = new StringBuilder();
    try{
      // Base URL for the Yahoo query
      urlStringBuilder.append("https://query.yahooapis.com/v1/public/yql?q=");
      urlStringBuilder.append(URLEncoder.encode("select * from yahoo.finance.quotes where symbol "
        + "in (", "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    if (params.getTag().equals("init") || params.getTag().equals("periodic")){
      initQueryCursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
          new String[] { "Distinct " + QuoteColumns.SYMBOL }, null,
          null, null);
      if (initQueryCursor.getCount() == 0 || initQueryCursor == null){
        // Init task. Populates DB with quotes for the symbols seen below
        try {
          urlStringBuilder.append(
              URLEncoder.encode("\"YHOO\",\"AAPL\",\"GOOG\",\"MSFT\")", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
        }
      } else if (initQueryCursor != null){
        //DatabaseUtils.dumpCursor(initQueryCursor);
        initQueryCursor.moveToFirst();
        for (int i = 0; i < initQueryCursor.getCount(); i++){
          mStoredSymbols.append("\""+
              initQueryCursor.getString(initQueryCursor.getColumnIndex("symbol"))+"\",");
          initQueryCursor.moveToNext();
        }
        mStoredSymbols.replace(mStoredSymbols.length() - 1, mStoredSymbols.length(), ")");
        try {
          urlStringBuilder.append(URLEncoder.encode(mStoredSymbols.toString(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
        }
      }
      initQueryCursor.close();
    } else if (params.getTag().equals("add")){
      // get symbol from params.getExtra and build query
      String stockInput = params.getExtras().getString("symbol");
      try {
        urlStringBuilder.append(URLEncoder.encode("\""+stockInput+"\")", "UTF-8"));
      } catch (UnsupportedEncodingException e){
        //e.printStackTrace();
      }
    }
    // finalize the URL for the API query.
    urlStringBuilder.append("&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables."
        + "org%2Falltableswithkeys&callback=");

    String urlString;
    String getResponse;
    int result = GcmNetworkManager.RESULT_FAILURE;

    if (urlStringBuilder != null){
      urlString = urlStringBuilder.toString();
      try{
        getResponse = Utils.fetchData(client, urlString);

        //If we are trying to add a new stock, we have to check if it is valid
        if(params.getTag().equals("add")){
          if(Utils.checkResponseOk(getResponse)){
            result = addResponse(getResponse);
          } else{
            mHandler.post(new Runnable() {
              @Override
              public void run() {
                Toast.makeText(mContext, R.string.symbol_not_found, Toast.LENGTH_SHORT).show();
              }
            });
          }
        } else{
          result = addResponse(getResponse);
        }
      } catch (IOException e){
        //e.printStackTrace();
      }
    }

    return result;
  }

  public int addResponse(String getResponse){
    try {
      mContext.getContentResolver().applyBatch(QuoteProvider.AUTHORITY,
              Utils.quoteJsonToContentVals(getResponse));
      //Update widgets after successful add
      Utils.updateWidgets(mContext);
      return GcmNetworkManager.RESULT_SUCCESS;
    }catch (RemoteException | OperationApplicationException e){
      //Log.e(LOG_TAG, "Error applying batch insert", e);
      return GcmNetworkManager.RESULT_FAILURE;
    }
  }

  }
