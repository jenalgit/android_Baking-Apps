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
import com.google.android.exoplayer2.C;
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
    private static final String ARG_STEP_VIDEO_POSITION = "PARAM_VIDEO_POSITION";
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
    private long mVideoLastPosition;
    private boolean mVideoPlayed;

    public StepFragment() {
        // Required empty public constructor
    }

    /**
     * Call or build the Step fragment with given params.
     *
     * @param step
     *         The step data to display
     * @param fullScreen
     *         If we need make full screnn layout
     *
     * @return The instance fragment
     */
    public static StepFragment newInstance(Step step, boolean fullScreen) {
        return newInstance(step, fullScreen, C.TIME_UNSET);
    }

    /**
     * Call or build the Step fragment with given params.
     *
     * @param step
     *         The step data to display
     * @param fullScreen
     *         If we need make full screnn layout
     * @param videoPositionStart
     *         Video start position
     *
     * @return The instance fragment
     */
    public static StepFragment newInstance(Step step, boolean fullScreen, long videoPositionStart) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_STEP_DATA, step);
        args.putBoolean(ARG_STEP_FULLSCREEN, fullScreen);
        args.putLong(ARG_STEP_VIDEO_POSITION, videoPositionStart);
        StepFragment fragment = new StepFragment();
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
        mVideoLastPosition = C.TIME_UNSET;
        mVideoPlayed = true;
        if (savedInstanceState != null) {
            mData = savedInstanceState.getParcelable("DATA");
            mVideoLastPosition = savedInstanceState.getLong("PLAYER_POSITION", C.TIME_UNSET);
            mVideoPlayed = savedInstanceState.getBoolean("PLAYER_STATUS", true);
        }
        if (mStepDescription != null && mData != null) {
            mStepDescription.setText(mData.getDescription());
        }
        if (mStepImageThumbnail != null && mData != null
            && !TextUtils.isEmpty(mData.getThumbnailURL())) {
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
        outState.putLong("PLAYER_POSITION", mVideoLastPosition);
        outState.putBoolean("PLAYER_STATUS", mVideoPlayed);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
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
            mExoPlayer.seekTo(mVideoLastPosition);
            mExoPlayer.setPlayWhenReady(mVideoPlayed);
        }
    }

    public boolean isFullScreen() {
        return mFullScreen;
    }

    public void setFullScreen(boolean fullScreen) {
        mFullScreen = fullScreen;
    }

    /**
     * Destroy or release video player when not need again.
     */
    public void releasePlayer() {
        if (mExoPlayer != null) {
            mVideoLastPosition = mExoPlayer.getCurrentPosition();
            mVideoPlayed = mExoPlayer.getPlayWhenReady();
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    public int getStepId() {
        return mData.getId();
    }

}
