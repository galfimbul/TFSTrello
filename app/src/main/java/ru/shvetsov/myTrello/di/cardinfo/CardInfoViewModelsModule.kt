package ru.shvetsov.myTrello.di.cardinfo

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.shvetsov.myTrello.di.ViewModelKey
import ru.shvetsov.myTrello.viewmodels.CardInfoViewModel

/**
 * Created by Alexander Shvetsov on 18.11.2019
 */
@Module
abstract class CardInfoViewModelsModule {
    @Binds
    @IntoMap
    @ViewModelKey(CardInfoViewModel::class)
    abstract fun bindCardInfoViewModel(cardInfoViewModel: CardInfoViewModel): ViewModel

}