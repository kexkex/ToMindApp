package ru.tomindapps.tominddictionary

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import ru.tomindapps.tominddictionary.R
import kotlinx.android.synthetic.main.item_container.view.*
import ru.tomindapps.tominddictionary.WordsAdapter.MyViewHolder



class WordsAdapter (var wordsList:ArrayList<InterestWord>, listener: MyAdapterListener):
    RecyclerView.Adapter<MyViewHolder>()  {

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
        holder.buDone.setOnClickListener(View.OnClickListener { listener.onWikiClicked(position) })
        holder.buEdit.setOnClickListener(View.OnClickListener { listener.onEditClicked(position) })
        holder.messageContainer.setOnClickListener(View.OnClickListener { listener.onMessageRowClicked(position) })
    }

    interface MyAdapterListener {
        fun onWikiClicked(position: Int)
        fun onMessageRowClicked(position: Int)
        fun onEditClicked(position: Int)
    }

    inner class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {

        val tvRvTitle=itemView.tvRvTitle
        val tvRvDescr=itemView.tvRvDescr
        val tvRvDate=itemView.tvRvDate
        val buEdit=itemView.buEdit
        val buDone=itemView.buDone
        val messageContainer=itemView.rvItem

        fun bind(interestWord: InterestWord) {

            if (interestWord.link!="") buDone.visibility=Button.VISIBLE else buDone.visibility=Button.INVISIBLE

            tvRvTitle.text = interestWord.interestWord
            tvRvDescr.text = interestWord.wordDescription
            tvRvDate.text = interestWord.date
        }

    }




}