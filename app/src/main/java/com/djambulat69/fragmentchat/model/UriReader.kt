package com.djambulat69.fragmentchat.model

import android.net.Uri

interface UriReader {
    fun readBytes(uri: Uri): ByteArray?
}
