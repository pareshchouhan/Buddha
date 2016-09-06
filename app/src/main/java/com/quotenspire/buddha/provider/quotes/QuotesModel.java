package com.quotenspire.buddha.provider.quotes;

import com.quotenspire.buddha.provider.base.BaseModel;

import java.util.Date;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * quotes.
 */
public interface QuotesModel extends BaseModel {

    /**
     * Uid
     */
    int getUid();

    /**
     * Quote
     * Cannot be {@code null}.
     */
    @NonNull
    String getQuote();

    /**
     * status
     */
    boolean getStatus();
}
