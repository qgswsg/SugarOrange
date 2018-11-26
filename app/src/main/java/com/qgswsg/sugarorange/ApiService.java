package com.qgswsg.sugarorange;

import com.qgswsg.sugarorangeannotation.Api;
import com.qgswsg.sugarorangeannotation.MergeName;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

@Api(baseUrl = "www.baidu.com")
public interface ApiService {

    /**
     * 再次点注释嘛，不要客气啦
     *
     * @param a 说话又带啦
     * @return 台湾来的吧
     */
    @GET("api")
    Observable method(@Query("a") String a);

}
