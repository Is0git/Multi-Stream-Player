package com.iso.player.settings.animations

import android.view.View

interface AnimationController {
    fun expand(view: View?, isExpanded: Boolean)

    fun hide(view: View?, isExpanded: Boolean)
}