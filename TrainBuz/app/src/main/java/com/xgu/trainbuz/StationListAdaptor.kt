package com.xgu.trainbuz

import android.content.Context
import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.xgu.trainbuz.databinding.StationListItemBinding
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
    var showList: MutableList<Station> = ArrayList()
    val maxShowCount = 10

    override fun getCount(): Int {
        return showList.size
    }

    override fun getItem(position: Int): String {
        return showList[position].nameKanji
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = parent?.context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val itemBinding = DataBindingUtil.inflate(inflater, R.layout.station_list_item, null, false) as StationListItemBinding

        itemBinding.stringName.text = showList[position].nameKanji

        itemBinding.root.setOnClickListener{
            Toast.makeText(parent.context, "Clicked: ${showList[position].nameKanji}", Toast.LENGTH_SHORT).show()
        }

        return itemBinding.root
    }

    override fun getFilter(): Filter {
        return StationFilter()
    }

    private inner class StationFilter : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()

            if (constraint != null && constraint.isNotEmpty()) {
                val filterList = ArrayList<Station>()

                var count = 0
                for (i in stationList.indices) {
                    if (stationList[i].nameRoma.toUpperCase().contains(constraint.toString().toUpperCase())) {
                        filterList.add(stationList[i])
                        count += 1
                        println("${stationList[i].nameRoma}")
                    }

                    if (count > maxShowCount) {
                        break
                    }

                    // println("${stationList[i].nameRoma}")
                }

                println("Filtering Constraint ${constraint.toString().toUpperCase()}")

                results.count = filterList.size
                results.values = filterList
            } else {
                println("Constraint is None")

                val filterList = ArrayList<Station>()
                var count = 0
                for (i in stationList.indices) {
                    filterList.add(stationList[i])
                    count += 1

                    if (count > maxShowCount) {
                        break
                    }
                }

                results.count = filterList.size
                results.values = filterList
            }

            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            val oldSize = showList.size
            var i = oldSize
            while (i > 0) {
                showList.removeAt(i - 1)
                i = i - 1
            }

            val stationList = results.values as List<Station>
            for (station in stationList) {
                showList.add(station)
            }

            notifyDataSetChanged()
        }
    }

    fun loadStationData(context: Context) {
        val csvStreamReader = context.resources.openRawResource(R.raw.station)

        val bufferReader = BufferedReader(InputStreamReader(csvStreamReader, Charset.forName("UTF-8")))

        var count = 0
        while (true) {
            val line: String? = bufferReader.readLine() ?: break

            count += 1

            if (count == 1) {
                // skip the first line with title
                continue
            }

            val cols = line?.split(",")

            cols?.let {
                val station = Station(it[0].toInt(), it[1].toInt(), it[2], it[3], it[4], it[5], it[6].toDouble(), it[7].toDouble())
                stationList.add(station)
                // val station = T4Station(cols[2].trim(), cols[9].toDouble(), cols[10].toDouble())
                // println("${cols[0]} ${cols[1]} ${cols[2]} ${cols[3]} ${cols[4]} ${cols[5]} ${cols[6]} ${it[7]}")

            }
        }

        for (i in stationList.indices) {
            val station = stationList[i]
            println("${station.nameKanji} ${station.lat} ${station.lon}")
            showList.add(station)

            if (i > maxShowCount) {
                break
            }
        }
    }
}