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

import android.support.test.runner.AndroidJUnit4;

import com.schibsted.spain.barista.BaristaAssertions;
import com.schibsted.spain.barista.flakyespresso.AllowFlaky;
import com.schibsted.spain.barista.flakyespresso.FlakyActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityStartTest {


    @Rule
    public FlakyActivityTestRule<MainActivity> mainActivityActivityTestRule
            = new FlakyActivityTestRule<>(
            MainActivity.class);


    @Test
    @AllowFlaky(attempts = 10)
    public void testRecyclerRecipe_Hidden() {
        BaristaAssertions.assertNotDisplayed(R.id.recyclerRecipes);
    }

    @Test
    @AllowFlaky(attempts = 10)
    public void testPromptLayout_Displayed() {
        BaristaAssertions.assertDisplayed(R.id.prompt_layout);
        BaristaAssertions.assertDisplayed(R.id.prompt_image);
        BaristaAssertions.assertDisplayed(R.id.prompt_text);
        BaristaAssertions.assertDisplayed(R.id.prompt_progress);
    }
}
