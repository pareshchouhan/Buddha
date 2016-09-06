package com.quotenspire.buddha.provider.quotes;

import android.net.Uri;
import android.provider.BaseColumns;

import com.quotenspire.buddha.provider.QuoteProvider;
import com.quotenspire.buddha.provider.quotes.QuotesColumns;

/**
 * quotes.
 */
public class QuotesColumns implements BaseColumns {
    public static final String TABLE_NAME = "quotes";
    public static final Uri CONTENT_URI = Uri.parse(QuoteProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    /**
     * Uid
     */
    public static final String UID = "uid";

    /**
     * Quote
     */
    public static final String QUOTE = "quote";

    /**
     * status
     */
    public static final String STATUS = "status";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            UID,
            QUOTE,
            STATUS
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(UID) || c.contains("." + UID)) return true;
            if (c.equals(QUOTE) || c.contains("." + QUOTE)) return true;
            if (c.equals(STATUS) || c.contains("." + STATUS)) return true;
        }
        return false;
    }

}
