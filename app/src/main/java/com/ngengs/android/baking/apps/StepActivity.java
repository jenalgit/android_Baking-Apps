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

package com.ngengs.android.baking.apps;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ngengs.android.baking.apps.data.Step;
import com.ngengs.android.baking.apps.fragments.StepFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class StepActivity extends AppCompatActivity {
    @BindView(R.id.fragment_step)
    FrameLayout mFragmentStepLayout;
    @BindView(R.id.button_step_prev)
    ImageView mButtonStepPrev;
    @BindView(R.id.step_indicator)
    TextView mStepIndicator;
    @BindView(R.id.button_step_next)
    ImageView mButtonStepNext;
    @BindView(R.id.tools_step)
    View mToolsStep;


    private List<Step> mData;
    private int mActivePosition;
    private StepFragment mStepFragment;
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_step);
        ButterKnife.bind(this);

        mFragmentManager = getSupportFragmentManager();
        mActivePosition = 0;
        mData = new ArrayList<>();

        if (mFragmentStepLayout != null) {
            if (savedInstanceState == null) {
                if (getIntent().getExtras() != null) {
                    List<Step> temp = getIntent().getExtras().getParcelableArrayList("DATA");
                    if (temp != null) {
                        mData.addAll(temp);
                    }
                    mActivePosition = getIntent().getExtras().getInt("POSITION");
                }
                mStepFragment = StepFragment.newInstance(mData.get(mActivePosition),
                                                         isFullScreen());
                mFragmentManager.beginTransaction()
                                .add(mFragmentStepLayout.getId(), mStepFragment)
                                .commit();
            } else {
                List<Step> temp = savedInstanceState.getParcelableArrayList("DATA");
                if (temp != null) {
                    mData.addAll(temp);
                }
                mActivePosition = savedInstanceState.getInt("POSITION");
                mStepFragment = (StepFragment) mFragmentManager.findFragmentById(
                        mFragmentStepLayout.getId());
            }
        }

        changeStatusPrevNextButton();
        changeStep(mActivePosition, false);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("DATA", new ArrayList<Parcelable>(mData));
        outState.putInt("POSITION", mActivePosition);
        super.onSaveInstanceState(outState);
    }

    private void changeLayoutToFullscreen(boolean fullScreen) {
        if (fullScreen) {
            Timber.d("changeLayoutToFullscreen: %s", "landscape");
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                 WindowManager.LayoutParams.FLAG_FULLSCREEN);
            if (getSupportActionBar() != null) {
                getSupportActionBar().hide();
            }
        } else {
            Timber.d("changeLayoutToFullscreen: %s", "portrait");
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            if (getSupportActionBar() != null) {
                getSupportActionBar().show();
            }
        }
        mToolsStep.setVisibility(fullScreen ? View.GONE : View.VISIBLE);
    }

    private void changeStatusPrevNextButton() {
        Timber.d("changeStatusPrevNextButton: %s %s", mActivePosition, mData.size());
        boolean activePrev = mActivePosition > 0;
        mButtonStepPrev.setClickable(activePrev);
        mButtonStepPrev.setVisibility((activePrev) ? View.VISIBLE : View.INVISIBLE);
        boolean activeNext = mActivePosition < (mData.size() - 1);
        mButtonStepNext.setClickable(activeNext);
        mButtonStepNext.setVisibility((activeNext) ? View.VISIBLE : View.INVISIBLE);
    }

    @OnClick({R.id.button_step_prev, R.id.button_step_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button_step_prev:
                if (mActivePosition > 0) {
                    changeStep(mActivePosition - 1, false);
                }
                break;
            case R.id.button_step_next:
                if (mActivePosition < (mData.size() - 1)) {
                    changeStep(mActivePosition + 1, false);
                }
                break;
            default:
                Timber.e("onViewClicked: %s", "The view id in clicked view is not known");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeTitle(@NonNull String title) {
        setTitle(title);
    }

    private boolean isFullScreen() {
        boolean fullScreen = (!getResources().getBoolean(R.bool.isTablet)
                              && (getResources().getConfiguration().orientation
                                  == Configuration.ORIENTATION_LANDSCAPE)
                              && !TextUtils.isEmpty(mData.get(mActivePosition).getVideoURL()));
        Timber.d("isFullScreen() returned: %s", fullScreen);
        return fullScreen;
    }

    private void changeStep(int positionNow, boolean forceExitFullScreen) {
        Timber.d("changeStep() called with: positionNow = [%s], forceExitFullScreen = [%s]",
                 positionNow, forceExitFullScreen);
        mActivePosition = positionNow;
        mStepIndicator.setText(
                String.format("%s/%s", mActivePosition, (mData.size() - 1)));
        changeTitle(mData.get(mActivePosition).getShortDescription());
        changeStatusPrevNextButton();

        boolean fullScreen = !forceExitFullScreen && isFullScreen();

        mStepFragment = StepFragment.newInstance(mData.get(mActivePosition), fullScreen);
        mFragmentManager.beginTransaction()
                        .replace(mFragmentStepLayout.getId(), mStepFragment)
                        .commit();
        if (!TextUtils.isEmpty(mData.get(mActivePosition).getVideoURL())) {
            changeLayoutToFullscreen(fullScreen);
        }
    }

    @Override
    public void onBackPressed() {
        if (isFullScreen() && mStepFragment.isFullScreen()) {
            changeStep(mActivePosition, true);
        } else {
            super.onBackPressed();
        }
    }
}
