 package com.android.hwyun.common.net;

import android.support.annotation.NonNull;
import android.util.Log;

import com.android.hwyun.common.net.inf.IAnalysisJson;
import com.ddy.httplib.RxSchedulersHelper;

import org.reactivestreams.Publisher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;


 /**
 * 基于OKhttp的帮助类
 * Created by linbaosheng on 2017/7/6.
 */

public abstract class BaseOkHttpHelper {

    public Disposable requestRegister() {
        LoadApiServiceHelp.loadApiService("").requestRegister(new HashMap<String, String>());
        return null;
    }

    /**
     */
    public Disposable sendGetRequest(String url, final IAnalysisJson analysisJson, int timeOut) {
        Disposable disposable = LoadApiServiceHelp.loadApiService("")
                .executeGet(url)
                .compose(RxSchedulersHelper.<ResponseBody>io_main())
                .compose(new FlowableTransformer<String, Object>() {

                    @Override
                    public Publisher<Object> apply(Flowable<String> upstream) {
                        return upstream.map(new Function<String, Object>() {
                            @Override
                            public Object apply(String s) throws Exception {
                                return analysisJson.getData(s);
                            }
                        });
                    }
                }).subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<Object>() {
                            @Override
                            public void accept(Object o) throws Exception {
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                            }
                        }
                );
        return disposable;
    }


    /**
     * post网络访问
     */
    public Disposable sendPostRequest(String baseurl, String url, Map<String, String> map, final IAnalysisJson
            analysisJson, int timeOut) {
        Map<String, String> headers = new HashMap<String, String>();
        Disposable disposable = LoadApiServiceHelp.loadApiService(baseurl)
                .executePost(url, map, headers)
                .compose(RxSchedulersHelper.<ResponseBody>io_main())
                .compose(new FlowableTransformer<String, Object>() {

                    @Override
                    public Publisher<Object> apply(Flowable<String> upstream) {
                        return upstream.map(new Function<String, Object>() {
                            @Override
                            public Object apply(String s) throws Exception {
                                return analysisJson.getData(s);
                            }
                        });
                    }
                }).subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<Object>() {
                            @Override
                            public void accept(Object o) throws Exception {
                                onResponse(o);
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                onErrorResponse(new Exception(throwable.getMessage()));
//                                Log.i("BaseOkhttphelper", "throl:" + throwable.getMessage());
                            }
                        }
                );
        return disposable;
    }


    public Disposable uploadResourcePost(String url, String path, Map<String, String> map, final
            IAnalysisJson analysisJson, int timeOut) {
        File file = new File(path);
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            builder.addFormDataPart(entry.getKey(),entry.getValue());
        }
        builder.addFormDataPart("img", file.getName(), RequestBody.create(MediaType.parse
                ("image/*"), file));
        RequestBody requestBody = builder.build();
        Disposable disposable = LoadApiServiceHelp.loadApiService("http://app.ddyun.com/")
                .uploadResourcePost(url, requestBody)
                .compose(RxSchedulersHelper.<ResponseBody>io_main())
                .compose(new FlowableTransformer<String, Object>() {

                    @Override
                    public Publisher<Object> apply(Flowable<String> upstream) {
                        return upstream.map(new Function<String, Object>() {
                            @Override
                            public Object apply(String s) throws Exception {
                                return analysisJson.getData(s);
                            }
                        });
                    }
                }).subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Consumer<Object>() {
                            @Override
                            public void accept(Object o) throws Exception {
                                onResponse(o);
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
//                                onErrorResponse(throwable);
                                Log.i("BaseOkhttphelper", "throl:" + throwable.getMessage());
                            }
                        }
                );
        return disposable;
    }

    public abstract void onResponse(Object response);

    public abstract void onErrorResponse(Exception error);
     //进度返回
     public interface ProgressCallBack {
         /**
          * 响应进度更新
          */
         void onProgress(Disposable disposable, long total, long current);

         void onFailure(Exception e);

         void onResponse();
     }

     /**
      * 创建带进度的RequestBody
      *
      * @param contentType MediaType
      * @param file        准备上传的文件
      * @param callBack    回调
      * @return
      */
     public RequestBody createProgressRequestBody(final Disposable disposable, final MediaType contentType, final File file, final ProgressCallBack callBack) {
         return new RequestBody() {
             @Override
             public MediaType contentType() {
                 return contentType;
             }

             @Override
             public long contentLength() {
                 return file.length();
             }

             @Override
             public void writeTo(BufferedSink sink) {
                 Source source;
                 try {
                     source = Okio.source(file);
                     Buffer buf = new Buffer();
                     long remaining = contentLength();
                     long current = 0;
                     for (long readCount; (readCount = source.read(buf, 2048)) != -1; ) {
                         sink.write(buf, readCount);
                         current += readCount;
                         callBack.onProgress(disposable, remaining, current);
                     }
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
             }
         };
     }

     /**
      * 上传文件
      *
      * @param url
      * @param file
      */
     public Disposable uploadFile(String url, File file, final ProgressCallBack callBack) {
         //创建RequestBody
         Disposable disposable = null;
         RequestBody requestBody = createProgressRequestBody(disposable, null, file, callBack);
         disposable = LoadApiServiceHelp.loadApiService(getAddress(url))
                 .uploadFile(url, requestBody)
                 .compose(RxSchedulersHelper.<ResponseBody>io_main())
                 .subscribeOn(Schedulers.io())
                 .unsubscribeOn(Schedulers.io())
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribe(
                         new Consumer<Object>() {
                             @Override
                             public void accept(Object o) throws Exception {
                                 callBack.onResponse();
                             }
                         },
                         new Consumer<Throwable>() {
                             @Override
                             public void accept(Throwable throwable) throws Exception {
                                 callBack.onFailure(new Exception(throwable.getMessage()));
                             }
                         }
                 );
         return disposable;

     }

     //获取首地址
     private String getAddress(String url) {
         URL str = null;
         try {
             str = new URL(url);
         } catch (MalformedURLException e) {
             e.printStackTrace();
         }
         String host = str.getHost();
         return "http://" + host + "/";
     }

     /**
      * 下载文件
      *
      * @param url
      * @param path
      * @param callBack
      */
     public void downloadFile(String url, String path, final ProgressCallBack callBack) {
         final File file = new File(path);
         Observable<ResponseBody> down = LoadApiServiceHelp.loadApiService(getAddress(url)).downloadFile(url);
         down.subscribeOn(Schedulers.io())//subscribeOn和ObserOn必须在io线程，如果在主线程会出错
                 .observeOn(Schedulers.io())
                 .observeOn(Schedulers.computation())//需要
                 .map(new Function<ResponseBody, File>() {
                     @Override
                     public File apply(@NonNull ResponseBody responseBody) throws Exception {
                         InputStream inputStream = responseBody.byteStream();
                         long max = responseBody.contentLength();
                         try {
                             FileOutputStream fileOutputStream = new FileOutputStream(file);
                             byte[] bytes = new byte[2048];
                             int rendLength = 0;
                             long currLength = 0;
                             while ((rendLength = inputStream.read(bytes)) != -1) {
                                 fileOutputStream.write(bytes, 0, rendLength);
                                 currLength += rendLength;
                                 callBack.onProgress(null, max, currLength);
                             }
                             fileOutputStream.flush();
                             inputStream.close();
                             fileOutputStream.close();
                             callBack.onResponse();
                         } catch (Exception e) {
                             e.printStackTrace();
                             callBack.onFailure(new Exception(e.toString()));
                         }
                         return file;
                     }
                 })
                 .observeOn(AndroidSchedulers.mainThread())
                 //因为要写文件,所以Observer不切换到主线程
                 .subscribe(new Observer<File>() {
                     @Override
                     public void onSubscribe(Disposable d) {
                     }

                     @Override
                     public void onNext(File responseBody) {

                     }

                     @Override
                     public void onError(Throwable e) {
                         callBack.onFailure(new Exception(e.toString()));
                     }

                     @Override
                     public void onComplete() {
                     }
                 });

     }

}
