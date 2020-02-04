package ru.shvetsov.myTrello.di.dagger

import dagger.Subcomponent
import ru.shvetsov.myTrello.di.addmembers.AddMembersModule
import ru.shvetsov.myTrello.di.addmembers.AddMembersViewModelsModule
import ru.shvetsov.myTrello.fragments.AddMembersDialogFragment

/**
 * Created by Alexander Shvetsov on 26.01.2020
 */
@Subcomponent(modules = [AddMembersModule::class, AddMembersViewModelsModule::class])
interface AddMemberComponent {
    fun inject(addMembersDialogFragment: AddMembersDialogFragment)
}