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

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.ngengs.android.baking.apps.adapters.RecipesAdapter;
import com.ngengs.android.baking.apps.data.Ingredient;
import com.ngengs.android.baking.apps.data.Recipe;
import com.ngengs.android.baking.apps.idlingresources.BakingIdlingResource;
import com.ngengs.android.baking.apps.remotes.Connection;
import com.ngengs.android.baking.apps.utils.ResourceHelpers;
import com.ngengs.android.baking.apps.widget.ListRemoteViewsService;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements Callback<List<Recipe>> {
    @BindView(R.id.recyclerRecipes)
    RecyclerView mRecyclerRecipes;
    @BindView(R.id.prompt_image)
    ImageView mPromptImage;
    @BindView(R.id.prompt_progress)
    ProgressBar mPromptProgress;
    @BindView(R.id.prompt_text)
    TextView mPromptText;
    @BindView(R.id.prompt_layout)
    LinearLayout mPromptLayout;


    private RecipesAdapter mAdapter;
    private int mAppWidgetId;

    @Nullable
    private BakingIdlingResource mIdlingResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        mAdapter = new RecipesAdapter(this, new RecipesAdapter.OnClickListener() {
            @Override
            public void onClick(Recipe recipe) {
                Timber.d("onClick: %s", recipe.getName());
                if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
                    Intent intent = new Intent(getBaseContext(), RecipeActivity.class);
                    intent.putExtra("DATA", recipe);
                    startActivity(intent);
                } else {
                    Timber.d("onClick: %s. Widget Id: %s", "Finishing widget configuration",
                             mAppWidgetId);
                    Context mContext = getApplicationContext();
                    RemoteViews views = new RemoteViews(getBaseContext().getPackageName(),
                                                        R.layout.widget_configured);
                    views.setTextViewText(R.id.widget_title, recipe.getName());
                    Intent ingredientWidgetListIntent = new Intent(mContext,
                                                                   ListRemoteViewsService.class);
                    ArrayList<String> dataIngredient = new ArrayList<>();
                    ArrayList<String> dataIngredientQuantity = new ArrayList<>();
                    for (Ingredient mIngredient : recipe.getIngredients()) {
                        dataIngredient.add(mIngredient.getIngredient());
                        dataIngredientQuantity.add(String.format("%s %s", mIngredient.getQuantity(),
                                                                 mIngredient.getMeasure()));
                    }
                    ingredientWidgetListIntent.putStringArrayListExtra("INGREDIENT",
                                                                       dataIngredient);
                    ingredientWidgetListIntent.putStringArrayListExtra("QUANTITY",
                                                                       dataIngredientQuantity);
                    views.setRemoteAdapter(R.id.widget_list, ingredientWidgetListIntent);
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
                    appWidgetManager.updateAppWidget(mAppWidgetId, views);
                    Intent intentWidget = new Intent();
                    intentWidget.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                    setResult(RESULT_OK, intentWidget);
                    finish();
                }
            }
        });
        mRecyclerRecipes.setHasFixedSize(true);
        int gridSpan;
        if (getResources().getBoolean(R.bool.isTablet)) {
            gridSpan = (getResources().getConfiguration().orientation
                        == Configuration.ORIENTATION_LANDSCAPE) ? 3 : 2;
        } else {
            gridSpan = (getResources().getConfiguration().orientation
                        == Configuration.ORIENTATION_LANDSCAPE) ? 2 : 1;
        }

        mRecyclerRecipes.setLayoutManager(new GridLayoutManager(this, gridSpan));
        mRecyclerRecipes.setAdapter(mAdapter);

        if (savedInstanceState != null) {
            Timber.d("onCreate: %s", "Instance State Not Empty");
            loadDataFromSavedInstanceState(savedInstanceState);
        } else {
            Timber.d("onCreate: %s", "New Launch");
            loadDataFromRemote();
        }
        Timber.d("onCreate: %s: %s", "App Widget Id", mAppWidgetId);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Timber.d("onCreate: %s", "Extras not empty");
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                                         AppWidgetManager.INVALID_APPWIDGET_ID);
            Timber.d("onCreate: %s: %s", "App Widget Id Now", mAppWidgetId);
        }
        getIdlingResource();
        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Timber.d("onSaveInstanceState() called with: outState = [%s]", outState);
        outState.putParcelableArrayList("DATA", new ArrayList<Parcelable>(mAdapter.getData()));
        super.onSaveInstanceState(outState);
    }

    private void loadDataFromSavedInstanceState(Bundle savedInstanceState) {
        List<Recipe> data = savedInstanceState.getParcelableArrayList("DATA");
        if (data != null) {
            if (data.size() > 0) {
                mAdapter.addData(data);

                if (mIdlingResource != null) {
                    mIdlingResource.setIdleState(true);
                }
            } else {
                loadDataFromRemote();
            }
        } else {
            Timber.w("loadDataFromSavedInstanceState: %s",
                     "Data from saved instance state is null");

            if (mIdlingResource != null) {
                mIdlingResource.setIdleState(true);
            }
        }
    }

    private void loadDataFromRemote() {
        mRecyclerRecipes.setVisibility(View.GONE);
        mPromptImage.setImageDrawable(
                ResourceHelpers.getDrawable(this, R.drawable.ic_layers_clear));
        mPromptProgress.setVisibility(View.VISIBLE);
        mPromptText.setText(R.string.empty_recipes);
        mPromptLayout.setVisibility(View.VISIBLE);
        Connection.build(getApplication()).getRecipes().enqueue(this);
    }

    @Override
    public void onResponse(@NonNull Call<List<Recipe>> call,
                           @NonNull Response<List<Recipe>> response) {
        Timber.d("onResponse: ");
        if (response.body() != null) {
            mAdapter.addData(response.body());
            mPromptProgress.setVisibility(View.GONE);
            mPromptImage.setVisibility(View.GONE);
            mPromptText.setVisibility(View.GONE);
            mPromptLayout.setVisibility(View.GONE);
            mRecyclerRecipes.setVisibility(View.VISIBLE);
        } else {
            Timber.w("onResponse: %s: %s", "Cant get data from remote", call);
            mPromptImage.setImageDrawable(
                    ResourceHelpers.getDrawable(this, R.drawable.ic_cloud_off));
            mPromptText.setText(R.string.error_load_recipes);
            mPromptProgress.setVisibility(View.GONE);
        }

        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(true);
        }
    }

    @Override
    public void onFailure(@NonNull Call<List<Recipe>> call, @NonNull Throwable t) {
        Timber.e(t, "Response Failure");
        mPromptImage.setImageDrawable(ResourceHelpers.getDrawable(this, R.drawable.ic_cloud_off));
        mPromptText.setText(R.string.error_load_recipes);
        mPromptProgress.setVisibility(View.GONE);

        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(true);
        }
    }


    /**
     * Only called from test, creates and returns a new
     * {@link BakingIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        Timber.d("getIdlingResource() called");
        if (mIdlingResource == null) {
            mIdlingResource = new BakingIdlingResource();
        }
        return mIdlingResource;
    }
}
