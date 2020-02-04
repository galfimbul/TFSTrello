package ru.shvetsov.myTrello.di.cardinfo

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import ru.shvetsov.myTrello.network.CardInfoApi

/**
 * Created by Alexander Shvetsov on 18.11.2019
 */
@Module
class CardInfoModule {

    @Provides
    fun provideCardChangesApi(retrofit: Retrofit): CardInfoApi {
        return retrofit.create(CardInfoApi::class.java)
    }


}
