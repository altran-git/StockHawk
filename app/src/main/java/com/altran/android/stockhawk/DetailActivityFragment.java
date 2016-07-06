package com.altran.android.stockhawk;

import android.support.v4.app.Fragment;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.altran.android.stockhawk.data.QuoteColumns;
import com.altran.android.stockhawk.data.QuoteDatabase;

/**
 * Created by ND on 7/5/2016.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

  private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
  private final static int DETAIL_LOADER = 0;
  static final String DETAIL_URI = "URI";

  private Uri mUri;

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

    return rootView;
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
      String bidText = cursor.getString(COL_QUOTE_BIDPRICE);
      mBidView.setText(bidText);

      String symbolText = cursor.getString(COL_QUOTE_SYMBOL);
      mSymbolView.setText(String.format(getActivity()
              .getString(R.string.format_symbol_display), symbolText));

      String changeText = cursor.getString(COL_QUOTE_CHANGE);
      String changePercentText = cursor.getString(COL_QUOTE_PERCENT_CHANGE);
      mChangeView.setText(String.format(getActivity()
              .getString(R.string.format_change_bid_display),changeText, changePercentText));

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
}
