/*******************************************************************************
 * Copyright (c) 2017 Rizky Kharisma (@ngengs)
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/

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