package ru.shvetsov.myTrello.di.dagger

import dagger.Subcomponent
import ru.shvetsov.myTrello.di.inputboardname.InputBoardNameModule
import ru.shvetsov.myTrello.di.inputboardname.InputBoardNameViewModelsModule
import ru.shvetsov.myTrello.di.listofboard.ListOfBoardScope
import ru.shvetsov.myTrello.fragments.InputBoardNameFragment

/**
 * Created by Alexander Shvetsov on 26.01.2020
 */
@ListOfBoardScope
@Subcomponent(modules = [InputBoardNameModule::class, InputBoardNameViewModelsModule::class])
interface InputBoardNameComponent {
    fun inject(inputBoardNameFragment: InputBoardNameFragment)
}