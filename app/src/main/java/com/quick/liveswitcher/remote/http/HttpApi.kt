package com.dongyou.common.http

import com.quick.liveswitcher.remote.http.converter.DyGsonConverterFactory
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


object HttpApi {
    const val TIME_OUT_FIVE = 5L
    const val TIMEOUT_READ: Long = 60L
    const val TIMEOUT_CONNECTION: Long = 60L
    private var BASE_URL: String? = null
    private var mRetrofit: Retrofit? = null
    var okHttpClient: OkHttpClient? = null

    fun initSSLSocketFactory(): SSLSocketFactory? {
        //创建加密上下文
        var sslContext: SSLContext? = null
        try {
            //这里要与服务器的算法类型保持一致TSL/SSL
            sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, arrayOf<TrustManager>(object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            }), SecureRandom())
            return sslContext.socketFactory
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * 生成TrustManager信任管理器类
     */
    val trustManager = object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return arrayOf()
        }
    }

    init {
        okHttpClient = OkHttpClient.Builder()
            .sslSocketFactory(initSSLSocketFactory()!!, trustManager)
            .hostnameVerifier { _, _ -> true }
            .retryOnConnectionFailure(true)
            .readTimeout(HttpApi.TIMEOUT_READ, TimeUnit.SECONDS)
            .connectTimeout(HttpApi.TIMEOUT_CONNECTION, TimeUnit.SECONDS)
            .cookieJar(object : CookieJar {
                private val cookieStore = HashMap<HttpUrl, List<Cookie>>()
                override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                    cookieStore[url] = cookies
                }

                override fun loadForRequest(url: HttpUrl): List<Cookie> {
                    val cookies = cookieStore[url]
                    return cookies ?: ArrayList()
                }
            }).build()
    }

    val retrofit: Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl("http://121.199.172.213:888/")
        .addConverterFactory(DyGsonConverterFactory.create())
        .build()

}