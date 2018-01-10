/*
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.facebook.stetho.inspector.database;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.IntDef;

import net.sqlcipher.database.SQLiteDatabase;

/**
 * Compatibility layer which supports opening databases with WAL and foreign key support
 * where supported.
 *
 * <p>For simplicity of implementation, all options are <em>ignored</em> prior to Honeycomb.</p>
 */
public abstract class SQLCDatabaseCompat {
  public static final int ENABLE_WRITE_AHEAD_LOGGING = 0x1;
  public static final int ENABLE_FOREIGN_KEY_CONSTRAINTS = 0x2;
  @IntDef(
      value = { ENABLE_WRITE_AHEAD_LOGGING, ENABLE_FOREIGN_KEY_CONSTRAINTS },
      flag = true)
  public @interface SQLCOpenOptions {}

  private static final SQLCDatabaseCompat sInstance;

  static {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      sInstance = new JellyBeanAndBeyondImpl();
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      sInstance = new HoneycombImpl();
    } else {
      sInstance = new NoopImpl();
    }
  }

  public static SQLCDatabaseCompat getInstance() {
    return sInstance;
  }

  public abstract int provideOpenFlags(@SQLCOpenOptions int openOptions);
  public abstract void enableFeatures(@SQLCOpenOptions int openOptions, SQLiteDatabase db);

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
  private static class JellyBeanAndBeyondImpl extends SQLCDatabaseCompat {
    @Override
    public int provideOpenFlags(@SQLCOpenOptions int openOptions) {
      int openFlags = 0;
      if ((openOptions & ENABLE_WRITE_AHEAD_LOGGING) != 0) {
        openFlags |= 1;
      }
      return openFlags;
    }

    @Override
    public void enableFeatures(@SQLCOpenOptions int openOptions, SQLiteDatabase db) {
      if ((openOptions & ENABLE_FOREIGN_KEY_CONSTRAINTS) != 0) {
        db.execSQL("PRAGMA foreign_keys = ON");
      }
    }
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  private static class HoneycombImpl extends SQLCDatabaseCompat {
    @Override
    public int provideOpenFlags(@SQLCOpenOptions int openOptions) {
      return 0;
    }

    @Override
    public void enableFeatures(@SQLCOpenOptions int openOptions, SQLiteDatabase db) {
      if ((openOptions & ENABLE_WRITE_AHEAD_LOGGING) != 0) {
        db.rawExecSQL("PRAGMA journal_mode=WAL;");
      }

      if ((openOptions & ENABLE_FOREIGN_KEY_CONSTRAINTS) != 0) {
        db.execSQL("PRAGMA foreign_keys = ON");
      }
    }
  }

  private static class NoopImpl extends SQLCDatabaseCompat {
    @Override
    public int provideOpenFlags(@SQLCOpenOptions int openOptions) {
      return 0;
    }

    @Override
    public void enableFeatures(@SQLCOpenOptions int openOptions, SQLiteDatabase db) {
    }
  }
}
