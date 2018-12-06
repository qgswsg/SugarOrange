package com.qgswsg.sugarorange;

import com.qgswsg.sugarorangeannotation.Api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

@Api
public interface OtherService {

    @Headers("Cache-Control: max-age=640000")
    @GET("widget/list")
    Call<List<Widget>> widgetList();

}
