package com.therahulmeena.kryptoscope;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.facebook.stetho.Kryptoscope;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitializeSQLCipher();
        Kryptoscope.initializeWithDefaults(this,"test123");
    }

    private void InitializeSQLCipher() {
        SQLiteDatabase.loadLibs(this);
        File databaseFile = getDatabasePath("demo.db");
        databaseFile.mkdirs();
        databaseFile.delete();
        SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFile, "test123", null);
        database.execSQL("create table t1(a, b)");
        database.execSQL("insert into t1(a, b) values(?, ?)", new Object[]{"one for the money",
                "two for the show"});
        database.close();
    }
}
