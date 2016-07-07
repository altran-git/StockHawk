package com.altran.android.stockhawk.data;

/**
 * Created by ND on 7/6/2016.
 */
public class QuoteHistory {
  private String mDate;
  private float mCloseBid;

  public QuoteHistory(String date, float closeBid) {
    mDate = date;
    mCloseBid = closeBid;
  }

  public String getDate() {
    return mDate;
  }

  public void setDate(String date) {
    mDate = date;
  }

  public float getCloseBid() {
    return mCloseBid;
  }

  public void setCloseBid(float closeBid) {
    mCloseBid = closeBid;
  }
}
