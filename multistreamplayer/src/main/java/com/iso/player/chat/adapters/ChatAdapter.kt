package com.iso.player.chat.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.text.Spannable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.iso.chat.chat.Chat
import com.iso.player.R
import com.iso.player.databinding.ChatLayoutBinding
import java.util.*

class ChatAdapter(var context: Context) : RecyclerView.Adapter<ChatAdapter.MyViewHolder>() {

    var linesList: LinkedList<Spannable> = LinkedList()
    var colorOne = ResourcesCompat.getColor(context.resources, R.color.colorSurface, context.theme)
    var colorTwo = ResourcesCompat.getColor(context.resources, R.color.colorLight, context.theme)

    class MyViewHolder(val binding: ChatLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        return position % 2
    }

    fun addLine(line: Spannable) {
        line.let {
            linesList.add(it)
            notifyItemInserted((linesList.count() - 1).coerceAtLeast(0))
            if (linesList.count() > 50) {
                linesList.poll()
                notifyItemRemoved(0)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ChatLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = linesList.count()

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        try {
            holder.binding.text.text = linesList[position]
        } catch (e:Exception) {
            Log.d(Chat.TAG, "exception in chat: $e")
        }

    }
}
