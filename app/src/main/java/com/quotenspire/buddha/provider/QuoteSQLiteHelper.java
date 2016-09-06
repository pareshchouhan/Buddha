package com.quotenspire.buddha.provider;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.DefaultDatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import com.quotenspire.buddha.BuildConfig;
import com.quotenspire.buddha.provider.quotes.QuotesColumns;

public class QuoteSQLiteHelper extends SQLiteOpenHelper {
    private static final String TAG = QuoteSQLiteHelper.class.getSimpleName();

    public static final String DATABASE_FILE_NAME = "quotenspire.db";
    private static final int DATABASE_VERSION = 1;
    private static QuoteSQLiteHelper sInstance;
    private final Context mContext;
    private final QuoteSQLiteHelperCallbacks mOpenHelperCallbacks;

    // @formatter:off
    public static final String SQL_CREATE_TABLE_QUOTES = "CREATE TABLE IF NOT EXISTS "
            + QuotesColumns.TABLE_NAME + " ( "
            + QuotesColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + QuotesColumns.UID + " INTEGER NOT NULL, "
            + QuotesColumns.QUOTE + " TEXT NOT NULL, "
            + QuotesColumns.STATUS + " INTEGER NOT NULL DEFAULT 0 "
            + ", CONSTRAINT unique_quote UNIQUE (quote) ON CONFLICT REPLACE"
            + " );";

    // @formatter:on

    public static QuoteSQLiteHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = newInstance(context.getApplicationContext());
        }
        return sInstance;
    }

    private static QuoteSQLiteHelper newInstance(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return newInstancePreHoneycomb(context);
        }
        return newInstancePostHoneycomb(context);
    }


    /*
     * Pre Honeycomb.
     */
    private static QuoteSQLiteHelper newInstancePreHoneycomb(Context context) {
        return new QuoteSQLiteHelper(context);
    }

    private QuoteSQLiteHelper(Context context) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);
        mContext = context;
        mOpenHelperCallbacks = new QuoteSQLiteHelperCallbacks();
    }


    /*
     * Post Honeycomb.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static QuoteSQLiteHelper newInstancePostHoneycomb(Context context) {
        return new QuoteSQLiteHelper(context, new DefaultDatabaseErrorHandler());
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private QuoteSQLiteHelper(Context context, DatabaseErrorHandler errorHandler) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION, errorHandler);
        mContext = context;
        mOpenHelperCallbacks = new QuoteSQLiteHelperCallbacks();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onCreate");
        mOpenHelperCallbacks.onPreCreate(mContext, db);
        db.execSQL(SQL_CREATE_TABLE_QUOTES);
        mOpenHelperCallbacks.onPostCreate(mContext, db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        mOpenHelperCallbacks.onOpen(mContext, db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        mOpenHelperCallbacks.onUpgrade(mContext, db, oldVersion, newVersion);
    }
}
