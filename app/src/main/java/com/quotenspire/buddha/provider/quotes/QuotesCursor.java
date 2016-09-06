package com.quotenspire.buddha.provider.quotes;

import java.util.Date;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.quotenspire.buddha.provider.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code quotes} table.
 */
public class QuotesCursor extends AbstractCursor implements QuotesModel {
    public QuotesCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    public long getId() {
        Long res = getLongOrNull(QuotesColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Uid
     */
    public int getUid() {
        Integer res = getIntegerOrNull(QuotesColumns.UID);
        if (res == null)
            throw new NullPointerException("The value of 'uid' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Quote
     * Cannot be {@code null}.
     */
    @NonNull
    public String getQuote() {
        String res = getStringOrNull(QuotesColumns.QUOTE);
        if (res == null)
            throw new NullPointerException("The value of 'quote' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * status
     */
    public boolean getStatus() {
        Boolean res = getBooleanOrNull(QuotesColumns.STATUS);
        if (res == null)
            throw new NullPointerException("The value of 'status' in the database was null, which is not allowed according to the model definition");
        return res;
    }
}
