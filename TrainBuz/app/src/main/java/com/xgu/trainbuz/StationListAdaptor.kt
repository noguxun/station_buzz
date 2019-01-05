package com.xgu.trainbuz

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView

class StationListAdaptor : BaseAdapter(), Filterable {
    lateinit var mData: List<String>

    override fun getCount(): Int {
        return mData.size
    }

    override fun getItem(position: Int): String {
        return mData[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = parent?.context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        // TODO
        return TextView(parent?.context)
    }

    override fun getFilter(): Filter {
        return StationFilter()
    }

    private inner class StationFilter : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()

            // TODO

            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            mData = results.values as List<String>
            notifyDataSetChanged()
        }
    }
}