
/*import android.R
import android.widget.TextView
import android.support.v7.widget.RecyclerView

import android.R
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView

class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var iconImageView: ImageView
    var iconTextView: TextView

    init {

        iconImageView = itemView.findViewById(R.id.myRecyclerImageView) as ImageView
        iconTextView = itemView.findViewById(R.id.myRecyclerTextView)

        iconTextView.setOnClickListener(object : View.OnClickListener() {
            override fun onClick(v: View) {
                onClickListener.iconTextViewOnClick(v, adapterPosition)
            }
        })
        iconImageView.setOnClickListener(object : View.OnClickListener() {
            fun onClick(v: View) {
                onClickListener.iconImageViewOnClick(v, adapterPosition)
            }
        })
    }
}*/