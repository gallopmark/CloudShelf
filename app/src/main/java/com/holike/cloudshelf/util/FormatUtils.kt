package com.holike.cloudshelf.util

import java.util.*

class FormatUtils {
    companion object {
        fun generateTime(time: Long): String {
            val totalSeconds = (time / 1000).toInt()
            val seconds = totalSeconds % 60
            val minutes = totalSeconds / 60 % 60
            val hours = totalSeconds / 3600
            return if (hours > 0) String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
            else String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }
    }
}