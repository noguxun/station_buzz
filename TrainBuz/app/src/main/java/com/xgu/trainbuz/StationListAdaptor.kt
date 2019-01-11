package com.xgu.trainbuz

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset

class StationListAdaptor : BaseAdapter(), Filterable {
    class Station(stationId: Int, groupId: Int,
                  nameKanji: String, nameKata: String, nameHira: String, nameRoma: String,
                  lon: Double, lat: Double){
        val stationId = stationId
        val groupId = groupId
        val nameKanji = nameKanji
        val nameKata = nameKata
        val nameHira = nameHira
        val nameRoma = nameRoma
        val lon = lon
        val lat = lat
    }

    var stationList: MutableList<Station> = ArrayList()

    override fun getCount(): Int {
        return stationList.size
    }

    override fun getItem(position: Int): String {
        return stationList[position].nameKanji
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

            notifyDataSetChanged()
        }
    }

    fun loadStationData(context: Context) {
        val csvStreamReader = context.resources.openRawResource(R.raw.station)

        val bufferReader = BufferedReader(InputStreamReader(csvStreamReader, Charset.forName("UTF-8")))

        var count = 0

        val maxCount = 100

        while (true) {
            val line: String? = bufferReader.readLine() ?: break

            count += 1

            if (count == 1) {
                // skip the first line with title
                continue
            }

            val cols = line?.split(",")

            if (count < maxCount) {
                cols?.let {
                    val station = Station(it[0].toInt(), it[1].toInt(), it[2], it[3], it[4], it[5], it[6].toDouble(), it[7].toDouble())
                    stationList.add(station)
                    // val station = T4Station(cols[2].trim(), cols[9].toDouble(), cols[10].toDouble())
                    // println("${cols[0]} ${cols[1]} ${cols[2]} ${cols[3]} ${cols[4]} ${cols[5]} ${cols[6]} ${it[7]}")

                }
            } else {
                break
            }
        }

        for (station in stationList) {
            println("${station.nameKanji} ${station.lat} ${station.lon}")
        }
    }
}