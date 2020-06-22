package com.iso.player.settings.groups.selection_group

interface SelectionListener {
    fun onItemSelect(group: SelectionGroup, position: Int)
}