package ru.shvetsov.myTrello.di.dagger

import android.app.Application
import android.content.Context
import dagger.Component
import ru.shvetsov.myTrello.di.ViewModelFactoryModule
import javax.inject.Singleton

/**
 * Created by Alexander Shvetsov on 26.01.2020
 */
@Singleton
@Component(modules = [AppModuleDagger::class, ViewModelFactoryModule::class])
interface AppComponentDagger {
    fun context(): Context
    fun applicationContext(): Application
    fun getSubcomponent(): AppSubcomponent
    fun getListOfBoardsSubcomponent(): ListOfBoardsComponent
    fun getInputBoardNameSubcomponent(): InputBoardNameComponent
    fun getSingleBoardSubcomponent(): SingleBoardComponent
    fun getCardChangesSubcomponent(): CardChangesComponent
    fun getCardInfoSubcomponent(): CardInfoComponent
    fun getAddMemberSubcomponent(): AddMemberComponent
}