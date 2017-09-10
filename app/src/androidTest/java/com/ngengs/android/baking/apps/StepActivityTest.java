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
import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;

import com.ngengs.android.baking.apps.data.Step;
import com.ngengs.android.baking.apps.utils.DataHelper;
import com.ngengs.android.baking.apps.utils.DeviceHelper;
import com.ngengs.android.baking.apps.utils.MatchHelper;
import com.schibsted.spain.barista.BaristaAssertions;
import com.schibsted.spain.barista.BaristaClickActions;
import com.schibsted.spain.barista.BaristaScrollActions;
import com.schibsted.spain.barista.BaristaSleepActions;
import com.schibsted.spain.barista.flakyespresso.AllowFlaky;
import com.schibsted.spain.barista.flakyespresso.FlakyActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class StepActivityTest {
    private static final int STEP_COUNT = 3;
    private static final int START_POSITION = 1;
    private final List<Step> stepData = DataHelper.buildSteps(STEP_COUNT);
    @Rule
    public FlakyActivityTestRule<StepActivity> stepActivityActivityTestRule
            = new FlakyActivityTestRule<StepActivity>(
            StepActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Context targetContext = InstrumentationRegistry.getInstrumentation()
                                                           .getTargetContext();
            Intent result = new Intent(targetContext, StepActivity.class);
            result.putParcelableArrayListExtra("DATA", new ArrayList<>(stepData));
            result.putExtra("POSITION", START_POSITION);
            return result;
        }
    };
    private StepActivity mStepActivity;

    @Before
    public void configurationActivity() {
        mStepActivity = stepActivityActivityTestRule.getActivity();
    }

    @Test
    @AllowFlaky(attempts = 10)
    public void layout_Available() {
        if (!DeviceHelper.isTablet(mStepActivity)) {
            BaristaAssertions.assertDisplayed(R.id.fragment_step);
            if (mStepActivity.getResources().getConfiguration().orientation ==
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                BaristaAssertions.assertDisplayed(R.id.tools_step);
                BaristaAssertions.assertDisplayed(R.id.button_step_next);
                BaristaAssertions.assertDisplayed(R.id.button_step_prev);
                BaristaAssertions.assertDisplayed(R.id.step_indicator);
            }
        }
    }

    @Test
    @AllowFlaky(attempts = 10)
    public void step_DataDisplayCorrectly() {
        if (!DeviceHelper.isTablet(mStepActivity)) {
            if (mStepActivity.getResources().getConfiguration().orientation ==
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                testLayout(START_POSITION);

            }
        }
    }

    @Test
    @AllowFlaky(attempts = 10)
    public void buttonPrev_Clicked() {
        if (!DeviceHelper.isTablet(mStepActivity)) {
            if (mStepActivity.getResources().getConfiguration().orientation ==
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                BaristaClickActions.click(R.id.button_step_prev);
                BaristaSleepActions.sleep(5, TimeUnit.SECONDS);
                testLayout(START_POSITION - 1);
            }
        }
    }

    @Test
    @AllowFlaky(attempts = 10)
    public void buttonNext_Clicked() {
        if (!DeviceHelper.isTablet(mStepActivity)) {
            if (mStepActivity.getResources().getConfiguration().orientation ==
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                BaristaClickActions.click(R.id.button_step_next);
                BaristaSleepActions.sleep(5, TimeUnit.SECONDS);
                testLayout(START_POSITION + 1);
            }
        }
    }

    @Test
    @AllowFlaky(attempts = 10)
    public void rotate_KeepData() {
        if (!DeviceHelper.isTablet(mStepActivity)) {
            if (mStepActivity.getResources().getConfiguration().orientation ==
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                mStepActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                BaristaSleepActions.sleep(5, TimeUnit.SECONDS);
                testLayout(START_POSITION);
            }
        }
    }

    @Test
    @AllowFlaky(attempts = 10)
    public void buttonNext_ClickedAndRotate() {
        if (!DeviceHelper.isTablet(mStepActivity)) {
            if (mStepActivity.getResources().getConfiguration().orientation ==
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                mStepActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                BaristaClickActions.click(R.id.button_step_next);
                BaristaSleepActions.sleep(5, TimeUnit.SECONDS);
                testLayout(START_POSITION + 1);
            }
        }
    }

    @Test
    @AllowFlaky(attempts = 10)
    public void buttonPrev_ClickedAndRotate() {
        if (!DeviceHelper.isTablet(mStepActivity)) {
            if (mStepActivity.getResources().getConfiguration().orientation ==
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                mStepActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                BaristaClickActions.click(R.id.button_step_prev);
                BaristaSleepActions.sleep(5, TimeUnit.SECONDS);
                testLayout(START_POSITION - 1);
            }
        }
    }

    private void testLayout(int position) {
        Step mStep = stepData.get(position);
        if (mStepActivity.getResources().getConfiguration().orientation !=
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && !TextUtils.isEmpty(mStep.getVideoURL())) {
            BaristaAssertions.assertNotDisplayed(R.id.tools_step);
            BaristaAssertions.assertDisplayed(R.id.step_description);
        } else {
            MatchHelper.matchToolbarTitle(mStep.getShortDescription());
            BaristaAssertions.assertDisplayed(
                    position + "/" + (stepData.size() - 1));
            if (position == STEP_COUNT) BaristaAssertions.assertNotDisplayed(R.id.button_step_next);
            if (position == 0) BaristaAssertions.assertNotDisplayed(R.id.button_step_prev);
            if (!TextUtils.isEmpty(mStep.getDescription())) {
                BaristaScrollActions.scrollTo(R.id.step_description);
                BaristaAssertions.assertDisplayed(R.id.step_description);
                BaristaAssertions.assertDisplayed(mStep.getDescription());
            } else {
                BaristaAssertions.assertNotDisplayed(R.id.step_description);
            }
            if (!TextUtils.isEmpty(mStep.getThumbnailURL())) {
                BaristaScrollActions.scrollTo(R.id.step_image_thumbnail);
                BaristaAssertions.assertDisplayed(R.id.step_image_thumbnail);
            } else {
                BaristaAssertions.assertNotDisplayed(R.id.step_image_thumbnail);
            }
        }
    }

}
