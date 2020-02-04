package ru.shvetsov.myTrello.di.cardchanges

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import ru.shvetsov.myTrello.adapters.CardChangesAdapter
import ru.shvetsov.myTrello.network.CardChangesApi

/**
 * Created by Alexander Shvetsov on 18.11.2019
 */
@Module
class CardChangesModule {

    @Provides
    fun provideCardChangesApi(retrofit: Retrofit): CardChangesApi {
        return retrofit.create(CardChangesApi::class.java)
    }

    @Provides
    fun provideListOfBoardsAdapter(): CardChangesAdapter {
        return CardChangesAdapter()
    }

}
