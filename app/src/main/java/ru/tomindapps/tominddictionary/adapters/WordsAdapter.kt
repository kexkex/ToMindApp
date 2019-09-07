package ru.tomindapps.tominddictionary.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_container.view.*
import ru.tomindapps.tominddictionary.R
import ru.tomindapps.tominddictionary.adapters.WordsAdapter.MyViewHolder
import ru.tomindapps.tominddictionary.models.InterestWord


class WordsAdapter (listener: MyAdapterListener):
    RecyclerView.Adapter<MyViewHolder>()  {

    var listener: MyAdapterListener = listener
    var wordsList = listOf<InterestWord>()

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

    fun updateList(words: List<InterestWord>){
        val diffUtilCallback = object : DiffUtil.Callback(){
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return wordsList[oldItemPosition].idWord == words[newItemPosition].idWord
            }

            override fun getOldListSize(): Int {
                return wordsList.size
            }

            override fun getNewListSize(): Int {
                return words.size
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return wordsList[oldItemPosition].hashCode() == words[newItemPosition].hashCode()
            }
        }
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)
        wordsList = words
        diffResult.dispatchUpdatesTo(this)

    }

    private fun applyClickEvents(holder: MyViewHolder, position: Int) {
        holder.buDone.setOnClickListener{ listener.onWikiClicked(position) }
        holder.buEdit.setOnClickListener{ listener.onEditClicked(position) }
        holder.messageContainer.setOnClickListener{ listener.onMessageRowClicked(position) }
    }

    interface MyAdapterListener {
        fun onWikiClicked(position: Int)
        fun onMessageRowClicked(position: Int)
        fun onEditClicked(position: Int)
    }

    inner class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {

        val tvRvTitle = itemView.tvRvTitle
        val tvRvDescr = itemView.tvRvDescr
        val tvRvDate = itemView.tvRvDate
        val buEdit = itemView.buEdit
        val buDone = itemView.buDone
        val messageContainer = itemView.rvItem

        fun bind(interestWord: InterestWord) {

            if (interestWord.link != "") buDone.visibility = Button.VISIBLE else buDone.visibility = Button.INVISIBLE

            tvRvTitle.text = interestWord.interestWord
            tvRvDescr.text = interestWord.wordDescription
            tvRvDate.text = interestWord.date
        }

    }




}