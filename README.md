# SugarOrange
这是一个通过注解方式管理HTTP API的开源库，只针对于Retrofit。

当Retrofit遇到需要使用多个baseUrl的项目时，我们不可避免的要为动态切换baseUrl做很多事，尤其是在每个baseUrl下的接口还很多的情况下。

这时候如果使用此库对这些HTTP API进行管理，也许会帮到你不少。

这个库的使用非常简单，只需要用到两个注解：@Api和@MergeName

在接口上使用@Api：

```java
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
```
而@MergeName可以在任意地方使用，目的是指定那些使用@Api的接口在合并后生成新class文件的文件名：
<pre>@MergeName("MyApiService")</pre>
![效果图](https://github.com/qgswsg/SugarOrange/blob/master/%E6%95%88%E6%9E%9C%E5%9B%BE.jpg)
如上图所示，多个接口文件被注解上@Api后，在执行完Rebuild Project后就自动合并成了@MergeName注解所指定的新文件。并且将对应的baseUrl添加到了对应的API接口上。

这是利用了Retrofit在写请求接口时，如果给定的是完整的URL路径，将忽略构建实例时所指定的baseUrl。

就这样简单轻松的解决了多个baseUrl带来的不便。

当然，如果项目中没有使用到多个baseUrl也可以使用本开源库对上百个API接口进行分类管理，使项目结构更加清晰。

依赖：
```
    implementation 'com.qgswsg:SugarOrange-annotation:v1.0'
    annotationProcessor  'com.qgswsg:SugarOrange-compiler:v1.5'
```
