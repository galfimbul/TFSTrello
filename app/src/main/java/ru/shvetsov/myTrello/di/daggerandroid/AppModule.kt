package ru.shvetsov.myTrello.di.daggerandroid

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.shvetsov.myTrello.dataClasses.TrelloConstants
import javax.inject.Singleton

/**
 * Created by Alexander Shvetsov on 16.11.2019
 */
@Module
class AppModule {
    @Provides
    fun providesSharedPreferences(application: Application): SharedPreferences {
        return application.getSharedPreferences("MyApp", Context.MODE_PRIVATE)
    }

    @Provides
    fun providesToken(application: Application): String {
        return application.getSharedPreferences("MyApp", Context.MODE_PRIVATE)
            .getString("access_token", "").orEmpty()
    }


    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofitInstance(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(TrelloConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .client(okHttpClient)
            .build()
    }
}