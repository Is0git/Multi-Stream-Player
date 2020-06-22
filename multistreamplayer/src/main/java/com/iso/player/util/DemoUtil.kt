package com.iso.player.util

import android.text.TextUtils
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.util.MimeTypes
import java.text.DecimalFormat
import java.util.*


internal object DemoUtil {
    /**
     * Builds a track name for display.
     *
     * @param format [Format] of the track.
     * @return a generated name specific to the track.
     */
    fun buildTrackName(format: Format): String {
        val trackName: String
        trackName = if (MimeTypes.isVideo(format.sampleMimeType)) {
            joinWithSeparator(
                joinWithSeparator(
                    joinWithSeparator(
                        buildResolutionString(
                            format
                        ),
                        buildBitrateString(
                            format
                        )
                    ),
                    buildTrackIdString(
                        format
                    )
                ),
                buildSampleMimeTypeString(
                    format
                )
            )
        } else if (MimeTypes.isAudio(format.sampleMimeType)) {
            joinWithSeparator(
                joinWithSeparator(
                    joinWithSeparator(
                        joinWithSeparator(
                            buildLanguageString(
                                format
                            ),
                            buildAudioPropertyString(
                                format
                            )
                        ),
                        buildBitrateString(
                            format
                        )
                    ),
                    buildTrackIdString(
                        format
                    )
                ),
                buildSampleMimeTypeString(
                    format
                )
            )
        } else {
            joinWithSeparator(
                joinWithSeparator(
                    joinWithSeparator(
                        buildLanguageString(
                            format
                        ),
                        buildBitrateString(
                            format
                        )
                    ),
                    buildTrackIdString(
                        format
                    )
                ),
                buildSampleMimeTypeString(
                    format
                )
            )
        }
        return if (trackName.isEmpty()) "unknown" else trackName
    }

    private fun buildResolutionString(format: Format): String {
        return if (format.width === Format.NO_VALUE || format.height === Format.NO_VALUE) "" else format.width.toString() + "x" + format.height
    }

    private fun buildAudioPropertyString(format: Format): String {
        return if (format.channelCount === Format.NO_VALUE || format.sampleRate === Format.NO_VALUE) "" else format.channelCount.toString() + "ch, " + format.sampleRate + "Hz"
    }

    private fun buildLanguageString(format: Format): String {
        return if (TextUtils.isEmpty(format.language) || "und" == format.language) "" else format.language!!
    }

    private fun buildBitrateString(format: Format): String {
        return if (format.bitrate === Format.NO_VALUE) "" else java.lang.String.format(
            Locale.US,
            "%.2fMbit",
            format.bitrate / 1000000f
        )
    }

    private fun joinWithSeparator(first: String, second: String): String {
        return if (first.length == 0) second else if (second.length == 0) first else "$first, $second"
    }

    private fun buildTrackIdString(format: Format): String {
        return if (format.id == null) "" else "id:" + format.id
    }

    private fun buildSampleMimeTypeString(format: Format): String {
        return if (format.sampleMimeType == null) "" else format.sampleMimeType!!
    }

    fun getFormattedDouble(value: Double, precision: Int): String {
        return DecimalFormat("#0." + if (precision <= 1) "0" else if (precision == 2) "00" else "000").format(
            value
        )
    }

    @JvmOverloads
    fun humanReadableByteCount(
        bytes: Long,
        si: Boolean,
        isBits: Boolean = false
    ): String {
        val unit = if (!si) 1000 else 1024
        if (bytes < unit) return "$bytes KB"
        val exp =
            (Math.log(bytes.toDouble()) / Math.log(unit.toDouble())).toInt()
        val pre = (if (si) "kMGTPE" else "KMGTPE")[exp - 1]
            .toString() + if (si) "" else "i"
        return if (isBits) String.format(
            "%.1f %sb",
            bytes / Math.pow(unit.toDouble(), exp.toDouble()),
            pre
        ) else String.format(
            "%.1f %sB",
            bytes / Math.pow(unit.toDouble(), exp.toDouble()),
            pre
        )
    }
}