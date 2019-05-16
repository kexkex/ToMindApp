package com.example.tomindapp

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.item_container.view.*
import com.example.tomindapp.WordsAdapter.MyViewHolder



class WordsAdapter (var wordsList:ArrayList<InterestWord>, listener: MyAdapterListener):
    RecyclerView.Adapter<WordsAdapter.MyViewHolder>()  {
    var listener: MyAdapterListener
    init {
        this.listener=listener
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {
        val v=LayoutInflater.from(p0.context).inflate(R.layout.item_container,p0,false)

        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return wordsList.size
    }

    override fun onBindViewHolder(p0: MyViewHolder, p1: Int) {
        p0.bind(wordsList[p1])
        applyClickEvents(p0, p1)

    }

    private fun applyClickEvents(holder: MyViewHolder, position: Int) {
        holder.buEdit.setOnClickListener(View.OnClickListener { listener.onEditClicked(position) })

        holder.buDone.setOnClickListener(View.OnClickListener { listener.onDoneClicked(position) })

       holder.messageContainer.setOnClickListener(View.OnClickListener { listener.onMessageRowClicked(position) })

        holder.messageContainer.setOnLongClickListener(View.OnLongClickListener { view ->
            listener.onRowLongClicked(position)
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            true
        })
    }

    interface MyAdapterListener {
        fun onEditClicked(position: Int)

        fun onDoneClicked(position: Int)

        fun onMessageRowClicked(position: Int)

        fun onRowLongClicked(position: Int)
    }

    inner class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {

        val tvRvGroup=itemView.tvRvGroup
        val tvRvTitle=itemView.tvRvTitle
        val tvRvDescr=itemView.tvRvDescr
        val tvRvDate=itemView.tvRvDate
        val buEdit=itemView.buEdit
        val buDone=itemView.buDone
        val messageContainer=itemView.rvItem

        fun bind(interestWord: InterestWord) {
            tvRvGroup.text = interestWord.groupId
            tvRvTitle.text = interestWord.interestWord
            tvRvDescr.text = interestWord.wordDescription
            tvRvDate.text = interestWord.date

        }

    }




}