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

package com.ngengs.android.baking.apps.utils;

import com.ngengs.android.baking.apps.data.Ingredient;
import com.ngengs.android.baking.apps.data.Step;

import java.util.ArrayList;
import java.util.List;

public class DataHelper {
    public static List<Step> buildSteps(int count) {
        List<Step> stepList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Step step = new Step();
            step.setId(i);
            step.setDescription("Description " + i);
            step.setShortDescription("Short Description " + i);
            step.setThumbnailURL(null);
            step.setThumbnailURL((i % 2 == 0)
                                 ? "https://d17h27t6h515a5.cloudfront.net/topher/2017" +
                                   "/April/58ffd974_-intro-creampie/-intro-creampie.mp4"
                                 : null);
            stepList.add(step);
        }
        return stepList;
    }

    public static List<Ingredient> buildIngredient(int count) {
        List<Ingredient> ingredientList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Ingredient ingredient = new Ingredient();
            ingredient.setMeasure("Oz");
            ingredient.setQuantity(i);
            ingredient.setIngredient("Ingredient " + i);
            ingredientList.add(ingredient);
        }
        return ingredientList;
    }
}
