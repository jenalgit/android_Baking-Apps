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

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.ngengs.android.baking.apps.data.Ingredient;
import com.ngengs.android.baking.apps.data.Recipe;
import com.ngengs.android.baking.apps.data.Step;
import com.ngengs.android.baking.apps.utils.DataHelper;
import com.ngengs.android.baking.apps.utils.DeviceHelper;
import com.ngengs.android.baking.apps.utils.MatchHelper;
import com.schibsted.spain.barista.BaristaAssertions;
import com.schibsted.spain.barista.BaristaRecyclerViewActions;
import com.schibsted.spain.barista.BaristaScrollActions;
import com.schibsted.spain.barista.BaristaSleepActions;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.TimeUnit;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class RecipeActivityTest {

    private final String RECIPE_NAME = "Food Recipe";
    private final int INGREDIENT_COUNT = 25;
    private final int STEP_COUNT = 10;
    private final List<Step> stepData = DataHelper.buildSteps(STEP_COUNT);
    private final List<Ingredient> ingredientData = DataHelper.buildIngredient(INGREDIENT_COUNT);

    @Rule
    public ActivityTestRule<RecipeActivity> recipeActivityActivityTestRule
            = new ActivityTestRule<RecipeActivity>(
            RecipeActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Context targetContext = InstrumentationRegistry.getInstrumentation()
                                                           .getTargetContext();
            Intent result = new Intent(targetContext, RecipeActivity.class);
            Recipe recipe = new Recipe();
            recipe.setName(RECIPE_NAME);
            recipe.setId(0);
            recipe.setImage(null);
            recipe.setServings(5);
            recipe.setIngredients(ingredientData);
            recipe.setSteps(stepData);
            result.putExtra("DATA", recipe);
            return result;
        }
    };

    @Test
    public void layout_Available() {
        Log.d("RecipeActivityTest", "layout_Available: ");
        testLayout();

    }

    @Test
    public void title_DataMatch() {
        Log.d("RecipeActivityTest", "title_DataMatch: ");
        MatchHelper.matchToolbarTitle(RECIPE_NAME);

    }

    @Test
    public void ingredient_DataMatch() {
        Log.d("RecipeActivityTest", "ingredient_DataMatch: ");
        testDataIngredient();
        BaristaAssertions.assertDisplayed(ingredientData.get(0).getIngredient());
        BaristaAssertions.assertDisplayed(
                ingredientData.get(0).getQuantity() + " " + ingredientData.get(0).getMeasure());
    }

    @Test
    public void step_DataMatch() {
        Log.d("RecipeActivityTest", "step_DataMatch: ");
        BaristaScrollActions.scrollTo(R.id.recycleStep);
        testDataStep();
        BaristaAssertions.assertDisplayed(stepData.get(0).getShortDescription());
        BaristaAssertions.assertDisplayed("#0");
    }

    @Test
    public void rotate_DataStillAvailable() {
        recipeActivityActivityTestRule.getActivity()
                                      .setRequestedOrientation(
                                              ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        BaristaSleepActions.sleep(5, TimeUnit.SECONDS);

        testLayout();
        testDataIngredient();
        testDataStep();

    }

    @Test
    public void step_Clicked() {
        BaristaScrollActions.scrollTo(R.id.recycleStep);
        BaristaRecyclerViewActions.clickRecyclerViewItem(R.id.recycleStep, 0);
        if (DeviceHelper.isTablet(recipeActivityActivityTestRule.getActivity())) {
            Log.d("RecipeActivityTest", "step_Clicked: in tablet");
            BaristaAssertions.assertDisplayed(R.id.fragment_step);
            BaristaAssertions.assertNotExist(R.id.tools_step);

        } else {
            Log.d("RecipeActivityTest", "step_Clicked: in phone");
            MatchHelper.matchToolbarTitle(stepData.get(0).getShortDescription());
            BaristaAssertions.assertDisplayed(R.id.tools_step);
        }
    }

    private void testLayout() {
        BaristaAssertions.assertDisplayed(R.id.fragment_recipe);
        BaristaScrollActions.scrollTo(R.id.recyclerIngredient);
        BaristaAssertions.assertDisplayed(R.id.recyclerIngredient);
        BaristaScrollActions.scrollTo(R.id.recycleStep);
        BaristaAssertions.assertDisplayed(R.id.recycleStep);
        if (DeviceHelper.isTablet(recipeActivityActivityTestRule.getActivity())) {
            Log.d("RecipeActivityTest", "testLayout: in tablet");
            // No Need Test, All tablet needed view not displayed at start
        } else {
            Log.d("RecipeActivityTest", "testLayout: in phone");
            BaristaAssertions.assertNotExist(R.id.guideline);
            BaristaAssertions.assertNotExist(R.id.fragment_step);
        }
    }

    private void testDataIngredient() {
        BaristaScrollActions.scrollTo(R.id.recyclerIngredient);
        BaristaAssertions.assertRecyclerViewItemCount(R.id.recyclerIngredient, INGREDIENT_COUNT);
    }

    private void testDataStep() {
        BaristaScrollActions.scrollTo(R.id.recycleStep);
        BaristaAssertions.assertRecyclerViewItemCount(R.id.recycleStep, STEP_COUNT);
    }

}
