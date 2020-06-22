package com.iso.player.emoticon_picker.adapters

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iso.chat.chat.chat_emotes.EmotesManager
import com.iso.chat.twitch_chat.chat_emotes.TwitchEmotesManager
import com.iso.player.R

class EmotesViewpagerAdapter :
    RecyclerView.Adapter<EmotesViewpagerAdapter.EmoteViewHolder>() {

    var data: MutableMap<String, List<EmotesManager.Emote>>? = mutableMapOf()
    var onItemClickListener: OnEmoteLayoutListener? = null

    fun putData(data: String, list: List<EmotesManager.Emote>) {
        this.data?.put(data, list)
        notifyItemInserted(data.count() - 1)
    }


    class EmoteViewHolder(
        view: View,
        onItemOnClickListener: OnEmoteLayoutListener? = null
    ) : RecyclerView.ViewHolder(view) {

        var adapter: EmoteSetItemAdapter
        var list: RecyclerView = view.findViewById(R.id.emotes_list)
        var items: List<TwitchEmotesManager.TwitchEmote>? = null

        init {
            adapter = EmoteSetItemAdapter(onItemOnClickListener)
            adapter.emotesList = items
            list.apply {
                adapter = this@EmoteViewHolder.adapter
                if (layoutManager is GridLayoutManager) (layoutManager as GridLayoutManager).spanCount =
                    calculateSpanCount()
            }
        }

        private fun calculateSpanCount(): Int {
            val displayMetrics = itemView.context.resources.displayMetrics
            val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35f, displayMetrics)
            val spanCount = (displayMetrics.widthPixels / px).toInt()
            return spanCount.coerceAtMost(7)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.emotes_list, parent, false)
        return EmoteViewHolder(view, onItemClickListener)
    }

    override fun getItemCount(): Int {
        return data?.count() ?: 0
    }

    override fun onBindViewHolder(holder: EmoteViewHolder, position: Int) {
        holder.adapter.emotesList = data?.values?.elementAt(position)
    }

    fun find(text: String) {
        data?.also { map ->
            if (map.count() < 1) return
            val emotes: MutableList<EmotesManager.Emote> = mutableListOf()
            for (i in 0 until map.count() - 1) {
                val list = data?.values?.elementAt(i)?.filter {
                    it.code?.contains(text, true) ?: false
                }
                if (list != null) emotes.addAll(list)
            }
            data!!["Search"] = emotes
            notifyItemChanged(data?.count()!! - 1)
        }
    }

}