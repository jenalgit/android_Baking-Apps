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

package com.ngengs.android.baking.apps;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
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

import timber.log.Timber;

public class RecipeActivity extends AppCompatActivity
        implements RecipeFragment.OnFragmentInteractionListener {
    private FrameLayout mFragmentStepLayout;
    private Guideline mGuideline;
    private ConstraintLayout mConstraintLayoutRoot;

    private Recipe mData;
    private StepFragment mStepFragment;
    private RecipeFragment mRecipeFragment;
    private boolean mOpenMultiLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        FrameLayout mFragmentRecipeLayout = findViewById(R.id.fragment_recipe);
        mFragmentStepLayout = findViewById(R.id.fragment_step);
        mGuideline = findViewById(R.id.guideline);
        mConstraintLayoutRoot = findViewById(R.id.constraint_layout_root);

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
