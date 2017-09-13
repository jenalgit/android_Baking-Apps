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

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ngengs.android.baking.apps.R;
import com.ngengs.android.baking.apps.data.Step;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.ViewHolder> {
    private final List<Step> mData;
    private final Context mContext;
    private final StepAdapter.OnClickListener mClickListener;
    private int selectedPosition;

    /**
     * Recycler View adapter for steps list.
     *
     * @param mContext
     *         Application or activity context
     * @param mData
     *         List of Step for adapter
     * @param mClickListener
     *         Item click listener
     */
    public StepAdapter(Context mContext, List<Step> mData,
                       StepAdapter.OnClickListener mClickListener) {
        this.mContext = mContext;
        this.mData = new ArrayList<>(mData);
        this.mClickListener = mClickListener;
        selectedPosition = -1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.item_step, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Step data = mData.get(position);
        holder.mStepNumber.setText(String.format("#%s", String.valueOf(position)));
        holder.mStepShortDescription.setText(data.getShortDescription());
        if (TextUtils.isEmpty(data.getVideoURL())) {
            holder.mIndicatorVideo.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(data.getThumbnailURL())) {
                loadImage(data.getThumbnailURL(), holder.mStepImageThumbnail);
            }
        } else {
            holder.mIndicatorVideo.setVisibility(View.VISIBLE);
            loadImage(data.getVideoURL(), holder.mStepImageThumbnail);
        }
        if (TextUtils.isEmpty(data.getVideoURL()) && TextUtils.isEmpty(data.getThumbnailURL())) {
            Glide.with(mContext).clear(holder.mStepImageThumbnail);
        }
        holder.mIndicatorSelected.setVisibility(
                (selectedPosition == position) ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    private void loadImage(@NonNull String imageUrl, ImageView imageView) {
        Glide.with(mContext).asBitmap().load(imageUrl).thumbnail(0.05f).into(imageView);
    }

    /**
     * Give selected indicator to the item in adapter.
     *
     * @param newPosition
     *         Position to give indicator, old indicator will be released.
     */
    public void indicatorSelected(int newPosition) {
        Timber.d("indicatorSelected() called with: newPosition = [%s]", newPosition);
        int temp = selectedPosition;
        selectedPosition = newPosition;
        if (temp >= 0) {
            notifyItemChanged(temp);
        }
        if (selectedPosition >= 0) {
            notifyItemChanged(selectedPosition);
        }
    }

    public interface OnClickListener {
        void onClickStep(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mStepImageThumbnail;
        private final ImageView mIndicatorVideo;
        private final TextView mStepNumber;
        private final TextView mStepShortDescription;
        private final View mIndicatorSelected;

        ViewHolder(View view) {
            super(view);
            mStepImageThumbnail = view.findViewById(R.id.stepImageThumbnail);
            mIndicatorVideo = view.findViewById(R.id.indicatorVideo);
            mStepNumber = view.findViewById(R.id.stepNumber);
            mStepShortDescription = view.findViewById(R.id.stepShortDescription);
            mIndicatorSelected = view.findViewById(R.id.indicatorSelected);
            CardView mCardView = view.findViewById(R.id.card_view_step);
            mCardView.setOnClickListener(
                    itemClick -> mClickListener.onClickStep(getAdapterPosition()));
        }
    }
}
