package com.roy.binaryeye.fragment

import android.content.Context
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.view.*
import android.widget.ArrayAdapter
import android.widget.ListView
import com.roy.binaryeye.R
import com.roy.binaryeye.view.setPaddingFromWindowInsets
import com.roy.binaryeye.view.systemBarListViewScrollListener
import com.roy.binaryeye.widget.toast

class NetworkSuggestionsFragment : Fragment() {
	@RequiresApi(Build.VERSION_CODES.R)
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		state: Bundle?
	): View? {
		val ac = activity ?: return null
		ac.setTitle(R.string.network_suggestions)

		val view = inflater.inflate(
			R.layout.f_network_suggestions,
			container,
			false
		)

		val wm = ac.applicationContext.getSystemService(
			Context.WIFI_SERVICE
		) as WifiManager
		val suggestionArrayAdapter = ArrayAdapter(
			ac,
			android.R.layout.simple_list_item_checked,
			wm.networkSuggestions.map {
				Suggestion(
					it.ssid ?: it.toString(),
					it
				)
			}
		)

		val listView = view.findViewById<ListView>(R.id.suggestions)
		listView.emptyView = view.findViewById(R.id.noSuggestions)
		listView.adapter = suggestionArrayAdapter
		listView.setOnScrollListener(systemBarListViewScrollListener)
		(view.findViewById(R.id.insetLayout) as View).setPaddingFromWindowInsets()
		listView.setPaddingFromWindowInsets()

		view.findViewById<View>(R.id.remove).setOnClickListener {
			val removeList = ArrayList<WifiNetworkSuggestion>()
			val removeFromAdapter = ArrayList<Suggestion>()
			val checked = listView.checkedItemPositions
			val size = checked.size()
			for (i in 0 until size) {
				if (checked.valueAt(i)) {
					val pos = checked.keyAt(i)
					listView.setItemChecked(pos, false)
					val suggestion = listView.getItemAtPosition(
						pos
					) as Suggestion
					removeList.add(suggestion.suggestion)
					removeFromAdapter.add(suggestion)
				}
			}
			if (removeList.isNotEmpty()) {
				removeFromAdapter.forEach {
					suggestionArrayAdapter.remove(it)
				}
				wm.removeNetworkSuggestions(removeList)
			} else {
				ac.toast(
					R.string.clear_network_suggestions_nothing_to_remove
				)
			}
		}

		return view
	}
}

private data class Suggestion(
	val label: String,
	val suggestion: WifiNetworkSuggestion
) {
	override fun toString() = label
}
