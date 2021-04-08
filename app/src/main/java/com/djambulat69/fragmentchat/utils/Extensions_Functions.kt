package com.djambulat69.fragmentchat.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.Px
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.widget.ViewPager2
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
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

// Functions

private const val DATE_FORMAT_PATTERN = "d MMM"

fun secondsToDateString(timeStamp: Long): String {
    return LocalDateTime
        .ofEpochSecond(
            timeStamp,
            1000,
            ZoneOffset.ofTotalSeconds(0)
        )
        .format(
            DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN, Locale.getDefault())
        )
}
