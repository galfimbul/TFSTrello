package ru.shvetsov.myTrello.di.dagger

import dagger.Subcomponent
import ru.shvetsov.myTrello.di.cardchanges.CardChangesModule
import ru.shvetsov.myTrello.di.cardchanges.CardChangesViewModelsModule
import ru.shvetsov.myTrello.fragments.CardChangesFragment

/**
 * Created by Alexander Shvetsov on 26.01.2020
 */
@Subcomponent(modules = [CardChangesModule::class, CardChangesViewModelsModule::class])
interface CardChangesComponent {
    fun inject(cardChangesFragment: CardChangesFragment)
}