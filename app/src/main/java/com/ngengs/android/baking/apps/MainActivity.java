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

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
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
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
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
            gridSpan = (getResources().getConfiguration().orientation ==
                        Configuration.ORIENTATION_LANDSCAPE) ? 3 : 2;
        } else {
            gridSpan = (getResources().getConfiguration().orientation ==
                        Configuration.ORIENTATION_LANDSCAPE) ? 2 : 1;
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
            if (data.size() > 0) mAdapter.addData(data);
            else loadDataFromRemote();
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
            mPromptImage.setImageDrawable(
                    ResourceHelpers.getDrawable(this, R.drawable.ic_cloud_off));
            mPromptText.setText(R.string.error_load_recipes);
            mPromptProgress.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFailure(@NonNull Call<List<Recipe>> call, @NonNull Throwable t) {
        Timber.e(t, "Response Failure");
        mPromptImage.setImageDrawable(ResourceHelpers.getDrawable(this, R.drawable.ic_cloud_off));
        mPromptText.setText(R.string.error_load_recipes);
        mPromptProgress.setVisibility(View.GONE);
    }
}
