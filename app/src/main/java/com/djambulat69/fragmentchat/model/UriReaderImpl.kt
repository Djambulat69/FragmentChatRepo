package com.djambulat69.fragmentchat.model

import android.net.Uri
import com.djambulat69.fragmentchat.ui.FragmentChatApplication

class UriReaderImpl : UriReader {
    override fun readBytes(uri: Uri): ByteArray? {
        return FragmentChatApplication.contentResolver().openInputStream(uri)?.readBytes()
    }
}
