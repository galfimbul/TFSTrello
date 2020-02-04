package ru.shvetsov.myTrello.network

import com.github.scribejava.apis.TrelloApi
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.oauth.OAuth10aService
import com.github.scribejava.httpclient.okhttp.OkHttpHttpClientConfig
import ru.shvetsov.myTrello.dataClasses.TrelloConstants

/**
 * Created by Alexander Shvetsov on 12.11.2019
 */
object ServiceClient {
    private const val CALLBACK_URL = "https://aeshvetsov.com"
    val instance: OAuth10aService by lazy {
        return@lazy ServiceBuilder(TrelloConstants.CONSUMER_KEY)
            .apiSecret(TrelloConstants.CONSUMER_SECRET)
            .callback(CALLBACK_URL)
            .httpClientConfig(OkHttpHttpClientConfig.defaultConfig())
            .build(TrelloApi.instance())
    }

}