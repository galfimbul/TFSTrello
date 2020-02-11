package ru.shvetsov.myTrello.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.github.scribejava.core.model.OAuth1RequestToken
import com.github.scribejava.core.oauth.OAuth10aService
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.auth_fragment.*
import ru.shvetsov.myTrello.R
import ru.shvetsov.myTrello.dataClasses.TrelloConstants
import ru.shvetsov.myTrello.di.dagger.AppTest
import ru.shvetsov.myTrello.extensions.showError
import ru.shvetsov.myTrello.interfaces.FragmentListener
import ru.shvetsov.myTrello.network.ServiceClient
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by Alexander Shvetsov on 31.10.2019
 */
class AuthFragment : Fragment() {
    lateinit var accessTokenListener: FragmentListener
    lateinit var requestToken: OAuth1RequestToken
    private val service: OAuth10aService by lazy {
        return@lazy ServiceClient.instance
    }
    private val disposablesBag = CompositeDisposable()

    @Inject
    lateinit var spref: SharedPreferences

    @Inject
    @Named("token")
    lateinit var tokentest: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val component = (requireActivity().application as AppTest).appComponent.getSubcomponent()
        component.inject(this)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d("M_AuthFragment", tokentest)
        return inflater.inflate(R.layout.auth_fragment, container, false)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_authorize_test.setOnClickListener {
            if (spref.getString(getString(R.string.access_token_key), "")!!.isNotEmpty()) {
                accessTokenListener.getToken()
            } else {
                getRequestToken()
                wv_test.settings.javaScriptEnabled = true
                wv_test.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                        if (url!!.startsWith(TrelloConstants.REST_CALLBACK_URL)) {
                            val uri = Uri.parse(url)
                            val oauthVerifier = uri.getQueryParameter("oauth_verifier")
                            getAccessToken(oauthVerifier!!, spref)
                        } else {
                            view!!.loadUrl(url)
                        }
                        return true
                    }
                }
            }
        }
    }

    private fun getAccessToken(oauthVerifier: String, spref: SharedPreferences) {
        val getAccessToken =
            Single.fromCallable { service.getAccessToken(requestToken, oauthVerifier) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val token = it.token
                    view!!.visibility = View.GONE
                    tv_response.visibility = View.VISIBLE
                    val editor = spref.edit()
                    editor.putString(getString(R.string.access_token_key), token)
                    editor.apply()
                    accessTokenListener.getToken()

                }, {
                    Log.d("M_AuthFragment", it.localizedMessage)
                })
        disposablesBag.add(getAccessToken)

    }

    private fun getRequestToken() {
        val getRequestToken = Single.fromCallable { service.requestToken }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                btn_authorize_test.visibility = View.GONE
                wv_test.visibility = View.VISIBLE
                val authUrl = service.getAuthorizationUrl(it)
                val url = getString(R.string.auth_fragment_web_view_loading_url, authUrl)
                wv_test.loadUrl(url)
                requestToken = it
            }, {

                showError(R.string.auth_fragment_get_request_token_failed)
            })
        disposablesBag.add(getRequestToken)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposablesBag.clear()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentListener) {
            accessTokenListener = context
        } else {
            throw RuntimeException("$context must implement OnViewCreatedListener")
        }
    }

    companion object {
        fun newInstance(): AuthFragment {
            return AuthFragment()
        }
    }
}