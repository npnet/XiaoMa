package com.qiming.fawcard.synthesize;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.qiming.fawcard.synthesize.data.source.remote.IDrivedApi;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class IDrivedApiService {

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://www.carbuyin.cn:19092/")
            .client(getOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();

    public static IDrivedApi createIDrivedApi() {
        return retrofit.create(IDrivedApi.class);
    }

    private static OkHttpClient getOkHttpClient(){
        return new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor())
                .build();
    }
}
