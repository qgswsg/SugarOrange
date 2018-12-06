package com.qgswsg.sugarorange;

import com.qgswsg.sugarorangeannotation.Api;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

@Api("http://www.google.com/")
public interface GoogleService {

    @GET("group/{id}/users")
    Call<List<User>> groupList(@Path("id") int groupId, @QueryMap Map<String, String> options);
}
