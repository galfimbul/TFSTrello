package ru.shvetsov.myTrello.di.dagger

import dagger.Subcomponent
import ru.shvetsov.myTrello.di.addmembers.AddMembersModule
import ru.shvetsov.myTrello.di.addmembers.AddMembersViewModelsModule
import ru.shvetsov.myTrello.di.cardinfo.CardInfoModule
import ru.shvetsov.myTrello.di.cardinfo.CardInfoViewModelsModule
import ru.shvetsov.myTrello.fragments.CardInfoFragment

/**
 * Created by Alexander Shvetsov on 26.01.2020
 */
@Subcomponent(
    modules = [CardInfoModule::class,
        CardInfoViewModelsModule::class,
        AddMembersModule::class,
        AddMembersViewModelsModule::class]
)
interface CardInfoComponent {
    fun inject(cardInfoFragment: CardInfoFragment)
}