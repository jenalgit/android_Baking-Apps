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

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Guideline;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.ngengs.android.baking.apps.data.Recipe;
import com.ngengs.android.baking.apps.fragments.RecipeFragment;
import com.ngengs.android.baking.apps.fragments.StepFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class RecipeActivity extends AppCompatActivity
        implements RecipeFragment.OnFragmentInteractionListener {

    @BindView(R.id.fragment_recipe)
    FrameLayout mFragmentRecipeLayout;
    @Nullable
    @BindView(R.id.fragment_step)
    FrameLayout mFragmentStepLayout;
    @Nullable
    @BindView(R.id.guideline)
    Guideline mGuideline;
    @Nullable
    @BindView(R.id.constraint_layout_root)
    ConstraintLayout mConstraintLayoutRoot;

    private Recipe mData;
    private StepFragment mStepFragment;
    private RecipeFragment mRecipeFragment;
    private boolean mOpenMultiLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        ButterKnife.bind(this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        mStepFragment = null;
        mOpenMultiLayout = false;


        if (mFragmentRecipeLayout != null) {
            if (savedInstanceState == null) {
                if (getIntent().getExtras() != null) {
                    mData = getIntent().getExtras().getParcelable("DATA");
                }
                if (mData != null) {
                    mRecipeFragment = RecipeFragment.newInstance(mData.getIngredients(),
                                                                 mData.getSteps());
                    fragmentManager.beginTransaction()
                                   .add(mFragmentRecipeLayout.getId(), mRecipeFragment)
                                   .commit();
                }
            } else {
                mData = savedInstanceState.getParcelable("DATA");
                //noinspection UnusedAssignment
                mRecipeFragment = (RecipeFragment) fragmentManager.findFragmentById(
                        mFragmentRecipeLayout.getId());
                if (mFragmentStepLayout != null) {
                    if (fragmentManager.findFragmentById(mFragmentStepLayout.getId()) != null) {
                        mStepFragment = (StepFragment) fragmentManager.findFragmentById(
                                mFragmentStepLayout.getId());
                    }
                }
            }

        }

        if (mData != null) {
            setTitle(mData.getName());
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (isMultiLayout()) {
            Timber.d("onCreate: %s", "create multi layout");
            mOpenMultiLayout = (savedInstanceState != null) && savedInstanceState.getBoolean(
                    "OPEN_MULTI", false);
            changeMultiLayout();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("DATA", mData);
        outState.putBoolean("OPEN_MULTI", mOpenMultiLayout);
        super.onSaveInstanceState(outState);
    }

    private boolean isMultiLayout() {
        return mFragmentStepLayout != null && mConstraintLayoutRoot != null && mGuideline != null
               && getResources().getBoolean(R.bool.isTablet);
    }

    @SuppressWarnings("ConstantConditions")
    private void changeMultiLayout() {
        if (isMultiLayout()) {
            ConstraintLayout.LayoutParams params
                    = (ConstraintLayout.LayoutParams) mGuideline.getLayoutParams();
            if (!mOpenMultiLayout) {
                mFragmentStepLayout.setVisibility(View.GONE);
                params.guidePercent = 1f;
            } else {
                mFragmentStepLayout.setVisibility(View.VISIBLE);
                params.guidePercent = (getResources().getConfiguration().orientation
                                       == Configuration.ORIENTATION_LANDSCAPE) ? 0.35f : 0.5f;
            }
            mGuideline.setLayoutParams(params);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onRecipeStepClick(int position) {
        Timber.d("onRecipeStepClick: #%d %s", position,
                 mData.getSteps().get(position).getShortDescription());
        if (!isMultiLayout()) {
            Timber.d("onRecipeStepClick: %s", "click in phone");
            Intent intent = new Intent(this, StepActivity.class);
            intent.putParcelableArrayListExtra("DATA", new ArrayList<>(mData.getSteps()));
            intent.putExtra("POSITION", position);
            startActivity(intent);
        } else {
            Timber.d("onRecipeStepClick: %s", "click on tablet");
            boolean changeFragment = true;
            FragmentManager mFragmentManager = getSupportFragmentManager();
            if (mStepFragment != null) {
                StepFragment temp = (StepFragment) mFragmentManager.findFragmentById(
                        mFragmentStepLayout.getId());
                if (temp.getStepId() == mData.getSteps().get(position).getId()) {
                    changeFragment = false;
                }
            }
            if (changeFragment) {
                Timber.d("onRecipeStepClick: %s", "click on tablet and change fragment");
                if (mRecipeFragment != null) {
                    mRecipeFragment.changeSelectedStep(position);
                }
                mStepFragment = StepFragment.newInstance(mData.getSteps().get(position), false);
                mFragmentManager.beginTransaction()
                                .replace(mFragmentStepLayout.getId(), mStepFragment)
                                .commit();
                if (!mOpenMultiLayout) {
                    mOpenMultiLayout = true;
                    changeMultiLayout();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!isMultiLayout()) {
            super.onBackPressed();
        } else {
            if (mOpenMultiLayout) {
                Timber.d("onBackPressed: %s", "close the detail step");
                if (mRecipeFragment != null) {
                    mRecipeFragment.changeSelectedStep(-1);
                }
                if (mStepFragment != null) {
                    mStepFragment.releasePlayer();
                }
                mOpenMultiLayout = false;
                changeMultiLayout();
            } else {
                super.onBackPressed();
            }
        }
    }
}
