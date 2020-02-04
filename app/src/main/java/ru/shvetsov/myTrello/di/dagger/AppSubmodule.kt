package ru.shvetsov.myTrello.di.dagger

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Named

/**
 * Created by Alexander Shvetsov on 26.01.2020
 */
@Module
class AppSubmodule {

    @Provides
    @Named("token")
    fun providesToken(application: Application): String {
        return application.getSharedPreferences("MyApp", Context.MODE_PRIVATE)
            .getString("access_token", "").orEmpty()
    }
}