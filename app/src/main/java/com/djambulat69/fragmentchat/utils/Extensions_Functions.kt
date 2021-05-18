package com.djambulat69.fragmentchat.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.Px
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.widget.ViewPager2
import com.djambulat69.fragmentchat.di.NetworkModule
import org.jsoup.Jsoup
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt


// Extensions

@Px
fun Context.spToPx(sp: Int): Int {
    return (sp * resources.displayMetrics.scaledDensity).roundToInt()
}

@Px
fun Context.dpToPx(dp: Int): Int {
    return (dp * resources.displayMetrics.density).roundToInt()
}

fun Int.toEmoji() = String(Character.toChars(this))

@Suppress("unchecked_cast")
fun <T : View> View.inflate(
    @LayoutRes
    layout: Int,
    root: ViewGroup? = this as? ViewGroup,
    attachToRoot: Boolean = false
): T = LayoutInflater.from(context).inflate(layout, root, attachToRoot) as T

fun ViewPager2.getCurrentFragments(fragmentManager: FragmentManager): List<Fragment?> {
    val itemCount = adapter?.itemCount ?: 0
    val fragments: MutableList<Fragment?> = MutableList(itemCount) { null }
    for (i in 0 until itemCount) {
        fragments[i] = fragmentManager.findFragmentByTag("f$i")
    }
    return fragments
}

fun ClipboardManager.copyText(text: String) {
    val clip = ClipData.newPlainText(null, text)
    setPrimaryClip(clip)
}

fun Fragment.setChildFragmentResultListener(requestKey: String, listener: (String, Bundle) -> Unit) {
    childFragmentManager.setFragmentResultListener(requestKey, this, listener)
}

// Functions

fun secondsToDateString(timeStamp: Long): String {
    return LocalDateTime
        .ofEpochSecond(
            timeStamp,
            1000,
            ZoneId.systemDefault().rules.getOffset(LocalDateTime.now())
        )
        .format(
            DateTimeFormatter.ofPattern("d MMM")
        )
}

fun parseHtml(content: String): CharSequence {
    val doc = Jsoup.parse(content, NetworkModule.BASE_URL).body()

    doc.select("a")?.forEach { e ->
        e.attr("href", e.absUrl("href"))
    }

    // to prevent HtmlCompat replacing imgS with image spans by default
    doc.getElementsByTag("img")?.remove()

    return HtmlCompat.fromHtml(doc.html(), HtmlCompat.FROM_HTML_MODE_COMPACT).trim()
}
