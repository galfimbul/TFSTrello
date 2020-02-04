package ru.shvetsov.myTrello.di.cardchanges

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.shvetsov.myTrello.di.ViewModelKey
import ru.shvetsov.myTrello.viewmodels.CardChangesViewModel

/**
 * Created by Alexander Shvetsov on 18.11.2019
 */
@Module
abstract class CardChangesViewModelsModule {
    @Binds
    @IntoMap
    @ViewModelKey(CardChangesViewModel::class)
    abstract fun bindCardChangesViewModel(cardChangesViewModel: CardChangesViewModel): ViewModel

}