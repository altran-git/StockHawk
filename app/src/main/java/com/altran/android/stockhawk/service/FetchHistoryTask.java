package com.altran.android.stockhawk.service;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.altran.android.stockhawk.DetailActivityFragment;
import com.altran.android.stockhawk.R;
import com.altran.android.stockhawk.data.QuoteHistory;
import com.altran.android.stockhawk.rest.Utils;
import com.github.mikephil.charting.charts.LineChart;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by ND on 7/1/2016.
 */
public class FetchHistoryTask extends AsyncTask<Void, Void, QuoteHistory[]> {
  private static final String LOG_TAG = FetchHistoryTask.class.getSimpleName();

  private static String mSymbol;
  private static String mStartDate;
  private static String mEndDate;
  private static String mSelectedTab;
  public AsyncResponse mDelegate = null;
  private Context mContext;
  Handler mHandler;

  private static ProgressBar mProgressBar;
  private static LineChart mLineChart;

  private OkHttpClient client = new OkHttpClient();

  public FetchHistoryTask(AsyncResponse delegate, Context context, ProgressBar progressBar, LineChart lineChart, String symbol, String selectedTab) {
    this.mDelegate = delegate;
    this.mContext = context;
    this.mSymbol = symbol;
    this.mSelectedTab = selectedTab;
    this.mProgressBar = progressBar;
    this.mLineChart = lineChart;
    this.mHandler = new Handler();
  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    mProgressBar.setVisibility(View.VISIBLE);
    mLineChart.setVisibility(View.INVISIBLE);

    //find the startDate and endDate for query
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    GregorianCalendar calEndDate = new GregorianCalendar();
    mEndDate = dateFormat.format(calEndDate.getTime());

    GregorianCalendar calStartDate = new GregorianCalendar();

    if(mSelectedTab.equals(DetailActivityFragment.ONE_WEEK)){
      calStartDate.add(Calendar.WEEK_OF_YEAR, -1);
    } else if(mSelectedTab.equals(DetailActivityFragment.ONE_MONTH)){
      calStartDate.add(Calendar.MONTH, -1);
    } else {
      calStartDate.add(Calendar.YEAR, -1);
    }

    mStartDate = dateFormat.format(calStartDate.getTime());
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
        //Get Data from JSON response
        return Utils.getHistoryData(getResponse);

      } catch (IOException e){
        //e.printStackTrace();
        mHandler.post(new Runnable() {
          @Override
          public void run() {
            Toast.makeText(mContext, R.string.timed_out, Toast.LENGTH_SHORT).show();
          }
        });
      }
    }

    return null;
  }

  //onPostExecute passes results to processFinish
  // interface that is called in DetailActivityFragment
  @Override
  protected void onPostExecute(QuoteHistory[] result) {
    super.onPostExecute(result);
    mProgressBar.setVisibility(View.INVISIBLE);
    mLineChart.setVisibility(View.VISIBLE);
    mDelegate.processFinish(result);
  }

  public interface AsyncResponse {
    void processFinish(QuoteHistory[] result);
  }
}
