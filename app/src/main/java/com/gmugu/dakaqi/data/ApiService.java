package com.gmugu.dakaqi.data;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mugu on 16/11/24.
 */

public class ApiService {

    private static IApiService apiService;
    private static final String baseUrl = "http://192.168.43.131:8080/run/";
    private static int timeout = 5;

    public static IApiService getApiService() {
        if (apiService == null) {
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(timeout, TimeUnit.SECONDS).build();
            Retrofit retrofit = new Retrofit.Builder().client(client).baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build();
            apiService = retrofit.create(IApiService.class);
        }
        return apiService;
    }
}
