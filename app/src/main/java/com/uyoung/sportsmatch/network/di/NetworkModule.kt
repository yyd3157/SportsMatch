package com.uyoung.sportsmatch.network.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.uyoung.sportsmatch.BuildConfig
import com.uyoung.sportsmatch.network.ApiCallAdapterFactory
import com.uyoung.sportsmatch.network.ApiClient
import com.uyoung.sportsmatch.network.KakaoMapApiClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class KakaoRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FireBaseRetrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        return OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()
    }

    @FireBaseRetrofit
    @Singleton
    @Provides
    fun provideFireBaseRetrofit(client: OkHttpClient): Retrofit {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.FIRE_BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(ApiCallAdapterFactory.create())
            .build()
    }

    @KakaoRetrofit
    @Singleton
    @Provides
    fun provideKakaoRetrofit(client: OkHttpClient): Retrofit {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.KAKAO_BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(ApiCallAdapterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideClient(@FireBaseRetrofit retrofit: Retrofit): ApiClient {
        return retrofit.create(ApiClient::class.java)
    }

    @Singleton
    @Provides
    fun provideKakaoClient(@KakaoRetrofit retrofit: Retrofit): KakaoMapApiClient {
        return retrofit.create(KakaoMapApiClient::class.java)
    }
}
