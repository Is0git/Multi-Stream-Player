package com.android.multistreamplayer.chat.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.multistreamchat.chat.chat_parser.ChatParser
import com.android.multistreamplayer.R
import com.android.multistreamplayer.databinding.ChatLayoutBinding
import com.bumptech.glide.Glide

class ChatAdapter(var context: Context) : RecyclerView.Adapter<ChatAdapter.MyViewHolder>() {

    var linesList: MutableList<ChatParser.Message> = mutableListOf()

    var colorOne = ResourcesCompat.getColor(context.resources, R.color.colorSurface, context.theme)

    var colorTwo = ResourcesCompat.getColor(context.resources, R.color.colorLight, context.theme)

    class MyViewHolder(val binding: ChatLayoutBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun getItemViewType(position: Int): Int {
        return position % 2
    }

    fun addLine(line: ChatParser.Message?) {
        line?.let {
            if (linesList.count() > 50) linesList.clear().also { notifyDataSetChanged() }
            linesList.add(line)
            notifyItemChanged(linesList.count() - 1)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ChatLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        when(viewType) {
//            0 -> binding.root.setBackgroundColor(colorOne)
//            else -> binding.root.setBackgroundColor(colorTwo)
//        }
        return MyViewHolder(
            binding
        )
    }

    override fun getItemCount(): Int  = linesList.count()

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.binding.user.apply {
            text = linesList[position].username + ":"
            setTextColor(Color.parseColor(linesList[position].usernameColor))
        }
        holder.binding.text.text = if (linesList[position].spannableMessage.isNullOrEmpty()) linesList[position].message else linesList[position].spannableMessage
        if (linesList[position].badges != null && linesList[position].badges?.isNotEmpty()!!)
            Glide.with(holder.binding.badge).load(linesList[position].badges?.get(0)).into(holder.binding.badge)


//        holder.binding.text.setTextColor(Color.parseColor(linesList[position].usernameColor))
    }


}