package ru.shvetsov.myTrello.di.addmembers

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import ru.shvetsov.myTrello.network.AddMembersApi

/**
 * Created by Alexander Shvetsov on 18.11.2019
 */
@Module
class AddMembersModule {

    @Provides
    fun provideAddMembersApi(retrofit: Retrofit): AddMembersApi {
        return retrofit.create(AddMembersApi::class.java)
    }
}
