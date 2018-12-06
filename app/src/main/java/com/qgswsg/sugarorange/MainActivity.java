package com.qgswsg.sugarorange;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.qgswsg.sugarorangeannotation.MergeName;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    @MergeName("MyApiService")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Retrofit.Builder()
                .baseUrl("www.baidu.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(MyApiService.class);
    }
}
