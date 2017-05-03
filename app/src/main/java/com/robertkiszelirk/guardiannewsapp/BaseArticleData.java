package com.robertkiszelirk.guardiannewsapp;

// THIS CLASS STORES THE BASE DATA FOR EACH NEWS ARTICLE

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

class BaseArticleData {

    //FOR THE NEWS TITLE STRING
    private final String articleTitle;

    //FOR THE NEWS SECTION STRING
    private final String articleSection;

    //FOR THE CONTRIBUTOR NAME;
    private final  String articleContributorName;

    //FOR THE NEWS THUMBNAIL
    private final Bitmap articleThumbnail;

    //FOR THE PUBLISH TIME
    private final String articlePublishTime;

    //FOR THE NEWS ARTICLE
    private final String articleURL;

    //CREATES A BASE ARTICLE DATA OBJECT
    public BaseArticleData(String articleSection, String articleTitle, String articleContributorName,
                        Bitmap articleThumbnail,String articlePublishTime, String articleURL){

        //SETTING THE OBJECT DATA
        this.articleTitle = articleTitle;
        this.articleSection = articleSection;
        this.articleContributorName = articleContributorName;
        this.articleThumbnail = articleThumbnail;
        this.articlePublishTime = articlePublishTime;
        this.articleURL = articleURL;

    }

    //RETURNS THE ARTICLE TITLE
    @Nullable
    public String getArticleTitle() {
        return articleTitle;
    }

    //RETURNS THE ARTICLE SECTION
    @Nullable
    public String getArticleSection() {
        return articleSection;
    }

    //RETURNS THE ARTICLE CONTRIBUTOR NAME
    @Nullable
    public String getArticleContributorName() {return articleContributorName;}

    //RETURNS THE ARTICLE THUMBNAIL
    @Nullable
    public Bitmap getArticleThumbnail() {
        return articleThumbnail;
    }

    //RETURNS THE ARTICLE PUBLISH TIME
    @Nullable
    public String getArticlePublishTime() {
        return articlePublishTime;
    }

    //RETURNS THE ARTICLE URL
    @Nullable
    public String getArticleURL() {
        return articleURL;
    }
}
