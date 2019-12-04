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
import android.widget.TextView;

import com.example.finalgroupproject.R;

import java.util.ArrayList;

/**
 * the adapter for the news listview
 */
public class SavedAdapter extends BaseAdapter{
    ArrayList<Articles> articles;
    Activity context;

    /**
     * @param articles saved articles objects
     * @param context activity object
     */
    public SavedAdapter(ArrayList<Articles> articles, Activity context) {
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

        newView = articleInflator.inflate(R.layout.savedlist_row, null);
        holder.title = (TextView) newView.findViewById(R.id.savedarticle);
        newView.setTag(holder);
        holder.title.setText(article.getTitle());


        return newView;
    }

    /**
     * the holder class for articles layout objects
     */
    private class ArticlesViewHolder{
        public TextView title;

    }


}