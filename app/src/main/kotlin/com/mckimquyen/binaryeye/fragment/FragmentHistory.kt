package com.mckimquyen.binaryeye.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SwitchCompat
import android.view.*
import android.widget.EditText
import android.widget.ListView
import com.mckimquyen.binaryeye.R
import com.mckimquyen.binaryeye.adapter.ScansAdapter
import com.mckimquyen.binaryeye.app.addFragment
import com.mckimquyen.binaryeye.app.alertDialog
import com.mckimquyen.binaryeye.app.hasWritePermission
import com.mckimquyen.binaryeye.prefs
import com.mckimquyen.binaryeye.content.copyToClipboard
import com.mckimquyen.binaryeye.content.shareText
import com.mckimquyen.binaryeye.db
import com.mckimquyen.binaryeye.db.Db
import com.mckimquyen.binaryeye.db.exportCsv
import com.mckimquyen.binaryeye.db.exportDatabase
import com.mckimquyen.binaryeye.db.exportJson
import com.mckimquyen.binaryeye.io.askForFileName
import com.mckimquyen.binaryeye.io.toSaveResult
import com.mckimquyen.binaryeye.view.lockStatusBarColor
import com.mckimquyen.binaryeye.view.setPaddingFromWindowInsets
import com.mckimquyen.binaryeye.view.systemBarListViewScrollListener
import com.mckimquyen.binaryeye.view.unlockStatusBarColor
import com.mckimquyen.binaryeye.view.useVisibility
import com.mckimquyen.binaryeye.widget.toast
import kotlinx.coroutines.*

class FragmentHistory : Fragment() {
    private lateinit var useHistorySwitch: SwitchCompat
    private lateinit var listView: ListView
    private lateinit var fab: View
    private lateinit var progressView: View

    private val parentJob = Job()
    private val scope = CoroutineScope(Dispatchers.IO + parentJob)
    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(
            mode: ActionMode,
            menu: Menu,
        ): Boolean {
            mode.menuInflater.inflate(
                R.menu.menu_f_history_edit,
                menu
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                lockStatusBarColor()
                val ac = activity ?: return false
                ac.window.statusBarColor = ContextCompat.getColor(
                    ac,
                    R.color.accentDark
                )
            }
            return true
        }

        override fun onPrepareActionMode(
            mode: ActionMode,
            menu: Menu,
        ): Boolean {
            return false
        }

        override fun onActionItemClicked(
            mode: ActionMode,
            item: MenuItem,
        ): Boolean {
            val ac = activity ?: return false
            return when (item.itemId) {
                R.id.copyScan -> {
                    scansAdapter?.getSelectedContent("\n")?.let {
                        ac.copyToClipboard(it)
                        ac.toast(R.string.copied_to_clipboard)
                    }
                    closeActionMode()
                    true
                }

                R.id.editScan -> {
                    scansAdapter?.forSelection { id, position ->
                        ac.askForName(
                            id,
                            scansAdapter?.getName(position),
                            scansAdapter?.getContent(position)
                        )
                    }
                    closeActionMode()
                    true
                }

                R.id.removeScan -> {
                    scansAdapter?.getSelectedIds()?.let {
                        if (it.isNotEmpty()) {
                            ac.askToRemoveScans(it)
                        }
                    }
                    closeActionMode()
                    true
                }

                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            closeActionMode()
        }
    }

    private var scansAdapter: ScansAdapter? = null
    private var listViewState: Parcelable? = null
    private var actionMode: ActionMode? = null
    private var filter: String? = null
    private var clearListMenuItem: MenuItem? = null
    private var exportHistoryMenuItem: MenuItem? = null

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        state: Bundle?,
    ): View? {
        val ac = activity ?: return null
        ac.setTitle(R.string.history)

        val view = inflater.inflate(
            R.layout.f_history,
            container,
            false
        )

        useHistorySwitch = view.findViewById(
            R.id.useHistory
        ) as SwitchCompat
        initHistorySwitch(useHistorySwitch)

        listView = view.findViewById(R.id.scans)
        listView.setOnItemClickListener { _, _, _, id ->
            showScan(id)
        }
        listView.setOnItemLongClickListener { _, v, position, id ->
            scansAdapter?.select(v, id, position)
            if (actionMode == null && ac is AppCompatActivity) {
                actionMode = ac.delegate.startSupportActionMode(
                    actionModeCallback
                )
            }
            true
        }
        listView.setOnScrollListener(systemBarListViewScrollListener)

        fab = view.findViewById(R.id.share)
        fab.setOnClickListener { v ->
            v.context.pickListSeparatorAndShare()
        }

        progressView = view.findViewById(R.id.progressView)

        (view.findViewById(R.id.insetLayout) as View).setPaddingFromWindowInsets()
        listView.setPaddingFromWindowInsets()

        update()

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        scansAdapter?.changeCursor(null)
        parentJob.cancel()
    }

    override fun onPause() {
        super.onPause()
        listViewState = listView.onSaveInstanceState()
    }

    override fun onCreateOptionsMenu(
        menu: Menu,
        inflater: MenuInflater,
    ) {
        inflater.inflate(R.menu.menu_f_history, menu)
        initSearchView(menu.findItem(R.id.search))
        menu.setGroupVisible(R.id.scansAvailable, scansAdapter?.count != 0)
        clearListMenuItem = menu.findItem(R.id.clear)
        exportHistoryMenuItem = menu.findItem(R.id.exportHistory)
    }

    private fun initSearchView(item: MenuItem?) {
        item ?: return
        val ac = activity ?: return
        val searchView = item.actionView as SearchView
        val searchManager = ac.getSystemService(
            Context.SEARCH_SERVICE
        ) as SearchManager
        searchView.setSearchableInfo(
            searchManager.getSearchableInfo(ac.componentName)
        )
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                update(query)
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                update(query)
                return false
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.clear -> {
                context?.askToRemoveScans()
                true
            }

            R.id.exportHistory -> {
                askToExportToFile()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initHistorySwitch(switchView: SwitchCompat) {
        switchView.setOnCheckedChangeListener { _, isChecked ->
            prefs.useHistory = isChecked
        }
        if (prefs.useHistory) {
            switchView.toggle()
        }
    }

    private fun updateAndClearFilter() {
        filter = null
        update()
    }

    private fun update(query: String? = null) {
        query?.let { filter = it }
        scope.launch {
            val cursor = db.getScans(filter)
            withContext(Dispatchers.Main) {
                val ac = activity ?: return@withContext
                val hasScans = cursor != null && cursor.count > 0
                if (filter == null) {
                    if (!hasScans) {
                        listView.emptyView = useHistorySwitch
                    }
                    ActivityCompat.invalidateOptionsMenu(ac)
                }
                enableMenuItems(hasScans)
                fab.visibility = if (hasScans) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
                cursor?.let { cursor ->
                    // Close previous cursor.
                    scansAdapter?.also { it.changeCursor(null) }
                    scansAdapter = ScansAdapter(ac, cursor)
                    listView.adapter = scansAdapter
                    listViewState?.also {
                        listView.onRestoreInstanceState(it)
                    }
                }
            }
        }
    }

    private fun enableMenuItems(enabled: Boolean) {
        clearListMenuItem?.isEnabled = enabled
        exportHistoryMenuItem?.isEnabled = enabled
    }

    private fun closeActionMode() {
        unlockStatusBarColor()
        scansAdapter?.clearSelection()
        actionMode?.finish()
        actionMode = null
        scansAdapter?.notifyDataSetChanged()
    }

    private fun showScan(id: Long) = db.getScan(id)?.also { scan ->
        closeActionMode()
        try {
            fragmentManager?.addFragment(FragmentDecode.newInstance(scan))
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    // Dialogs don't have a parent layout.
    @SuppressLint("InflateParams")
    private fun Context.askForName(
        id: Long,
        text: String?,
        content: String?,
    ) {
        val view = LayoutInflater.from(this).inflate(
            R.layout.dlg_enter_name, null
        )
        val nameView = view.findViewById<EditText>(R.id.name)
        nameView.setText(text)
        AlertDialog.Builder(this)
            .setTitle(
                if (content.isNullOrEmpty()) {
                    getString(R.string.binary_data)
                } else {
                    content
                }
            )
            .setView(view)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val name = nameView.text.toString()
                db.renameScan(id, name)
                update()
            }
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .show()
    }

    private fun Context.askToRemoveScans(ids: List<Long>) {
        AlertDialog.Builder(this)
            .setMessage(
                if (ids.size > 1) {
                    R.string.reallyRemoveSelectedScans
                } else {
                    R.string.really_remove_scan
                }
            )
            .setPositiveButton(android.R.string.ok) { _, _ ->
                ids.forEach { db.removeScan(it) }
                if (scansAdapter?.count == 1) {
                    updateAndClearFilter()
                } else {
                    update()
                }
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->
            }
            .show()
    }

    private fun Context.askToRemoveScans() {
        AlertDialog.Builder(this)
            .setMessage(
                if (filter == null) {
                    R.string.reallyRemoveAllScans
                } else {
                    R.string.reallyRemoveSelectedScans
                }
            )
            .setPositiveButton(android.R.string.ok) { _, _ ->
                db.removeScans(filter)
                updateAndClearFilter()
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->
            }
            .show()
    }

    private fun askToExportToFile() {
        scope.launch {
            val ac = activity ?: return@launch
            progressView.useVisibility {
                // Write permission is only required before Android Q.
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
                    !ac.hasWritePermission { askToExportToFile() }
                ) {
                    return@useVisibility
                }
                val options = ac.resources.getStringArray(
                    R.array.exportOptionsValues
                )
                val delimiter = alertDialog<String>(ac) { resume ->
                    setTitle(R.string.exportAs)
                    setItems(R.array.exportOptionsNames) { _, which ->
                        resume(options[which])
                    }
                } ?: return@useVisibility
                val name = withContext(Dispatchers.Main) {
                    ac.askForFileName(
                        when (delimiter) {
                            "db" -> ".db"
                            "json" -> ".json"
                            else -> ".csv"
                        }
                    )
                } ?: return@useVisibility
                val message = when (delimiter) {
                    "db" -> ac.exportDatabase(name)
                    else -> db.getScansDetailed(filter)?.use {
                        when (delimiter) {
                            "json" -> ac.exportJson(name, it)
                            else -> ac.exportCsv(name, it, delimiter)
                        }
                    } ?: false
                }.toSaveResult()
                withContext(Dispatchers.Main) {
                    ac.toast(message)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun Context.pickListSeparatorAndShare() {
        val separators = resources.getStringArray(
            R.array.listSeparatorsValues
        )
        AlertDialog.Builder(this)
            .setTitle(R.string.pickListSeparator)
            .setItems(R.array.listSeparatorsNames) { _, which ->
                shareScans(separators[which])
            }
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun shareScans(format: String) = scope.launch {
        progressView.useVisibility {
            var text: String? = null
            db.getScansDetailed(filter)?.use { cursor ->
                val details = format.split(":")
                text = when (details[0]) {
                    "text" -> cursor.exportText(details[1])
                    "csv" -> cursor.exportCsv(details[1])
                    else -> cursor.exportJson()
                }
            }
            text?.let {
                withContext(Dispatchers.Main) {
                    context?.shareText(it)
                }
            }
        }
    }
}

private fun Cursor.exportText(separator: String): String {
    val sb = StringBuilder()
    val contentIndex = getColumnIndex(Db.SCANS_CONTENT)
    if (contentIndex > -1 && moveToFirst()) {
        do {
            val content = getString(contentIndex)
            if (content?.isNotEmpty() == true) {
                sb.append(content)
                sb.append(separator)
            }
        } while (moveToNext())
    }
    return sb.toString()
}
