package com.robertkiszelirk.guardiannewsapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

class ArticleAdapter extends ArrayAdapter<BaseArticleData> {
    private static final String LOG_TAG = ArticleAdapter.class.getSimpleName();

    // Get article list
    ArticleAdapter(Context context, List<BaseArticleData> articleList) {
        super(context, -1, articleList);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get list view
        View listItemView = convertView;
        // Check if list view exists if not create
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.item_list_layout, parent, false);
        }
        // Get current article
        BaseArticleData currentArticle = getItem(position);
        // If article is not null
        if (currentArticle != null) {
            // Set image
            ImageView articleThumbnail = (ImageView) listItemView.findViewById(R.id.article_thumbnail_image_view);
            articleThumbnail.setImageBitmap(formatImageFromBitmap(currentArticle.getArticleThumbnail()));
            // Set section
            TextView section = (TextView) listItemView.findViewById(R.id.article_section_text_view);
            section.setText(currentArticle.getArticleSection());
            // Set title
            TextView title = (TextView) listItemView.findViewById(R.id.article_title_text_view);
            title.setText(currentArticle.getArticleTitle());
            // Set publish time
            TextView publishTime = (TextView) listItemView.findViewById(R.id.article_publish_time_text_view);
            publishTime.setText(formatPublishTime(currentArticle.getArticlePublishTime()));
            // Set contributor
            TextView contributor = (TextView) listItemView.findViewById(R.id.article_contributor_text_view);
            contributor.setText(currentArticle.getArticleContributorName());
        }
        // Return list item
        return listItemView;
    }

    // Format publish date
    private String formatPublishTime(final String time) {
        // If not the correct base format
        String rTime = "N.A.";
        // Check time validation
        if ((time != null) && (!time.isEmpty())) {
            try {
                // Create current format
                SimpleDateFormat currentSDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                // Create new format
                SimpleDateFormat newSDF = new SimpleDateFormat("yyyy.MM.dd / HH:mm");
                // Parse time
                rTime = newSDF.format(currentSDF.parse(time));
            } catch (ParseException parseEx) {
                // If an error occurs don't stop the app
                rTime = "N.A.";
                // But log the error
                Log.e(LOG_TAG, "Error while parsing the published date", parseEx);
            }
        }

        return rTime;
    }

    // Get the thumbnail image
    private Bitmap formatImageFromBitmap(Bitmap articleThumbnail) {
        // Bitmap for image
        Bitmap returnBitmap;
        // Check thumbnail valid
        if (articleThumbnail == null) {
            // If not valid return default image
            returnBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.noimage);
        } else {
            // If valid return image
            returnBitmap = articleThumbnail;
        }
        // Return bitmap
        return returnBitmap;
    }
}
