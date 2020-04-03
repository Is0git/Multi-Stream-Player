package com.android.multistreamplayer.chat.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.multistreamchat.twitch_chat.chat_emotes.TwitchEmotesManager
import com.android.multistreamplayer.databinding.EmoteItemLayoutBinding
import com.bumptech.glide.Glide

class EmoteAdapter : RecyclerView.Adapter<EmoteAdapter.MyViewHolder>() {

    var twitchEmotesList: List<TwitchEmotesManager.TwitchEmote>? = null
        set(value) {
            field= value
            notifyDataSetChanged()
        }

    class MyViewHolder(val emoteItemLayoutBinding: EmoteItemLayoutBinding) : RecyclerView.ViewHolder(emoteItemLayoutBinding.root) {

    }

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
