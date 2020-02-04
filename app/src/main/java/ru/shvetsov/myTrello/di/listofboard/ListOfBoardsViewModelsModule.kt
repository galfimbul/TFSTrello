package ru.shvetsov.myTrello.di.listofboard

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.shvetsov.myTrello.di.ViewModelKey
import ru.shvetsov.myTrello.viewmodels.ListOfBoardsViewModel

/**
 * Created by Alexander Shvetsov on 18.11.2019
 */
@Module
abstract class ListOfBoardsViewModelsModule {
    @Binds
    @IntoMap
    @ViewModelKey(ListOfBoardsViewModel::class)
    abstract fun bindListOfBoardsViewModel(listOfBoardsViewModel: ListOfBoardsViewModel): ViewModel

}