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

package com.ngengs.android.baking.apps.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ngengs.android.baking.apps.R;
import com.ngengs.android.baking.apps.data.Ingredient;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {
    private final List<Ingredient> mData;

    public IngredientAdapter(List<Ingredient> mData) {
        this.mData = new ArrayList<>(mData);
    }

    @Override
    public IngredientAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.item_ingredient, parent, false);
        return new IngredientAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(IngredientAdapter.ViewHolder holder, int position) {
        holder.mTextIngredientName.setText(mData.get(position).getIngredient());
        holder.mTextIngredientSize.setText(String.format("%s %s", mData.get(position).getQuantity(),
                                                         mData.get(position).getMeasure()));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_ingredient_name)
        TextView mTextIngredientName;
        @BindView(R.id.text_ingredient_size)
        TextView mTextIngredientSize;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
