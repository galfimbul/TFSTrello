package ru.shvetsov.myTrello.di.builders

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.shvetsov.myTrello.MainActivity

/**
 * Created by Alexander Shvetsov on 16.11.2019
 */
@Module
abstract class ActivityBuildersModule {
    @ContributesAndroidInjector(
        modules = [MainFragmetBuildersModule::class]
    )
    abstract fun contributeMainActivity(): MainActivity
}
