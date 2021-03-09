package com.djambulat69.fragmentchat.utils

import android.content.Context
import androidx.annotation.Px
import kotlin.math.roundToInt

@Px
fun Context.spToPx(sp: Int): Int {
    return (sp * resources.displayMetrics.scaledDensity).roundToInt()
}

