package com.quotenspire.buddha.provider.quotes;

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.quotenspire.buddha.provider.base.AbstractSelection;

/**
 * Selection for the {@code quotes} table.
 */
public class QuotesSelection extends AbstractSelection<QuotesSelection> {
    @Override
    protected Uri baseUri() {
        return QuotesColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code QuotesCursor} object, which is positioned before the first entry, or null.
     */
    public QuotesCursor query(ContentResolver contentResolver, String[] projection) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new QuotesCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, null)}.
     */
    public QuotesCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null);
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param context The context to use for the query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code QuotesCursor} object, which is positioned before the first entry, or null.
     */
    public QuotesCursor query(Context context, String[] projection) {
        Cursor cursor = context.getContentResolver().query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new QuotesCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(context, null)}.
     */
    public QuotesCursor query(Context context) {
        return query(context, null);
    }


    public QuotesSelection id(long... value) {
        addEquals("quotes." + QuotesColumns._ID, toObjectArray(value));
        return this;
    }

    public QuotesSelection idNot(long... value) {
        addNotEquals("quotes." + QuotesColumns._ID, toObjectArray(value));
        return this;
    }

    public QuotesSelection orderById(boolean desc) {
        orderBy("quotes." + QuotesColumns._ID, desc);
        return this;
    }

    public QuotesSelection orderById() {
        return orderById(false);
    }

    public QuotesSelection uid(int... value) {
        addEquals(QuotesColumns.UID, toObjectArray(value));
        return this;
    }

    public QuotesSelection uidNot(int... value) {
        addNotEquals(QuotesColumns.UID, toObjectArray(value));
        return this;
    }

    public QuotesSelection uidGt(int value) {
        addGreaterThan(QuotesColumns.UID, value);
        return this;
    }

    public QuotesSelection uidGtEq(int value) {
        addGreaterThanOrEquals(QuotesColumns.UID, value);
        return this;
    }

    public QuotesSelection uidLt(int value) {
        addLessThan(QuotesColumns.UID, value);
        return this;
    }

    public QuotesSelection uidLtEq(int value) {
        addLessThanOrEquals(QuotesColumns.UID, value);
        return this;
    }

    public QuotesSelection orderByUid(boolean desc) {
        orderBy(QuotesColumns.UID, desc);
        return this;
    }

    public QuotesSelection orderByUid() {
        orderBy(QuotesColumns.UID, false);
        return this;
    }

    public QuotesSelection quote(String... value) {
        addEquals(QuotesColumns.QUOTE, value);
        return this;
    }

    public QuotesSelection quoteNot(String... value) {
        addNotEquals(QuotesColumns.QUOTE, value);
        return this;
    }

    public QuotesSelection quoteLike(String... value) {
        addLike(QuotesColumns.QUOTE, value);
        return this;
    }

    public QuotesSelection quoteContains(String... value) {
        addContains(QuotesColumns.QUOTE, value);
        return this;
    }

    public QuotesSelection quoteStartsWith(String... value) {
        addStartsWith(QuotesColumns.QUOTE, value);
        return this;
    }

    public QuotesSelection quoteEndsWith(String... value) {
        addEndsWith(QuotesColumns.QUOTE, value);
        return this;
    }

    public QuotesSelection orderByQuote(boolean desc) {
        orderBy(QuotesColumns.QUOTE, desc);
        return this;
    }

    public QuotesSelection orderByQuote() {
        orderBy(QuotesColumns.QUOTE, false);
        return this;
    }

    public QuotesSelection status(boolean value) {
        addEquals(QuotesColumns.STATUS, toObjectArray(value));
        return this;
    }

    public QuotesSelection orderByStatus(boolean desc) {
        orderBy(QuotesColumns.STATUS, desc);
        return this;
    }

    public QuotesSelection orderByStatus() {
        orderBy(QuotesColumns.STATUS, false);
        return this;
    }
}
