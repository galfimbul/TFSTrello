package ru.shvetsov.myTrello.di.singleboard

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import ru.shvetsov.myTrello.network.SingleBoardApi

/**
 * Created by Alexander Shvetsov on 18.11.2019
 */
@Module
class SingleBoardModule {
    @SingleBoardScope
    @Provides
    fun provideSingleBoardApi(retrofit: Retrofit): SingleBoardApi {
        return retrofit.create(SingleBoardApi::class.java)
    }

}
