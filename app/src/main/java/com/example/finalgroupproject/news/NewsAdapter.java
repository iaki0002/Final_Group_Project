package com.example.finalgroupproject.news;
/*
Author: David Lee
Student Number: 040959646
Course: CST2335
Date: Dec 02, 2019
 */

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.finalgroupproject.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;


/**
 * the adapter for the news listview
 */
public class NewsAdapter extends BaseAdapter{
    ArrayList<Articles> articles;
    Activity context;


    /**
     * @param articles news articles objects
     * @param context activity object
     */
    public NewsAdapter(ArrayList<Articles> articles, Activity context) {
        this.articles = articles;
        this.context = context;
    }


    /**
     * @return the number of articles size
     */
    @Override
    public int getCount(){return articles.size();}


    /**
     * @param i row position
     * @return what to show at row position
     */
    @Override
    public Object getItem(int i) {return articles.get(i);}


    /**
     * @param i row position
     * @return the database id of the item at position i
     */
    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * @param i row position i
     * @param view view object
     * @param viewGroup viewgroup object
     * @return  View object to go in a row of the ListView
     */
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ArticlesViewHolder holder = new ArticlesViewHolder();
        View newView = view;

        LayoutInflater articleInflator = context.getLayoutInflater();
        Articles article = articles.get(i);

        newView = articleInflator.inflate(R.layout.news_table_row_layout, null);
        holder.title = (TextView) newView.findViewById(R.id.titleview);
        holder.description = (TextView) newView.findViewById(R.id.descview);
        holder.image = (ImageView) newView.findViewById(R.id.imageview);
        newView.setTag(holder);
        holder.title.setText(article.getTitle());
        holder.description.setText(article.getDescription());
        Picasso.get().load(article.getImage()).into(holder.image);

        return newView;
    }

    /**
     * the holder class for articles layout objects
     */
    private class ArticlesViewHolder{
        public TextView title;
        public TextView description;
        public ImageView image;

    }


}
