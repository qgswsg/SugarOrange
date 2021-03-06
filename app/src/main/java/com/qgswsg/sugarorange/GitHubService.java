package com.qgswsg.sugarorange;

import com.qgswsg.sugarorangeannotation.Api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Headers;
import retrofit2.http.OPTIONS;
import retrofit2.http.Path;

@Api("http://www.github.com/")
public interface GitHubService {
    @GET(MainActivity.API_1)
    Call<List<Repo>> listRepos(@Path("user") String user);

    @Headers({
            "Accept: application/vnd.github.v3.full+json",
            "User-Agent: Retrofit-Sample-App"
    })
    @HTTP(method = "GET",path = "users/{user}/repos")
    Call<User> getUser(@Path("username") String username);
}
