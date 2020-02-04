package ru.shvetsov.myTrello.di.singleboard

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.shvetsov.myTrello.di.ViewModelKey
import ru.shvetsov.myTrello.viewmodels.SingleBoardViewModel

/**
 * Created by Alexander Shvetsov on 18.11.2019
 */
@Module
abstract class SingleBoardViewModelsModule {
    @Binds
    @IntoMap
    @ViewModelKey(SingleBoardViewModel::class)
    abstract fun bindSingleBoardViewModel(singleBoardViewModel: SingleBoardViewModel): ViewModel

}
