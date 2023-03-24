package com.android.hwyun.common.net;

import com.ddy.httplib.BaseHttpResult;

import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 *
 * Created by linbaosheng on 2016/12/8.
 */

public interface BaseApiService {

    @GET
    Flowable<ResponseBody> executeGet(@Url String url);

    @FormUrlEncoded
    @POST
    Flowable<ResponseBody> executePost(
            @Url String url, @FieldMap Map<String, String> maps, @HeaderMap Map<String, String> headers);

//    @Multipart
//    @FormUrlEncoded
    @POST
    Flowable<ResponseBody> uploadResourcePost(@Url String url, @Body RequestBody Body);

    /**
     * 注册账号
     */
    @FormUrlEncoded
    @POST("User/Register")
    Flowable<BaseHttpResult<String>> requestRegister(@FieldMap Map<String, String> data);

    @PUT
    Flowable<ResponseBody> uploadFile(@Url String url,@Body RequestBody Body);

    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Url String url);
}
