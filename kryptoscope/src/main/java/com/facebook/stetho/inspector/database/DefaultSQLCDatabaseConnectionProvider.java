/*
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.facebook.stetho.inspector.database;


import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteException;

import java.io.File;

/**
 * Opens the requested database using
 * {@link SQLiteDatabase#openDatabase(String, String, SQLiteDatabase.CursorFactory, int)} directly.
 * <p/>
 * <p>It is intended that this class be subclassed to enable/disable features via
 * {@link #determineOpenOptions(File)}</p>
 */
public class DefaultSQLCDatabaseConnectionProvider implements SQLCDatabaseConnectionProvider {
    String sqlCipherDBKey;

    public DefaultSQLCDatabaseConnectionProvider(String sqlCipherDBKey) {
        this.sqlCipherDBKey = sqlCipherDBKey;
    }

    @Override
    public SQLiteDatabase openDatabase(File databaseFile) throws SQLiteException {
        return performOpen(
                databaseFile,
                determineOpenOptions(databaseFile));
    }

    /**
     * Subclassing this function is intended to provide custom open behaviour on a per-file basis.
     */
    protected
    @SQLCDatabaseCompat.SQLCOpenOptions
    int determineOpenOptions(File databaseFile) {
        @SQLCDatabaseCompat.SQLCOpenOptions int flags = 0;

        // Try to guess if we should be using write-ahead logging.  If this heuristic fails
        // developers are expected to subclass this provider and explicitly assert the connection.
        File walFile = new File(databaseFile.getParent(), databaseFile.getName() + "-wal");
        if (walFile.exists()) {
            flags |= SQLCDatabaseCompat.ENABLE_WRITE_AHEAD_LOGGING;
        }

        return flags;
    }

    /**
     * Perform the open per the options provided in {@link #determineOpenOptions(File)}.
     * Subclassing is supported however this typically indicates a missing feature of some kind
     * in {@link SQLiteDatabaseCompat} that should be patched in Stetho itself.
     */
    protected SQLiteDatabase performOpen(File databaseFile, @SQLCDatabaseCompat.SQLCOpenOptions int options) {
        int flags = SQLiteDatabase.OPEN_READWRITE;

        SQLCDatabaseCompat compatInstance = SQLCDatabaseCompat.getInstance();
        flags |= compatInstance.provideOpenFlags(options);

        SQLiteDatabase db = SQLiteDatabase.openDatabase(
                databaseFile.getAbsolutePath(), sqlCipherDBKey,
                null /* cursorFactory */,
                flags);
        compatInstance.enableFeatures(options, db);
        return db;
    }
}
