package com.altran.android.stockhawk.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.ConflictResolutionType;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.Unique;

/**
 * Created by sam_chordas on 10/5/15.
 * Updated by altran
 */
public class QuoteColumns {
  @DataType(DataType.Type.INTEGER) @PrimaryKey @AutoIncrement
  public static final String _ID = "_id";
  @DataType(DataType.Type.TEXT) @NotNull @Unique(onConflict = ConflictResolutionType.REPLACE)
  public static final String SYMBOL = "symbol";
  @DataType(DataType.Type.TEXT) @NotNull
  public static final String NAME = "name";
  @DataType(DataType.Type.TEXT) @NotNull
  public static final String PERCENT_CHANGE = "percent_change";
  @DataType(DataType.Type.TEXT) @NotNull
  public static final String CHANGE = "change";
  @DataType(DataType.Type.TEXT) @NotNull
  public static final String BIDPRICE = "bid_price";
  @DataType(DataType.Type.TEXT) @NotNull
  public static final String PREV_CLOSE = "prev_close";
  @DataType(DataType.Type.TEXT) @NotNull
  public static final String OPEN_BID = "open_bid";
  @DataType(DataType.Type.TEXT) @NotNull
  public static final String DAY_LOW = "day_low";
  @DataType(DataType.Type.TEXT) @NotNull
  public static final String DAY_HIGH = "day_high";
  @DataType(DataType.Type.TEXT) @NotNull
  public static final String YEAR_LOW = "year_low";
  @DataType(DataType.Type.TEXT) @NotNull
  public static final String YEAR_HIGH = "year_high";
  @DataType(DataType.Type.INTEGER) @NotNull
  public static final String ISUP = "is_up";
}
