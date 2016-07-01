package com.altran.android.stockhawk.data;

import android.database.sqlite.SQLiteDatabase;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.OnCreate;
import net.simonvt.schematic.annotation.OnUpgrade;

/**
 * Created by sam_chordas on 10/5/15.
 * Updated by altran
 */
@Database(fileName = "quotes.db", version = QuoteDatabase.VERSION)
public class QuoteDatabase {
  private QuoteDatabase(){}

  public static final String QUOTES = "quotes";
  public static final int VERSION = 7;

  @OnCreate public static void onCreate(SQLiteDatabase db) {
    final String SQL_CREATE_QUOTES_TABLE = "CREATE TABLE " + QUOTES + " (" +
            QuoteColumns._ID + " INTEGER PRIMARY KEY, " +
            QuoteColumns.SYMBOL + " INTEGER NOT NULL, " +
            QuoteColumns.PERCENT_CHANGE + " TEXT NOT NULL, " +
            QuoteColumns.CHANGE + " TEXT NOT NULL, " +
            QuoteColumns.BIDPRICE + " TEXT NOT NULL, " +
            QuoteColumns.CREATED + " TEXT, " +
            QuoteColumns.ISUP + " INTEGER NOT NULL, " +
            QuoteColumns.ISCURRENT + " INTEGER, " +
            "UNIQUE (" + QuoteColumns.SYMBOL + ") ON CONFLICT REPLACE);";

    db.execSQL(SQL_CREATE_QUOTES_TABLE);
  }

  //@Table(QuoteColumns.class) public static final String QUOTES = "quotes";

  @OnUpgrade public static void upgrade(SQLiteDatabase db){
    db.execSQL("DROP TABLE IF EXISTS " + QUOTES);
    onCreate(db);
  }
}



