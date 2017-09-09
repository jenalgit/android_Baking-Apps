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

import android.content.pm.ActivityInfo;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.ngengs.android.baking.apps.utils.RecyclerViewItemCountAssertion;
import com.schibsted.spain.barista.BaristaAssertions;
import com.schibsted.spain.barista.BaristaSleepActions;
import com.schibsted.spain.barista.flakyespresso.AllowFlaky;
import com.schibsted.spain.barista.flakyespresso.FlakyActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.greaterThan;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityDataLoadedTest {

    @Rule
    public FlakyActivityTestRule<MainActivity> mainActivityActivityTestRule
            = new FlakyActivityTestRule<>(MainActivity.class);

    private IdlingResource mIdlingResource;

    @Before
    public void registerIdlingResource() {
        mIdlingResource = mainActivityActivityTestRule.getActivity().getIdlingResource();
        Log.d("MainActivityRecipeList",
              "registerIdlingResource: " + mIdlingResource.getName() + ":" +
              mIdlingResource.isIdleNow());
        IdlingRegistry.getInstance().register(mIdlingResource);
        BaristaSleepActions.sleep(5, TimeUnit.SECONDS);
    }

    @Test
    public void testRecyclerRecipe_Click() {
        Log.d("MainActivityRecipeList", "testRecyclerRecipe_Click: ");
        testRecyclerData();
        // Perform click in recycler view item
        onView(withId(R.id.recyclerRecipes)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click()));
        // Check layout in recipe activity
        BaristaAssertions.assertDisplayed(R.id.fragment_recipe);

    }

    @Test
    @AllowFlaky(attempts = 10)
    public void layoutAfterLoaded_DisplayOrHidden() {
        Log.d("MainActivityRecipeList", "testRecyclerOther_Click: ");
        testLayout();
    }

    @Test
    @AllowFlaky(attempts = 10)
    public void rotate_DataStillAvailable() {
        Log.d("MainActivityRecipeList", "testRecyclerRecipe_Click: ");
        // Rotate the screen
        mainActivityActivityTestRule.getActivity()
                                    .setRequestedOrientation(
                                            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        BaristaSleepActions.sleep(5, TimeUnit.SECONDS);

        // Run test
        testLayout();
        testRecyclerData();

    }


    @After
    public void unregisterIdlingResource() {
        Log.d("MainActivityRecipeList",
              "unregisterIdlingResource: " + mIdlingResource.getName() + ":" +
              mIdlingResource.isIdleNow());
        if (mIdlingResource != null) IdlingRegistry.getInstance().unregister(mIdlingResource);

    }

    private void testRecyclerData() {
        // Check data count in recycler view
        onView(withId(R.id.recyclerRecipes)).check(
                new RecyclerViewItemCountAssertion(greaterThan(0)));
    }

    private void testLayout() {
        // Check Recycle View is show
        BaristaAssertions.assertDisplayed(R.id.recyclerRecipes);
        // Check prompt is hide
        BaristaAssertions.assertNotDisplayed(R.id.prompt_layout);
        BaristaAssertions.assertNotDisplayed(R.id.prompt_image);
        BaristaAssertions.assertNotDisplayed(R.id.prompt_text);
        BaristaAssertions.assertNotDisplayed(R.id.prompt_progress);
    }
}
