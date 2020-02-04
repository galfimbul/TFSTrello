package ru.shvetsov.myTrello.di.daggerandroid

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import ru.shvetsov.myTrello.BaseApplication
import ru.shvetsov.myTrello.di.ViewModelFactoryModule
import ru.shvetsov.myTrello.di.builders.ActivityBuildersModule
import javax.inject.Singleton

/**
 * Created by Alexander Shvetsov on 16.11.2019
 */
@Singleton
@Component(
    modules = [AndroidSupportInjectionModule::class,
        ActivityBuildersModule::class,
        AppModule::class,
        ViewModelFactoryModule::class]
)
interface AppComponent : AndroidInjector<BaseApplication> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}

