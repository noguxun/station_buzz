package com.xgu.trainbuz

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.SearchView
import kotlinx.android.synthetic.main.activity_select_station.*

class SelectStationActivity : AppCompatActivity() {

    lateinit var stationAdapter: StationListAdaptor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_station)

        stationAdapter = StationListAdaptor()

        listView.adapter = stationAdapter
        stationAdapter.loadStationData(this)

        searchView.apply {
            isActivated = true
            queryHint = "Type the station name here"
            onActionViewExpanded()
            isIconified = true
            clearFocus()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                println("onQueryTextSubmit ${query}")
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                println("onQueryTextChange ${newText}")
                stationAdapter.filter.filter(newText)
                // TODO
                return false
            }
        })
    }
}
