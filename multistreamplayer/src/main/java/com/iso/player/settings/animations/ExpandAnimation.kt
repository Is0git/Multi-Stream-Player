package com.iso.player.settings.animations

import android.transition.Transition
import android.transition.TransitionInflater
import android.transition.TransitionManager
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout

class ExpandAnimation(var view: View, animationController: AnimationController) : AnimationController  by animationController{

    constructor(view: View, transition: Transition, animationController: AnimationController) : this(view, animationController) {
        this.transition = transition
    }
    constructor(view: View, transitionId: Int, animationController: AnimationController) : this(view, animationController) {
        this.transition = TransitionInflater.from(view.context).inflateTransition(transitionId)
    }

    lateinit var transition: Transition
    var isExpanded: Boolean = false

    fun playAnimation(rootView: ConstraintLayout) {
        TransitionManager.beginDelayedTransition(rootView, transition)
                if (isExpanded) hide(view, isExpanded).also { isExpanded = false } else expand(view, isExpanded).also { isExpanded = true }
    }
}