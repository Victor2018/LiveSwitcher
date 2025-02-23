package com.quick.liveswitcher.remote.http.converter;


import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.quick.liveswitcher.remote.http.bean.HttpResultEntity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Converter;


public class DyGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private static final String TAG = "CustomGsonResponseBodyConverter";
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    DyGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {

        String response = value.string();
        try {
            HttpResultEntity httpStatus = gson.fromJson(response, HttpResultEntity.class);
            //验证status返回是否为1
            if (httpStatus.getCode().equals("200")) {
                //继续处理body数据反序列化，注意value.string() 不可重复使用
                MediaType contentType = value.contentType();
                Charset charset = (contentType == null) ? contentType.charset(UTF_8) : UTF_8;
                ByteArrayInputStream inputStream = new ByteArrayInputStream(response.getBytes());
                InputStreamReader reader = new InputStreamReader(inputStream, charset);
                JsonReader jsonReader = gson.newJsonReader(reader);

                try {
                    return adapter.read(jsonReader);
                } finally {
                    value.close();
                }
            } else {
                value.close();
                HttpResultEntity error = new HttpResultEntity();
                error.setCode(httpStatus.getCode());
                error.setMessage(httpStatus.getMessage());
                error.setLinkUrl(httpStatus.getLinkUrl());
                return (T) error;
            }

        }catch (Exception e){
            e.printStackTrace();
            HttpResultEntity error = new HttpResultEntity();
            error.setMessage(e.getMessage());
            return (T) error;
        }
    }
}
