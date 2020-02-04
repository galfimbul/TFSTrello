package ru.shvetsov.myTrello.di.inputboardname

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.shvetsov.myTrello.di.ViewModelKey
import ru.shvetsov.myTrello.viewmodels.InputBoardNameViewModel

/**
 * Created by Alexander Shvetsov on 18.11.2019
 */
@Module
abstract class InputBoardNameViewModelsModule {
    @Binds
    @IntoMap
    @ViewModelKey(InputBoardNameViewModel::class)
    abstract fun bindInputBoardNameViewModel(inputBoardNameViewModel: InputBoardNameViewModel): ViewModel

}