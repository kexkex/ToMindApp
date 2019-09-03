package ru.tomindapps.tominddictionary.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.tomindapps.tominddictionary.R
import kotlinx.android.synthetic.main.bottom_sheet_fragment.view.*
import java.net.URLDecoder


class BottomSheetFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val v=inflater.inflate(R.layout.bottom_sheet_fragment,container,false)
        val tvTitle = v.tvSheetTitle
        val tvDescr = v.tvSheetDescr
        val tvLink = v.tvSheetLink
        var link = ""

        val bundle = arguments
        if (bundle!=null){
            tvTitle.text= bundle.getString("title")
            tvDescr.text= bundle.getString("descr")
            link = bundle.getString("link")}
            tvLink.text= URLDecoder.decode(link)

        return v
        }
}



