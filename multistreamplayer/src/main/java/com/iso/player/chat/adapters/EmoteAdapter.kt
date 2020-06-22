package com.iso.player.chat.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.iso.chat.twitch_chat.chat_emotes.TwitchEmotesManager
import com.iso.player.databinding.EmoteItemLayoutBinding

class EmoteAdapter : RecyclerView.Adapter<EmoteAdapter.MyViewHolder>() {

    var twitchEmotesList: List<TwitchEmotesManager.TwitchEmote>? = null
        set(value) {
            field= value
            notifyDataSetChanged()
        }

    class MyViewHolder(val emoteItemLayoutBinding: EmoteItemLayoutBinding) : RecyclerView.ViewHolder(emoteItemLayoutBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = EmoteItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(
            binding
        )
    }

    override fun getItemCount(): Int {
        return twitchEmotesList?.count() ?: 0
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(holder.emoteItemLayoutBinding.image).load(twitchEmotesList!![position].imageUrl).into(holder.emoteItemLayoutBinding.image)
    }
}
