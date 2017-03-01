package com.robertkiszelirk.guardiannewsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

/**
 * Created by kiszeli on 2017.02.23..
 */

public class ArticleLoader extends AsyncTaskLoader {

    private Bundle pBundle;
    // Article loader
    public ArticleLoader(final Context context, final Bundle bundle){
        super(context);
        pBundle = bundle;
    }
    // Start loading article
    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    @Nullable
    public Object loadInBackground() {
        // Create base article list
        List<BaseArticleData> articleList = null;
        // If bundle has data get articles
        if(pBundle != null){
            articleList = QueryUtils.fetchArticleData(pBundle.getString("uri"));
        }
        // Return articles list
        return articleList;
    }
}
