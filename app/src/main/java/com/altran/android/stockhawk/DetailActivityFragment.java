package com.altran.android.stockhawk;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

import com.altran.android.stockhawk.data.QuoteColumns;
import com.altran.android.stockhawk.data.QuoteDatabase;
import com.altran.android.stockhawk.data.QuoteHistory;
import com.altran.android.stockhawk.service.FetchHistoryTask;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

/**
 * Created by ND on 7/5/2016.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

  private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
  private final static int DETAIL_LOADER = 0;
  static final String DETAIL_URI = "URI";

  private Uri mUri;
  private String mSymbol;

  private static final String[] DETAIL_COLUMNS = {
          QuoteDatabase.QUOTES + "." + QuoteColumns._ID,
          QuoteColumns.SYMBOL,
          QuoteColumns.NAME,
          QuoteColumns.PERCENT_CHANGE,
          QuoteColumns.CHANGE,
          QuoteColumns.BIDPRICE,
          QuoteColumns.PREV_CLOSE,
          QuoteColumns.OPEN_BID,
          QuoteColumns.DAY_LOW,
          QuoteColumns.DAY_HIGH,
          QuoteColumns.YEAR_LOW,
          QuoteColumns.YEAR_HIGH,
          QuoteColumns.ISUP
  };

  // These indices are tied to DETAIL_COLUMNS.  If DETAIL_COLUMNS changes, these
  // must change.
  public static final int COL_QUOTE_ID = 0;
  public static final int COL_QUOTE_SYMBOL = 1;
  public static final int COL_QUOTE_NAME = 2;
  public static final int COL_QUOTE_PERCENT_CHANGE = 3;
  public static final int COL_QUOTE_CHANGE = 4;
  public static final int COL_QUOTE_BIDPRICE = 5;
  public static final int COL_QUOTE_PREV_CLOSE = 6;
  public static final int COL_QUOTE_OPEN_BID = 7;
  public static final int COL_QUOTE_DAY_LOW = 8;
  public static final int COL_QUOTE_DAY_HIGH = 9;
  public static final int COL_QUOTE_YEAR_LOW = 10;
  public static final int COL_QUOTE_YEAR_HIGH = 11;
  public static final int COL_QUOTE_ISUP = 12;

  private TextView mBidView;
  private TextView mSymbolView;
  private TextView mChangeView;
  private TextView mPrevCloseView;
  private TextView mOpenView;
  private TextView mDayLowView;
  private TextView mDayHighView;
  private TextView mYearLowView;
  private TextView mYearHighView;
  private LineChart mLineChart;
  private TabHost mTabHost;
  private View tabContent;

  public DetailActivityFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Log.d(LOG_TAG, "onCreate");
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    Log.d(LOG_TAG, "onCreateView");

    Bundle arguments = getArguments();
    if (arguments != null){
      mUri = arguments.getParcelable(DETAIL_URI);
      mSymbol = mUri.getLastPathSegment();
    }

    View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

    mBidView = (TextView) rootView.findViewById(R.id.detail_bidprice_textview);
    mSymbolView = (TextView) rootView.findViewById(R.id.detail_symbol_textview);
    mChangeView = (TextView) rootView.findViewById(R.id.detail_change_textview);
    mPrevCloseView = (TextView) rootView.findViewById(R.id.detail_prevclose_textview);
    mOpenView = (TextView) rootView.findViewById(R.id.detail_open_textview);
    mDayLowView = (TextView) rootView.findViewById(R.id.detail_daylow_textview);
    mDayHighView = (TextView) rootView.findViewById(R.id.detail_dayhigh_textview);
    mYearLowView = (TextView) rootView.findViewById(R.id.detail_yearlow_textview);
    mYearHighView = (TextView) rootView.findViewById(R.id.detail_yearhigh_textview);
    mLineChart = (LineChart) rootView.findViewById(R.id.detail_stock_linechart);
    mTabHost = (TabHost) rootView.findViewById(R.id.tabhost);
    mTabHost.setup();
    TabHost.TabSpec spec = mTabHost.newTabSpec("Tab One");

    spec = mTabHost.newTabSpec("Tab 1");
    spec.setIndicator("Tab 1");
    spec.setContent(android.R.id.tabcontent);
    mTabHost.addTab(spec);

    FetchHistoryTask fetchHistoryTask = new FetchHistoryTask(new FetchHistoryTask.AsyncResponse() {
      @Override
      public void processFinish(QuoteHistory [] result) {
        Legend legend = mLineChart.getLegend();
        legend.setEnabled(false);

        LineData data = chartSetup(result);
        mLineChart.setDescription(null);
        mLineChart.setData(data);
        mLineChart.setAutoScaleMinMaxEnabled(true);
        mLineChart.invalidate();

      }
    },mSymbol, "2016-06-22", "2016-07-06");
    fetchHistoryTask.execute();

    return rootView;
  }

  public LineData chartSetup(QuoteHistory [] result){
    ArrayList<Entry> entries = new ArrayList<>();
    ArrayList<String> labels = new ArrayList<String>();

    for(int i = 0; i<result.length; i++){
      entries.add(new Entry(result[i].getCloseBid(), i));
      labels.add(result[i].getDate());
    }


    LineDataSet lineDataSet = new LineDataSet(entries, "Close Bid");
    lineDataSet.setDrawValues(false);

    LineData data = new LineData(labels, lineDataSet);

    return data;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    Log.d(LOG_TAG, "onActivityCreated");
    // Prepare the loader.  Either re-connect with an existing one,
    // or start a new one.
    getLoaderManager().initLoader(DETAIL_LOADER, null, this);
    super.onActivityCreated(savedInstanceState);
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    Log.d(LOG_TAG, "onCreateLoader");

    if (mUri != null) {
      // Now create and return a CursorLoader that will take care of
      // creating a Cursor for the data being displayed.
      return new CursorLoader(
              getActivity(),
              mUri,
              DETAIL_COLUMNS,
              null,
              null,
              null
      );
    }
    return null;
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
    Log.d(LOG_TAG, "onLoadFinished");

    if (cursor != null && cursor.moveToFirst()) {
      String name = cursor.getString(COL_QUOTE_NAME);
      getActivity().setTitle(name);

      String bidText = cursor.getString(COL_QUOTE_BIDPRICE);
      mBidView.setText(bidText);

      String symbolText = cursor.getString(COL_QUOTE_SYMBOL);
      mSymbolView.setText(String.format(getActivity()
              .getString(R.string.format_symbol_display), symbolText));

      String changeText = cursor.getString(COL_QUOTE_CHANGE);
      String changePercentText = cursor.getString(COL_QUOTE_PERCENT_CHANGE);
      mChangeView.setText(String.format(getActivity()
              .getString(R.string.format_change_bid_display),changeText, changePercentText));

      if(cursor.getInt(COL_QUOTE_ISUP) == 1){
        mChangeView.setTextColor(getColorResource(R.color.material_green_A200));
      } else {
        mChangeView.setTextColor(getColorResource(R.color.material_red_A200));
      }


      String prevCloseText = cursor.getString(COL_QUOTE_PREV_CLOSE);
      mPrevCloseView.setText(prevCloseText);

      String openText = cursor.getString(COL_QUOTE_OPEN_BID);
      mOpenView.setText(openText);

      String dayLowText = cursor.getString(COL_QUOTE_DAY_LOW);
      mDayLowView.setText(dayLowText);

      String dayHighText = cursor.getString(COL_QUOTE_DAY_HIGH);
      mDayHighView.setText(dayHighText);

      String yearLowText = cursor.getString(COL_QUOTE_YEAR_LOW);
      mYearLowView.setText(yearLowText);

      String yearHighText = cursor.getString(COL_QUOTE_YEAR_HIGH);
      mYearHighView.setText(yearHighText);
    }
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    Log.d(LOG_TAG, "onLoaderReset");
  }

  public int getColorResource(int colorId){
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      return getResources().getColor(colorId);
    } else {
      return getResources().getColor(colorId, null);
    }
  }

  public int findMin(float[] array){
    float min=array[0];

    for (int i = 1; i < array.length; i++) {
      if (array[i] < min) {
        min = array[i];
      }
    }

    return (int)min;
  }

  public int findMax(float[] array){
    float max=array[0];

    for (int i = 1; i < array.length; i++) {
      if (array[i] > max) {
        max = array[i];
      }
    }

    return (int)max;
  }
}
