package com.quotenspire.buddha.provider.quotes;

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.quotenspire.buddha.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code quotes} table.
 */
public class QuotesContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return QuotesColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable QuotesSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(Context context, @Nullable QuotesSelection where) {
        return context.getContentResolver().update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * Uid
     */
    public QuotesContentValues putUid(int value) {
        mContentValues.put(QuotesColumns.UID, value);
        return this;
    }


    /**
     * Quote
     */
    public QuotesContentValues putQuote(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("quote must not be null");
        mContentValues.put(QuotesColumns.QUOTE, value);
        return this;
    }


    /**
     * status
     */
    public QuotesContentValues putStatus(boolean value) {
        mContentValues.put(QuotesColumns.STATUS, value);
        return this;
    }

}
