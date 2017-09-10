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

package com.ngengs.android.baking.apps.remotes;

import android.app.Application;
import android.support.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

@SuppressWarnings("WeakerAccess")
public class Connection {
    public static final String FALLBACK_IMAGE_NUTELLA_PIE
            = "http://static.ngengs.com/images/udacity/image_nutella_pie.webp";
    public static final String FALLBACK_IMAGE_BROWNIES
            = "http://static.ngengs.com/images/udacity/image_brownies.webp";
    public static final String FALLBACK_IMAGE_CHEESE_CAKE
            = "http://static.ngengs.com/images/udacity/image_cheese_cake.webp";
    public static final String FALLBACK_IMAGE_YELLOW_CAKE
            = "http://static.ngengs.com/images/udacity/image_yellow_cake.webp";
    private static final String BASE_URL = "http://go.udacity.com/";
    private static final long CACHE_SIZE = 10 * 1024 * 1024;    // 10 MB
    private static final int CONNECT_TIMEOUT = 30;
    private static final int WRITE_TIMEOUT = 300;
    private static final int TIMEOUT = 300;

    /**
     * Build okHttp client.
     *
     * @param application
     *         The application of activity
     *
     * @return okHttp client
     */
    public static OkHttpClient provideOkHttp(Application application) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(
                new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(@NonNull String message) {
                        Timber.tag("OkHttp").d(message);
                    }
                });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        Cache cache = new Cache(application.getCacheDir(), CACHE_SIZE);

        return new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .followRedirects(true)
                .followSslRedirects(true)
                .cache(cache)
                .build();
    }

    /**
     * Build retrofit client.
     *
     * @param okHttpClient
     *         okHttp configured client.
     *
     * @return retrofit client
     */
    public static Retrofit provideRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(Connection.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }


    /**
     * Build BakingAPI client.
     *
     * @param application
     *         The application of activity
     *
     * @return BakingAPI client from retrofit.
     */
    public static BakingAPI build(Application application) {
        return provideRetrofit(provideOkHttp(application)).create(BakingAPI.class);
    }
}
