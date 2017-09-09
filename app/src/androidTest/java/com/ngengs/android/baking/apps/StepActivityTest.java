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

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
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
    private List<Step> stepData = DataHelper.buildSteps(STEP_COUNT);
    @Rule
    public ActivityTestRule<StepActivity> stepActivityActivityTestRule
            = new ActivityTestRule<StepActivity>(
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
    public void step_DataDisplayCorrectly() {
        if (!DeviceHelper.isTablet(mStepActivity)) {
            if (mStepActivity.getResources().getConfiguration().orientation ==
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                testLayout(START_POSITION);

            }
        }
    }

    @Test
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
