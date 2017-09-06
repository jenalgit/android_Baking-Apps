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

package com.ngengs.android.baking.apps.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.AppCompatDrawableManager;

@SuppressWarnings("unused")
public class ResourceHelpers {
    public static int getColor(Context context, int colorId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getResources().getColor(colorId, null);
        } else {
            //noinspection deprecation
            return context.getResources().getColor(colorId);
        }
    }

    public static Drawable getDrawable(Context context, int drawableId) {
        Drawable placeholder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            placeholder = context.getResources().getDrawable(drawableId, null);
        } else {
            placeholder = AppCompatDrawableManager.get().getDrawable(context, drawableId);
        }
        return placeholder;
    }
}
