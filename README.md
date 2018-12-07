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
而@MergeName可以在任意地方使用，目的只为指定被@Api注解的所有接口文件合并后的class文件名：
<pre>@MergeName("MyApiService")</pre>
![效果图](https://github.com/qgswsg/SugarOrange/blob/master/%E6%95%88%E6%9E%9C%E5%9B%BE.jpg)
