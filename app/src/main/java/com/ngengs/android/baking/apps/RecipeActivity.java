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
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.ngengs.android.baking.apps.data.Recipe;
import com.ngengs.android.baking.apps.fragments.RecipeFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class RecipeActivity extends AppCompatActivity
        implements RecipeFragment.OnFragmentInteractionListener {

    @BindView(R.id.fragment_recipe)
    FrameLayout mFragmentRecipeLayout;

    private Recipe mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        ButterKnife.bind(this);

        FragmentManager fragmentManager = getSupportFragmentManager();


        if (mFragmentRecipeLayout != null) {
            RecipeFragment mRecipeFragment;
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
            }

        }

        if (mData != null) {
            setTitle(mData.getName());
        }
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("DATA", mData);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) super.onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRecipeStepClick(int position) {
        Timber.d("onRecipeStepClick: #%d %s", position,
                 mData.getSteps().get(position).getShortDescription());
        if (!getResources().getBoolean(R.bool.isTablet)) {
            Intent intent = new Intent(this, StepActivity.class);
            intent.putExtra("DATA", mData);
            intent.putExtra("POSITION", position);
            startActivity(intent);
        }
    }
}
