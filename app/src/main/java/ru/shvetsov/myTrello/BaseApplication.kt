package ru.shvetsov.myTrello

import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import ru.shvetsov.myTrello.di.daggerandroid.DaggerAppComponent

/**
 * Created by Alexander Shvetsov on 16.11.2019
 */
class BaseApplication : DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().application(this).build()
    }
}