package ru.shvetsov.myTrello.di.listofboard

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import ru.shvetsov.myTrello.adapters.ListOfBoardsAdapter
import ru.shvetsov.myTrello.network.ListOfBoardsApi

/**
 * Created by Alexander Shvetsov on 18.11.2019
 */
@Module
class ListOfBoardsModule {

    @ListOfBoardScope
    @Provides
    fun provideListOfBoardsApi(retrofit: Retrofit): ListOfBoardsApi {
        return retrofit.create(ListOfBoardsApi::class.java)
    }

    @ListOfBoardScope
    @Provides
    fun provideListOfBoardsAdapter(): ListOfBoardsAdapter {
        return ListOfBoardsAdapter()
    }

}
