package ru.shvetsov.myTrello.di.dagger

import dagger.Subcomponent
import ru.shvetsov.myTrello.fragments.AuthFragment

/**
 * Created by Alexander Shvetsov on 26.01.2020
 */
@Subcomponent(modules = [AppSubmodule::class])
interface AppSubcomponent {

    fun inject(authFragment: AuthFragment)
}