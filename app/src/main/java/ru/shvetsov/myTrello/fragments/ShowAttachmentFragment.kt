package ru.shvetsov.myTrello.fragments

import android.Manifest
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.DownloadListener
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.show_attachment_fragment.*
import ru.shvetsov.myTrello.BuildConfig
import ru.shvetsov.myTrello.R
import java.io.File

/**
 * Created by Alexander Shvetsov on 20.12.2019
 */
private const val PERMISSION_REQUEST_CODE = 1234

class ShowAttachmentFragment : Fragment() {
    lateinit var url: String
    private var isFile = false
    private lateinit var downloadListener: DownloadListener
    var writeAccess = false
    lateinit var downloadFileName: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        url = arguments?.getString("url").orEmpty()
        isFile = arguments?.getBoolean("isFile")!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.show_attachment_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        wv_show_photo.webViewClient = WebViewClient()
        wv_show_photo.webChromeClient = WebChromeClient()
        wv_show_photo.settings.loadWithOverviewMode = true
        wv_show_photo.settings.useWideViewPort = true
        wv_show_photo.isVerticalScrollBarEnabled = true
        wv_show_photo.settings.apply {
            allowFileAccess = true
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true
        }
        when {
            url.isNotEmpty() && !isFile -> {
                wv_show_photo.visibility = View.VISIBLE
                if (show_attachment_download_label.isVisible)
                    show_attachment_download_label.visibility = View.GONE

                wv_show_photo.loadUrl(url)
            }
            url.isNotEmpty() && isFile -> {
                downloadAndOpenFile(url)
            }
            else -> {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.show_image_fragment_url_error), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun openFileAfterDownload(fileName: String) {
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
        val fileIntent = Intent(Intent.ACTION_VIEW)
        val uri = FileProvider.getUriForFile(requireContext(), "${BuildConfig.APPLICATION_ID}.provider", file)
        Log.d("M_ShowAttachmentFra", fileName)

        fileIntent.data = (uri)
        fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val chooserIntent = Intent.createChooser(fileIntent, getString(R.string.show_attachment_fragment_chooser_title))
        startActivity(chooserIntent)
    }

    private fun downloadAndOpenFile(url: String) {
        if (wv_show_photo.isVisible) {
            wv_show_photo.visibility = View.GONE
        }
        checkWriteAccess()
        createDownloadListener()
        show_attachment_download_label.visibility = View.VISIBLE
        show_attachment_download_label.text =
            getString(R.string.show_attachment_fragment_file_download_label, downloadFileName)
        wv_show_photo.setDownloadListener(downloadListener)
        wv_show_photo.loadUrl(url)
        onDownloadComplete()
    }

    private fun checkWriteAccess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requireActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setMessage(getString(R.string.show_attachment_fragment_permission_dialog_message))
                    builder.setTitle(getString(R.string.show_attachment_fragment_permission_dialog_title))
                    builder.setPositiveButton(getString(R.string.show_attachment_fragment_permission_dialog_positive_btn)) { _, _ ->
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            PERMISSION_REQUEST_CODE
                        )
                    }
                    builder.setNeutralButton(
                        getString(R.string.show_attachment_fragment_permission_dialog_negative_btn),
                        null
                    )
                    val dialog = builder.create()
                    dialog.show()
                } else {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        PERMISSION_REQUEST_CODE
                    )
                }
            } else {
                writeAccess = true
            }
        }
    }

    private fun createDownloadListener() {
        downloadListener = DownloadListener { url, _, contentDescription, mimetype, _ ->

            val request = DownloadManager.Request(Uri.parse(url))

            request.allowScanningByMediaScanner()

            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

            downloadFileName = URLUtil.guessFileName(url, contentDescription, mimetype)


            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, downloadFileName)

            val dManager = requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

            if (writeAccess)
                dManager.enqueue(request)
            else {
                Toast.makeText(
                    context,
                    getString(R.string.show_attachment_fragment_write_permission_failed),
                    Toast.LENGTH_LONG
                ).show()
                checkWriteAccess()
            }
        }
    }

    private fun onDownloadComplete() {
        val onComplete = object : BroadcastReceiver() {
            override fun onReceive(ctxt: Context, intent: Intent) {
                Toast.makeText(
                    ctxt,
                    getString(R.string.show_attachment_fragment_download_complete_toast),
                    Toast.LENGTH_LONG
                ).show()
                openFileAfterDownload(downloadFileName)
            }
        }
        requireActivity().registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    writeAccess = true
                } else {
                    // Permission denied
                    writeAccess = false
                    Toast.makeText(
                        context,
                        getString(R.string.show_attachment_fragment_permission_error),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    companion object {
        fun newInstance(url: String, isFile: Boolean = false): Fragment {
            val showImageFragment = ShowAttachmentFragment()
            val args = Bundle()
            args.putString("url", url)
            args.putBoolean("isFile", isFile)
            showImageFragment.arguments = args
            return showImageFragment
        }
    }
}