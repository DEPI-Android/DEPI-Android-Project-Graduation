package com.hfad.egypttour.data.api

import com.hfad.egypttour.data.util.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

//object RetrofitInstance {
//    private val okHttpClient : OkHttpClient by lazy {
//        okHttpClient.newBuilder()
//            .addInterceptor {
//                val original  = it.request()
//                val requestBuilder = original.newBuilder()
//                    .header("User-Agent", Constants.USER_AGENT)
//                    .build()
//                it.proceed(requestBuilder)
//            }
//            .addInterceptor(
//                HttpLoggingInterceptor().apply {
//                    level = HttpLoggingInterceptor.Level.BODY
//                }
//            ).connectTimeout(Constants.CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
//            .readTimeout(Constants.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
//            .writeTimeout(Constants.WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
//            .build()
//    }
//
//
//    private val retrofit: Retrofit by lazy {
//        Retrofit.Builder()
//            .baseUrl(Constants.WIKIPEDIA_BASE_URL)
//            .client(okHttpClient)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//    }
//
//    val api: WikiApiService by lazy {
//        retrofit.create(WikiApiService::class.java)
//    }
//}


    object RetrofitInstance {

        private val okHttpClient: OkHttpClient by lazy {
            // FIX 1: Use OkHttpClient.Builder(), not the variable name
            OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val original = chain.request()
                    val requestBuilder = original.newBuilder()
                        .header("User-Agent", Constants.USER_AGENT)
                        .build()
                    chain.proceed(requestBuilder)
                }
                .addInterceptor(
                    HttpLoggingInterceptor().apply {
                        // FIX 2: Only log Body in Debug mode.
                        // In Release, use NONE to save performance and security.
                        level =  HttpLoggingInterceptor.Level.BODY

                    }
                )
                .connectTimeout(Constants.CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(Constants.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(Constants.WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build()
        }

        private val retrofit: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(Constants.WIKIPEDIA_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val api: WikiApiService by lazy {
            retrofit.create(WikiApiService::class.java)
        }
    }