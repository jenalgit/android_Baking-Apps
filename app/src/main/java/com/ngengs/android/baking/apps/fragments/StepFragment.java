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

package com.ngengs.android.baking.apps.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.ngengs.android.baking.apps.R;
import com.ngengs.android.baking.apps.data.Step;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class StepFragment extends Fragment {
    private static final String ARG_STEP_DATA = "PARAM_STEP";
    private static final String ARG_STEP_FULLSCREEN = "PARAM_FULL";
    @BindView(R.id.step_video_player)
    SimpleExoPlayerView mStepExoPlayerView;
    @Nullable
    @BindView(R.id.step_image_thumbnail)
    ImageView mStepImageThumbnail;
    @Nullable
    @BindView(R.id.step_description)
    TextView mStepDescription;
    private Unbinder unbinder;

    private SimpleExoPlayer mExoPlayer;
    private Step mData;
    private boolean mFullScreen;

    public StepFragment() {
        // Required empty public constructor
    }

    public static StepFragment newInstance(Step step, boolean fullScreen) {
        StepFragment fragment = new StepFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_STEP_DATA, step);
        args.putBoolean(ARG_STEP_FULLSCREEN, fullScreen);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mData = getArguments().getParcelable(ARG_STEP_DATA);
            mFullScreen = getArguments().getBoolean(ARG_STEP_FULLSCREEN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(
                (mFullScreen ? R.layout.fragment_step_fullscreen : R.layout.fragment_step),
                container, false);
        unbinder = ButterKnife.bind(this, view);
        if (savedInstanceState != null) {
            mData = savedInstanceState.getParcelable("DATA");
        }
        if (mStepDescription != null && mData != null)
            mStepDescription.setText(mData.getDescription());
        if (mStepImageThumbnail != null && mData != null &&
            !TextUtils.isEmpty(mData.getThumbnailURL())) {
            mStepImageThumbnail.setVisibility(View.VISIBLE);
            Glide.with(this)
                 .load(mData.getThumbnailURL())
                 .thumbnail(0.5f)
                 .into(mStepImageThumbnail);
        }
        if (mData != null && !TextUtils.isEmpty(mData.getVideoURL())) {
            mStepExoPlayerView.setVisibility(View.VISIBLE);
            initializePlayer(getContext(), Uri.parse(mData.getVideoURL()));
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("DATA", mData);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        releasePlayer();
        unbinder.unbind();
    }

    private void initializePlayer(Context context, Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
            mStepExoPlayerView.setPlayer(mExoPlayer);
            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(context, "BakingAppPlayer");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri,
                                                               new DefaultDataSourceFactory(
                                                                       context, userAgent),
                                                               new DefaultExtractorsFactory(), null,
                                                               null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    public boolean isFullScreen() {
        return mFullScreen;
    }

    public void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    public int getStepId() {
        return mData.getId();
    }

}
