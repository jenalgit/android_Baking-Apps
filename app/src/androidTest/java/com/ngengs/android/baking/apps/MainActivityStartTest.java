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

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class MainActivityStartTest {


    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<>(
            MainActivity.class);

    public void testRecyclerRecipe_Hidden() {
        onView(ViewMatchers.withId(R.id.recyclerRecipes)).check(matches(not(isDisplayed())));
    }

    public void testPromptLayout_Displayed() {
        onView(ViewMatchers.withId(R.id.prompt_layout)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.prompt_image)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.prompt_text)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.prompt_progress)).check(matches(isDisplayed()));
    }
}
