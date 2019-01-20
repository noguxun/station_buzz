package com.xgu.trainbuz

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.xgu.trainbuz.databinding.StationListItemBinding
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset

// we need to show prefecture name
//  http://www.ekidata.jp/doc/pref.php
class StationListAdaptor(activity: AppCompatActivity) : BaseAdapter(), Filterable {
    val parentActivity = activity

    val prefectures = arrayOf(
        "北海道",
        "青森県",
        "岩手県",
        "宮城県",
        "秋田県",
        "山形県",
        "福島県",
        "茨城県",
        "栃木県",
        "群馬県",
        "埼玉県",
        "千葉県",
        "東京都",
        "神奈川県",
        "新潟県",
        "富山県",
        "石川県",
        "福井県",
        "山梨県",
        "長野県",
        "岐阜県",
        "静岡県",
        "愛知県",
        "三重県",
        "滋賀県",
        "京都府",
        "大阪府",
        "兵庫県",
        "奈良県",
        "和歌山県",
        "鳥取県",
        "島根県",
        "岡山県",
        "広島県",
        "山口県",
        "徳島県",
        "香川県",
        "愛媛県",
        "高知県",
        "福岡県",
        "佐賀県",
        "長崎県",
        "熊本県",
        "大分県",
        "宮崎県",
        "鹿児島県",
        "沖縄県"
    )

    class Station(stationId: Int, groupId: Int,
                  nameKanji: String, nameKata: String, nameHira: String, nameRoma: String,
                  lon: Double, lat: Double, prefecture: Int){
        val stationId = stationId
        val groupId = groupId
        val nameKanji = nameKanji
        val nameKata = nameKata
        val nameHira = nameHira
        val nameRoma = nameRoma
        val lon = lon
        val lat = lat
        val prefecture = prefecture
    }

    var stationList = ArrayList<Station>()
    var showList = ArrayList<Station>()
    val maxShowCount = 10
    private val filter = StationFilter()

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

        var station = showList[position]
        val prefectureName = prefectures[station.prefecture - 1]
        itemBinding.stationName.text = station.nameKanji
        itemBinding.stationPrefecture.text = prefectureName

        itemBinding.root.setOnClickListener{
            Toast.makeText(parent.context, "Clicked: ${showList[position].nameKanji}", Toast.LENGTH_SHORT).show()
            val intent = Intent(parentActivity, BuzzStartActivity::class.java)
            intent.putExtra("STATION_NAME", station.nameKanji)
            intent.putExtra("STATION_PRE", prefectureName)
            intent.putExtra("STATION_LON", station.lon)
            intent.putExtra("STATION_LAT", station.lat)
            parentActivity.startActivity(intent)
        }

        return itemBinding.root
    }

    override fun getFilter(): Filter {
        return filter
    }

    private inner class StationFilter : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()

            if (constraint == null || constraint.isEmpty()) {
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

                return results
            }

            val filterList = ArrayList<Station>()

            var count = 0
            for (i in stationList.indices) {
                if (count > maxShowCount) {
                    break
                }

                if (stationList[i].nameRoma.contains(constraint.toString().toUpperCase())) {
                    filterList.add(stationList[i])
                    count += 1
                    println("${i} ${stationList[i].nameRoma}")
                    continue
                }

                if (stationList[i].nameKanji.contains(constraint.toString())) {
                    filterList.add(stationList[i])
                    count += 1
                    println("${i} ${stationList[i].nameKanji}")
                    continue
                }

                if (stationList[i].nameKata.contains(constraint.toString())) {
                    filterList.add(stationList[i])
                    count += 1
                    println("${i} ${stationList[i].nameKata}")
                    continue
                }

                if (stationList[i].nameHira.contains(constraint.toString())) {
                    filterList.add(stationList[i])
                    count += 1
                    println("${i} ${stationList[i].nameHira}")
                    continue
                }

                // println("${stationList[i].nameRoma}")
            }

            println("Filtering Constraint ${constraint.toString().toUpperCase()}")

            results.count = filterList.size
            results.values = filterList

            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            val oldSize = showList.size
            var i = oldSize
            while (i > 0) {
                showList.removeAt(i - 1)
                i = i - 1
            }

            println("Publishing Result ${constraint.toString().toUpperCase()}")

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

            val cols = line?.split(",")

            cols?.let {
                val station = Station(it[0].toInt(), it[1].toInt(), it[2], it[3], it[4],
                    it[5].toUpperCase(), it[6].toDouble(), it[7].toDouble(), it[8].toInt())
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