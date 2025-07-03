package com.example.artsyapp.util

import java.time.Duration
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

fun getRelativeTime(isoTime: String?): String {
    if (isoTime == null) return ""
    return try {
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val pastTime = OffsetDateTime.parse(isoTime, formatter)
        val duration = Duration.between(pastTime, now)

        when {
            duration.seconds < 60 -> "${duration.seconds} seconds ago"
            duration.toMinutes() < 60 -> "${duration.toMinutes()} minutes ago"
            duration.toHours() < 24 -> "${duration.toHours()} hours ago"
            else -> "${duration.toDays()} days ago"
        }
    } catch (e: Exception) {
        ""
    }
}
