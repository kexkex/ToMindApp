package com.example.tomindapp


import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.bottom_sheet_fragment.*
import kotlinx.android.synthetic.main.bottom_sheet_fragment.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class BottomSheetFragment : BottomSheetDialogFragment() {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v=inflater.inflate(R.layout.bottom_sheet_fragment,container,false)
        val tvTitle = v.tvSheetTitle
        val tvDescr = v.tvSheetDescr
        val tvLink = v.tvSheetLink
        val bundle = arguments
        if (bundle!=null){
            tvTitle.text=bundle!!.getString("title")
            tvDescr.text=bundle!!.getString("descr")
            tvLink.text=bundle!!.getString("link")}
        return v
        }
}



