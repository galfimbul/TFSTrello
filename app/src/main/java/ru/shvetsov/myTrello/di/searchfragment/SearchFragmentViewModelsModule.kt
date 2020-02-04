package ru.shvetsov.myTrello.di.searchfragment

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.shvetsov.myTrello.di.ViewModelKey
import ru.shvetsov.myTrello.viewmodels.SearchFragmentViewModel

/**
 * Created by Alexander Shvetsov on 18.11.2019
 */
@Module
abstract class SearchFragmentViewModelsModule {
    @Binds
    @IntoMap
    @ViewModelKey(SearchFragmentViewModel::class)
    abstract fun bindSearchFragmentViewModel(searchFragmentViewModel: SearchFragmentViewModel): ViewModel

}