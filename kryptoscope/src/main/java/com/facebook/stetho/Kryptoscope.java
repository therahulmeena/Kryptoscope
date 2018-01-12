package com.facebook.stetho;

import android.content.Context;

/**
 * Created by rahul on 12/1/18.
 */
public class Kryptoscope {
    private Kryptoscope() {
    }

    /**
     * Start the listening server, providing a custom implementation to support SQLCipher Database
     *
     * @param context the context.
     * @param sqlCipherDBKey the sqlCipher database key
     */
    public static void initializeWithDefaults(final Context context, final String sqlCipherDBKey) {
        Stetho.initializeWithDefaults(context,sqlCipherDBKey);
    }
}
