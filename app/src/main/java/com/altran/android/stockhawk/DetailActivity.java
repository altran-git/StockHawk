package com.altran.android.stockhawk;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


/**
 * Created by ND on 7/5/2016.
 */
public class DetailActivity extends AppCompatActivity{
  private static final String LOG_TAG = DetailActivity.class.getSimpleName();
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Log.d(LOG_TAG, "onCreate");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail);
    getSupportActionBar().setElevation(0f);

    if (savedInstanceState == null) {
      // Create the detail fragment and add it to the activity
      // using a fragment transaction.

      Bundle arguments = new Bundle();
      arguments.putParcelable(DetailActivityFragment.DETAIL_URI, getIntent().getData());

      DetailActivityFragment fragment = new DetailActivityFragment();
      fragment.setArguments(arguments);

      getSupportFragmentManager().beginTransaction()
              .add(R.id.stock_detail_container, fragment)
              .commit();
    }
  }
}
