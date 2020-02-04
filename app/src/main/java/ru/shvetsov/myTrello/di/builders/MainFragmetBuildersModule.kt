package ru.shvetsov.myTrello.di.builders

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.shvetsov.myTrello.di.searchfragment.SearchFragmentModule
import ru.shvetsov.myTrello.di.searchfragment.SearchFragmentViewModelsModule
import ru.shvetsov.myTrello.fragments.SearchFragment

@Module
abstract class MainFragmetBuildersModule {

    /*@ContributesAndroidInjector // Attaches fragment to Dagger graph.
    abstract fun contributeAuthFragment(): AuthFragment

    @ListOfBoardScope
    @ContributesAndroidInjector(
        modules = [
            ListOfBoardsModule::class,
            ListOfBoardsViewModelsModule::class,
            InputBoardNameViewModelsModule::class,
            InputBoardNameModule::class
        ]
    )
    abstract fun contributeListOfBoardsFragment(): ListOfBoardsFragment

    @ListOfBoardScope
    @ContributesAndroidInjector(modules = [InputBoardNameModule::class, InputBoardNameViewModelsModule::class])
    abstract fun contributeInputBoardNameFragment(): InputBoardNameFragment

    @SingleBoardScope
    @ContributesAndroidInjector(modules = [SingleBoardModule::class, SingleBoardViewModelsModule::class])
    abstract fun contributeSingleBoardFragment(): SingleBoardFragment

    @ContributesAndroidInjector(modules = [CardChangesModule::class, CardChangesViewModelsModule::class])
    abstract fun contributeCardChangesFragment(): CardChangesFragment

    @ContributesAndroidInjector(
        modules = [CardInfoModule::class,
            CardInfoViewModelsModule::class,
            AddMembersModule::class, AddMembersViewModelsModule::class]
    )
    abstract fun contributeCardInfoFragment(): CardInfoFragment

    @ContributesAndroidInjector(modules = [AddMembersModule::class, AddMembersViewModelsModule::class])
    abstract fun contributeAddMembersFragment(): AddMembersDialogFragment*/

    @ContributesAndroidInjector(modules = [SearchFragmentModule::class, SearchFragmentViewModelsModule::class])
    abstract fun contributeSearchFragmentFragment(): SearchFragment

}