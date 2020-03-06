package com.android.multistreamplayer.settings.groups.selection_group

interface SelectionListener {
    fun onItemSelect(group: SelectionGroup, position: Int)
}