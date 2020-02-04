package ru.shvetsov.myTrello.di.dagger

import dagger.Subcomponent
import ru.shvetsov.myTrello.di.inputboardname.InputBoardNameModule
import ru.shvetsov.myTrello.di.inputboardname.InputBoardNameViewModelsModule
import ru.shvetsov.myTrello.di.listofboard.ListOfBoardScope
import ru.shvetsov.myTrello.di.listofboard.ListOfBoardsModule
import ru.shvetsov.myTrello.di.listofboard.ListOfBoardsViewModelsModule
import ru.shvetsov.myTrello.fragments.ListOfBoardsFragment

/**
 * Created by Alexander Shvetsov on 26.01.2020
 */
@ListOfBoardScope
@Subcomponent(
    modules = [ListOfBoardsModule::class,
        ListOfBoardsViewModelsModule::class,
        InputBoardNameViewModelsModule::class,
        InputBoardNameModule::class]
)
interface ListOfBoardsComponent {
    fun inject(listOfBoardsFragment: ListOfBoardsFragment)

}