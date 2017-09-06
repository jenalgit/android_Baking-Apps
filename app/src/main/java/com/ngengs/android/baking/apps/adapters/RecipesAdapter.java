/*******************************************************************************
 * Copyright (c) 2017 Rizky Kharisma (@ngengs)
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ngengs.android.baking.apps.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ngengs.android.baking.apps.R;
import com.ngengs.android.baking.apps.data.Recipe;
import com.ngengs.android.baking.apps.remotes.Connection;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder> {

    private final List<Recipe> mData;
    private final Context mContext;
    private final OnClickListener mClickListener;

    public RecipesAdapter(Context mContext, OnClickListener mClickListener) {
        this.mContext = mContext;
        this.mData = new ArrayList<>();
        this.mClickListener = mClickListener;
    }

    @Override
    public RecipesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.item_recipes, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecipesAdapter.ViewHolder holder, int position) {
        Recipe recipe = mData.get(position);
        String recipeName = recipe.getName();
        if (TextUtils.isEmpty(recipe.getImage())) {
            if (recipeName.replaceAll("\\s", "").equalsIgnoreCase("nutellapie")) {
                recipe.setImage(Connection.FALLBACK_IMAGE_NUTELLA_PIE);
            } else if (recipeName.replaceAll("\\s", "").equalsIgnoreCase("brownies")) {
                recipe.setImage(Connection.FALLBACK_IMAGE_BROWNIES);
            } else if (recipeName.replaceAll("\\s", "").equalsIgnoreCase("yellowcake")) {
                recipe.setImage(Connection.FALLBACK_IMAGE_YELLOW_CAKE);
            } else {
                recipe.setImage(Connection.FALLBACK_IMAGE_CHEESE_CAKE);
            }
            mData.set(position, recipe);
        }
        Glide.with(mContext).load(recipe.getImage()).thumbnail(0.05f).into(holder.mImageRecipe);
        holder.mTextRecipe.setText(recipeName);
        holder.mTextServing.setText(String.valueOf(recipe.getServings()));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void addData(List<Recipe> data) {
        if (data != null) {
            int position = mData.size();
            mData.addAll(data);
            notifyItemRangeInserted(position, data.size());
        }
    }

    public List<Recipe> getData() {
        return new ArrayList<>(mData);
    }

    public interface OnClickListener {
        void onClick(Recipe recipe);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image_recipe)
        ImageView mImageRecipe;
        @BindView(R.id.text_recipe)
        TextView mTextRecipe;
        @BindView(R.id.text_serving)
        TextView mTextServing;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.card_view_recipe)
        void clickRecipeCard() {
            mClickListener.onClick(mData.get(getAdapterPosition()));
        }
    }
}
