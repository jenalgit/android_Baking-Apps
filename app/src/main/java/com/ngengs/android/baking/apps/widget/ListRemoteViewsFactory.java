package com.ngengs.android.baking.apps.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.ngengs.android.baking.apps.R;

import java.util.ArrayList;

@SuppressWarnings("WeakerAccess")
public class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private final Context mContext;
    private final ArrayList<String> mDataIngredient;
    private final ArrayList<String> mDataQuantity;

    ListRemoteViewsFactory(Context mContext, Intent intent) {
        this.mContext = mContext;
        mDataIngredient = intent.getStringArrayListExtra("INGREDIENT");
        mDataQuantity = intent.getStringArrayListExtra("QUANTITY");
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return (mDataIngredient != null) ? mDataIngredient.size() : 0;
    }

    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_ingredient);
        views.setTextViewText(R.id.text_ingredient_name, mDataIngredient.get(i));
        views.setTextViewText(R.id.text_ingredient_size, mDataQuantity.get(i));
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}