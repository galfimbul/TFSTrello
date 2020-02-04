package ru.shvetsov.myTrello.di.dagger

import dagger.Subcomponent
import ru.shvetsov.myTrello.di.singleboard.SingleBoardModule
import ru.shvetsov.myTrello.di.singleboard.SingleBoardScope
import ru.shvetsov.myTrello.di.singleboard.SingleBoardViewModelsModule
import ru.shvetsov.myTrello.fragments.SingleBoardFragment

/**
 * Created by Alexander Shvetsov on 26.01.2020
 */
@SingleBoardScope
@Subcomponent(modules = [SingleBoardModule::class, SingleBoardViewModelsModule::class])
interface SingleBoardComponent {
    fun inject(singleBoardFragment: SingleBoardFragment)
}