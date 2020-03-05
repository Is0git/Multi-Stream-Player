package com.android.multistreamplayer.settings

import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray

interface ResourceListener{
    fun onResourceTracksReady(player: TrackSelectionArray)

    fun onTrackChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?)
}