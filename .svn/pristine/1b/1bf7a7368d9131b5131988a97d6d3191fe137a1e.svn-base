package com.ddy.httplib;


import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import okio.BufferedSource;
import okio.Okio;
import retrofit2.Converter;

/**
 * 针对性的json转换
 * @param <T>
 */
public class LoadResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private final Gson gson;
//    private final TypeAdapter<T> adapter;
    private Type type;
    LoadResponseBodyConverter(Gson gson, Type type) {
        this.gson = gson;
        this.type = type;
//        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        BufferedSource bufferedSource = Okio.buffer(value.source());
        String tempStr = bufferedSource.readUtf8();
        bufferedSource.close();
//        JsonReader jsonReader = gson.newJsonReader(value.charStream());
        try {
//            return adapter.read(jsonReader);
//            BaseDataResult dataResult = (BaseDataResult) JsonUtil.parsData(tempStr, BaseDataResult.class);
//            dataResult.setData();
            return (T) JsonUtil.parsData(tempStr,type);
        } finally {
            value.close();
        }
    }

}
