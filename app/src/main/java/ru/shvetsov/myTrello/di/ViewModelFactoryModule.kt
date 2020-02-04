package ru.shvetsov.myTrello.di

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import ru.shvetsov.myTrello.viewmodels.ViewModelProviderFactory

/**
 * Created by Alexander Shvetsov on 17.11.2019
 */
@Module
abstract class ViewModelFactoryModule {
    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelProviderFactory): ViewModelProvider.Factory

}