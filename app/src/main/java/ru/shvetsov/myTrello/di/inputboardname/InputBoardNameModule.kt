package ru.shvetsov.myTrello.di.inputboardname

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import ru.shvetsov.myTrello.di.listofboard.ListOfBoardScope
import ru.shvetsov.myTrello.network.InputBoardNameApi

/**
 * Created by Alexander Shvetsov on 18.11.2019
 */
@Module
class InputBoardNameModule {
    @ListOfBoardScope
    @Provides
    fun provideInputBoardNameApi(retrofit: Retrofit): InputBoardNameApi {
        return retrofit.create(InputBoardNameApi::class.java)
    }


}
