package com.jabama.challenge.network.di

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.jabama.challenge.repository.token.TokenRepository
import com.jabama.challenge.repository.token.TokenRepositoryImpl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

const val OK_HTTP = "OK_HTTP"
const val RETROFIT = "RETROFIT"
const val READ_TIMEOUT = "READ_TIMEOUT"
const val WRITE_TIMEOUT = "WRITE_TIMEOUT"
const val CONNECTION_TIMEOUT = "CONNECTION_TIMEOUT"
val networkModule = module {

    single(named(READ_TIMEOUT)) { 30 * 1000 }
    single(named(WRITE_TIMEOUT)) { 10 * 1000 }
    single(named(CONNECTION_TIMEOUT)) { 10 * 1000 }

    factory<Interceptor> {
        HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.HEADERS)
            .setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    factory(named(OK_HTTP)) {
        OkHttpClient.Builder()
            .readTimeout(get(named(READ_TIMEOUT)), TimeUnit.MILLISECONDS)
            .writeTimeout(get(named(WRITE_TIMEOUT)), TimeUnit.MILLISECONDS)
            .connectTimeout(get(named(CONNECTION_TIMEOUT)), TimeUnit.MILLISECONDS)
            .addInterceptor(get())
            .build()
    }

    single(named(RETROFIT)) {
        Retrofit.Builder()
            .client(get(named(OK_HTTP)))
            .baseUrl("http://api.github.com")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    }

    single {
        TokenRepositoryImpl(get()) as TokenRepository
    }
}