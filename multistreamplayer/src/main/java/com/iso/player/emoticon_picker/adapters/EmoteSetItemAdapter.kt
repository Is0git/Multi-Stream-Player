package com.iso.player.emoticon_picker.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar
import com.iso.chat.chat.chat_emotes.EmotesManager
import com.iso.player.R

class EmoteSetItemAdapter(
    var onItemOnClickListener: OnEmoteLayoutListener? = null
) :
    RecyclerView.Adapter<EmoteSetItemAdapter.MyViewHolder>() {

    var emotesList: List<EmotesManager.Emote>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var emoteImage: ImageView = view.findViewById(R.id.emote_image)
        lateinit var contextText: String

        init {
            view.setOnLongClickListener {
                Snackbar.make(emoteImage, contextText, LENGTH_SHORT).show().let { true }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.emotes_list_item_layout, parent, false)
        return MyViewHolder(view).also {
            it.itemView.setOnClickListener { view ->
                onItemOnClickListener?.onEmoteItemClick(
                    view, it.adapterPosition
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return emotesList?.count() ?: 0
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = emotesList?.get(position)
        holder.contextText = item?.code ?: "Kappa"
        holder.emoteImage.setImageDrawable(item?.emoteDrawable)
    }
}

interface OnEmoteLayoutListener {
    fun onEmoteItemClick(view: View, itemPosition: Int)
}