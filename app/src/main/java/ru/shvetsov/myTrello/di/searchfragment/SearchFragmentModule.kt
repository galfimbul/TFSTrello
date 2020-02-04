package ru.shvetsov.myTrello.di.searchfragment

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import ru.shvetsov.myTrello.adapters.SearchFragmentAdapter
import ru.shvetsov.myTrello.network.SearchFragmentApi

/**
 * Created by Alexander Shvetsov on 18.11.2019
 */
@Module
class SearchFragmentModule {

    @Provides
    fun provideSearchFragmentApi(retrofit: Retrofit): SearchFragmentApi {
        return retrofit.create(SearchFragmentApi::class.java)
    }

    @Provides
    fun provideListOfBoardsAdapter(): SearchFragmentAdapter {
        return SearchFragmentAdapter()
    }

}
