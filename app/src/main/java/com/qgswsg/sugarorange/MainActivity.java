package com.qgswsg.sugarorange;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.qgswsg.sugarorangeannotation.MergeName;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public static final String API_1 = "users/{user}/repos";

    private @MergeName("MyApiService")
    Class<?> c;
    {
        try {
            c = Class.forName("MyApiService");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Retrofit.Builder()
                .baseUrl("http://square.github.io/retrofit/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(MyApiService.class);
    }
}
