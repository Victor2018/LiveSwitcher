package com.quick.liveswitcher.remote.http.bean;

import com.google.gson.annotations.SerializedName;


public class RawResponseData<T> extends HttpResultEntity {
    @SerializedName("data")
    public T data;
}
