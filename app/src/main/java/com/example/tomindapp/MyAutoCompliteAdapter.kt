package com.example.tomindapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import java.util.ArrayList

class MyAutoCompliteAdapter (val context: Context, var titleArr:ArrayList<String>):BaseAdapter(),Filterable{
    var myTitleArr = titleArr
    override fun getFilter(): Filter {
        myTitleArr = titleArr
        var filter = object:Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                var filterResults = FilterResults()
                var filteredTitles = arrayListOf<String>()
                if (constraint!=null) {
                    for (t in myTitleArr) {
                        if (t.contains(constraint,true)) filteredTitles.add(t)
                    }
                    filterResults.values = filteredTitles
                    filterResults.count = filteredTitles.size

                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results!=null&&results.count>0){
                    titleArr=results.values as ArrayList<String>
                    notifyDataSetChanged()
                } else {notifyDataSetInvalidated()}
                myTitleArr.clear()
            }

        }
                return filter
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var retView:View?
        val title = titleArr[position]
        if (convertView==null){
            val inflater = LayoutInflater.from(context)
            retView = inflater.inflate(R.layout.dropdown_item,parent,false)
        } else {retView=convertView}

        retView!!.findViewById<TextView>(R.id.tvDropListItem).setText(title)

        return retView!!
    }

    override fun getItem(position: Int): Any {
        return titleArr.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return titleArr.size
    }


}