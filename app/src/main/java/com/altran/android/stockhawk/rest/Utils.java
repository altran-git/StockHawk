package com.altran.android.stockhawk.rest;

import android.content.ContentProviderOperation;
import android.util.Log;

import com.altran.android.stockhawk.data.QuoteColumns;
import com.altran.android.stockhawk.data.QuoteHistory;
import com.altran.android.stockhawk.data.QuoteProvider;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by sam_chordas on 10/8/15.
 * Updated by altran
 */
public class Utils {

  private static String LOG_TAG = Utils.class.getSimpleName();

  public static boolean showPercent = true;

  public static ArrayList quoteJsonToContentVals(String JSON){
    ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
    JSONObject jsonObject = null;
    JSONArray resultsArray = null;
    try{
      jsonObject = new JSONObject(JSON);
      if (jsonObject != null && jsonObject.length() != 0){
        jsonObject = jsonObject.getJSONObject("query");
        int count = Integer.parseInt(jsonObject.getString("count"));
        if (count == 1){
          jsonObject = jsonObject.getJSONObject("results")
              .getJSONObject("quote");
          batchOperations.add(buildBatchOperation(jsonObject));
        } else{
          resultsArray = jsonObject.getJSONObject("results").getJSONArray("quote");

          if (resultsArray != null && resultsArray.length() != 0){
            for (int i = 0; i < resultsArray.length(); i++){
              jsonObject = resultsArray.getJSONObject(i);
              batchOperations.add(buildBatchOperation(jsonObject));
            }
          }
        }
      }
    } catch (JSONException e){
      Log.e(LOG_TAG, "String to JSON failed: " + e);
    }
    return batchOperations;
  }

  public static QuoteHistory[] getHistoryData(String JSON){
    JSONObject jsonObject = null;
    JSONArray resultsArray = null;
    try{
      jsonObject = new JSONObject(JSON);
      if (jsonObject != null && jsonObject.length() != 0){
        jsonObject = jsonObject.getJSONObject("query");
        int count = Integer.parseInt(jsonObject.getString("count"));
        if (count > 1){
          QuoteHistory [] quoteHistory = new QuoteHistory[count];
          resultsArray = jsonObject.getJSONObject("results").getJSONArray("quote");
          int resultSize = resultsArray.length();

          if (resultsArray != null && resultSize != 0){
            for (int i = 0; i < resultSize; i++){
              jsonObject = resultsArray.getJSONObject(i);
              quoteHistory[resultSize-i-1] = new QuoteHistory(jsonObject.getString("Date"), Float.valueOf(jsonObject.getString("Close")));
            }
            return quoteHistory;
          }
        }
      }
    } catch (JSONException e){
      Log.e(LOG_TAG, "String to JSON failed: " + e);
    }

    return null;
  }


  public static String truncateBidPrice(String bidPrice){
    bidPrice = String.format("%.2f", Float.parseFloat(bidPrice));
    return bidPrice;
  }

  public static String truncateChange(String change, boolean isPercentChange){
    String weight = change.substring(0,1);
    String ampersand = "";
    if (isPercentChange){
      ampersand = change.substring(change.length() - 1, change.length());
      change = change.substring(0, change.length() - 1);
    }
    change = change.substring(1, change.length());
    double round = (double) Math.round(Double.parseDouble(change) * 100) / 100;
    change = String.format("%.2f", round);
    StringBuffer changeBuffer = new StringBuffer(change);
    changeBuffer.insert(0, weight);
    changeBuffer.append(ampersand);
    change = changeBuffer.toString();
    return change;
  }

  public static ContentProviderOperation buildBatchOperation(JSONObject jsonObject){
    ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
        QuoteProvider.Quotes.CONTENT_URI);
    try {
      String change = jsonObject.getString("Change");
      builder.withValue(QuoteColumns.SYMBOL, jsonObject.getString("symbol"));
      builder.withValue(QuoteColumns.NAME, jsonObject.getString("Name"));
      builder.withValue(QuoteColumns.BIDPRICE, truncateBidPrice(jsonObject.getString("Bid")));
      builder.withValue(QuoteColumns.PREV_CLOSE, jsonObject.getString("PreviousClose"));
      builder.withValue(QuoteColumns.OPEN_BID, jsonObject.getString("Open"));
      builder.withValue(QuoteColumns.DAY_LOW, jsonObject.getString("DaysLow"));
      builder.withValue(QuoteColumns.DAY_HIGH, jsonObject.getString("DaysHigh"));
      builder.withValue(QuoteColumns.YEAR_LOW, jsonObject.getString("YearLow"));
      builder.withValue(QuoteColumns.YEAR_HIGH, jsonObject.getString("YearHigh"));
      builder.withValue(QuoteColumns.PERCENT_CHANGE, truncateChange(
          jsonObject.getString("ChangeinPercent"), true));
      builder.withValue(QuoteColumns.CHANGE, truncateChange(change, false));
      if (change.charAt(0) == '-'){
        builder.withValue(QuoteColumns.ISUP, 0);
      }else{
        builder.withValue(QuoteColumns.ISUP, 1);
      }

    } catch (JSONException e){
      e.printStackTrace();
    }
    return builder.build();
  }


  /*
   *  This function checks the JSON response and
   *  Returns false if there are null values in JSON repsonse.
   *  Returns true otherwise.
   */
  public static boolean checkResponseOk(String JSON){
    JSONObject jsonObject;
    try{
      jsonObject = new JSONObject(JSON);
      if (jsonObject != null && jsonObject.length() != 0){
        jsonObject = jsonObject.getJSONObject("query");
        int count = Integer.parseInt(jsonObject.getString("count"));
        if (count == 1){
          jsonObject = jsonObject.getJSONObject("results")
                  .getJSONObject("quote");
          if("null" == jsonObject.getString("symbol") ||
                  "null" == jsonObject.getString("Name") ||
                  "null" == jsonObject.getString("Bid") ||
                  "null" == jsonObject.getString("Change") ||
                  "null" == jsonObject.getString("ChangeinPercent")){
            return false;
          } else {
            return true;
          }
        }
      }
    } catch (JSONException e){
      Log.e(LOG_TAG, "String to JSON failed: " + e);
    }
    return false;
  }

  public static String fetchData(OkHttpClient client, String url) throws IOException {
    Request request = new Request.Builder()
            .url(url)
            .build();

    Response response = client.newCall(request).execute();
    return response.body().string();
  }
}