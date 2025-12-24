package com.securesoft.pay.internal;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// এখানে 'public' শব্দটি যোগ করা হয়েছে
public final class ApiClient {

    // মেথডটি এখন আর static নয়, কারণ আমরা SecureSoftPay ক্লাস থেকে এর অবজেক্ট তৈরি করব।
    // তবে সহজ করার জন্য static রাখাই ভালো।
    public static ApiService create(String baseUrl) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        String sanitizedBaseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(sanitizedBaseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(ApiService.class);
    }
}