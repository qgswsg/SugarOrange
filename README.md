# SugarOrange
这是一个通过注解方式管理HTTP API的开源库，只针对于Retrofit。
当Retrofit遇到需要使用多个baseUrl的项目时，我们不可避免的要为动态切换baseUrl做很多事，尤其是在每个baseUrl下的接口还很多的情况下。
这时候如果使用此库对这些HTTP API进行管理，也许会帮到你不少。
这个库的使用非常简单，只需要用到两个注解：@Api和@MergeName
@Api用来注解Retrofit的接口文件：
<pre>
@Api("http://www.github.com/")
public interface GitHubService {
    @GET("users/{user}/repos")
    Call<List<Repo>> listRepos(@Path("user") String user);

    @Headers({
            "Accept: application/vnd.github.v3.full+json",
            "User-Agent: Retrofit-Sample-App"
    })
    @GET("users/{username}")
    Call<User> getUser(@Path("username") String username);
}
</pre>
