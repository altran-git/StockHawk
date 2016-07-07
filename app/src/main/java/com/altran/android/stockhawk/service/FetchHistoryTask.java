package com.altran.android.stockhawk.service;

import android.os.AsyncTask;

import com.altran.android.stockhawk.data.QuoteHistory;
import com.altran.android.stockhawk.rest.Utils;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by ND on 7/1/2016.
 */
public class FetchHistoryTask extends AsyncTask<Void, Void, QuoteHistory[]> {
  private static final String LOG_TAG = FetchHistoryTask.class.getSimpleName();

  private static String mSymbol;
  private static String mStartDate;
  private static String mEndDate;
  public AsyncResponse mDelegate = null;

  private OkHttpClient client = new OkHttpClient();

  public FetchHistoryTask(AsyncResponse delegate, String symbol, String startDate, String endDate) {
    this.mDelegate = delegate;
    this.mSymbol = symbol;
    this.mStartDate = startDate;
    this.mEndDate = endDate;
  }

  @Override
  protected QuoteHistory[] doInBackground(Void... params) {

    StringBuilder urlStringBuilder = new StringBuilder();
    try{
      // Base URL for the Yahoo query
      urlStringBuilder.append("https://query.yahooapis.com/v1/public/yql?q=");
      //select * from yahoo.finance.historicaldata where symbol = "YHOO" and startDate = "2009-09-11" and endDate = "2010-03-10"
      urlStringBuilder.append(URLEncoder.encode("select * from yahoo.finance.historicaldata where symbol = \"" +
              mSymbol.toUpperCase() +
              "\" and startDate = \"" +
              mStartDate +
              "\" and endDate = \"" +
              mEndDate +
              "\"", "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }

    // finalize the URL for the API query.
    urlStringBuilder.append("&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables."
            + "org%2Falltableswithkeys&callback=");

    String urlString;
    String getResponse;

    if (urlStringBuilder != null){
      urlString = urlStringBuilder.toString();
      try{
        getResponse = Utils.fetchData(client, urlString);
        //Todo: Get Data from JSON response
        return Utils.getHistoryData(getResponse);

      } catch (IOException e){
        e.printStackTrace();
      }
    }

    return null;
  }

  @Override
  protected void onPostExecute(QuoteHistory[] result) {
    mDelegate.processFinish(result);
  }

  public interface AsyncResponse {
    void processFinish(QuoteHistory[] result);
  }
}
