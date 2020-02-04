package ru.shvetsov.myTrello.di.addmembers

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.shvetsov.myTrello.di.ViewModelKey
import ru.shvetsov.myTrello.viewmodels.AddCardMembersViewModel

/**
 * Created by Alexander Shvetsov on 18.11.2019
 */
@Module
abstract class AddMembersViewModelsModule {
    @Binds
    @IntoMap
    @ViewModelKey(AddCardMembersViewModel::class)
    abstract fun bindAddCardMembersViewModel(addCardMembersViewModel: AddCardMembersViewModel): ViewModel

}