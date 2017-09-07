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
import android.support.annotation.NonNull;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.ViewHolder> {
    private final List<Step> mData;
    private final Context mContext;
    private final StepAdapter.OnClickListener mClickListener;
    private int selectedPosition;

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

    public void indicatorSelected(int newPosition) {
        Timber.d("indicatorSelected() called with: newPosition = [%s]", newPosition);
        int temp = selectedPosition;
        selectedPosition = newPosition;
        if (temp >= 0) notifyItemChanged(temp);
        if (selectedPosition >= 0) notifyItemChanged(selectedPosition);
    }

    public interface OnClickListener {
        void onClickStep(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.stepImageThumbnail)
        ImageView mStepImageThumbnail;
        @BindView(R.id.indicatorVideo)
        ImageView mIndicatorVideo;
        @BindView(R.id.stepNumber)
        TextView mStepNumber;
        @BindView(R.id.stepShortDescription)
        TextView mStepShortDescription;
        @BindView(R.id.indicatorSelected)
        View mIndicatorSelected;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.card_view_step)
        void onClickCardStep() {
            mClickListener.onClickStep(getAdapterPosition());
        }
    }
}
