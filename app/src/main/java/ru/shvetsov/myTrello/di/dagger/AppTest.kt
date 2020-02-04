package ru.shvetsov.myTrello.di.dagger

import android.app.Application

/**
 * Created by Alexander Shvetsov on 26.01.2020
 */
class AppTest : Application() {
    lateinit var appComponent: AppComponentDagger
    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponentDagger.builder()
            .appModuleDagger(AppModuleDagger(this))
            .build()
    }
}