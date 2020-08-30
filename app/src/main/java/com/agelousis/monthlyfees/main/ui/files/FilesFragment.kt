package com.agelousis.monthlyfees.main.ui.files

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import com.agelousis.monthlyfees.R
import com.agelousis.monthlyfees.custom.enumerations.SwipeAction
import com.agelousis.monthlyfees.custom.itemDecoration.HeaderItemDecoration
import com.agelousis.monthlyfees.custom.itemTouchHelper.SwipeItemTouchHelper
import com.agelousis.monthlyfees.databinding.FragmentFilesLayoutBinding
import com.agelousis.monthlyfees.main.MainActivity
import com.agelousis.monthlyfees.main.enumerations.SwipeItemType
import com.agelousis.monthlyfees.main.ui.files.adapters.FilesAdapter
import com.agelousis.monthlyfees.main.ui.files.models.FileDataModel
import com.agelousis.monthlyfees.main.ui.files.models.HeaderModel
import com.agelousis.monthlyfees.main.ui.files.presenter.FilePresenter
import com.agelousis.monthlyfees.main.ui.files.viewHolders.FileViewHolder
import com.agelousis.monthlyfees.main.ui.files.viewModel.FilesViewModel
import com.agelousis.monthlyfees.main.ui.payments.models.EmptyModel
import com.agelousis.monthlyfees.utils.extensions.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_files_layout.*
import kotlinx.android.synthetic.main.fragment_files_layout.searchLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class FilesFragment: Fragment(), FilePresenter {

    override fun onFileSelected(fileDataModel: FileDataModel) {
        context?.openPDF(
            pdfFile = File(
                context?.filesDir ?: return, fileDataModel.fileName ?: return
            )
        )
    }

    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val viewModel by lazy { ViewModelProvider(this).get(FilesViewModel::class.java) }
    private val fileList by lazy { arrayListOf<FileDataModel>() }
    private val filteredList by lazy { arrayListOf<Any>() }
    private var searchViewState: Boolean = false
        set(value) {
            field  = value
            searchLayout.visibility = if (value) View.VISIBLE else View.GONE
        }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        configureObservers()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentFilesLayoutBinding.inflate(
            inflater,
            container,
            false
        ).also {
            it.userModel = (activity as? MainActivity)?.userModel
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureSearchView()
        configureRecyclerView()
        initializeFiles()
    }

    private fun configureSearchView() {
        searchLayout.onProfileImageClicked {
            (activity as? MainActivity)?.drawerLayout?.openDrawer(GravityCompat.START)
        }
        searchLayout.onQueryListener {
            configureFileList(
                files = fileList,
                query = it
            )
        }
    }

    private fun configureRecyclerView() {
        filesListRecyclerView.adapter = FilesAdapter(
            list = filteredList,
            presenter = this
        )
        filesListRecyclerView.addItemDecoration(
            HeaderItemDecoration(
                parent = filesListRecyclerView
            ) { position ->
                filteredList.getOrNull(
                    index = position
                ) is HeaderModel
            }
        )
        configureSwipeEvents()
    }

    private fun configureSwipeEvents() {
        val swipeItemTouchHelper = ItemTouchHelper(
            SwipeItemTouchHelper(
                context = context ?: return,
                swipeItemType = SwipeItemType.PDF_ITEM,
                swipePredicateBlock = {
                    it is FileViewHolder
                }
            ) innerBlock@ { swipeAction, position ->
                when(swipeAction) {
                    SwipeAction.RIGHT -> {
                        configureShareAction(
                            position = position
                        )
                        (filesListRecyclerView.adapter as? FilesAdapter)?.restoreItem(
                            position = position
                        )
                    }
                    SwipeAction.LEFT ->
                        configureDeleteAction(
                            position = position
                        )
                }
            }
        )
        swipeItemTouchHelper.attachToRecyclerView(filesListRecyclerView)
    }

    private fun configureShareAction(position: Int) {
        context?.sharePDF(
            pdfFile = File(
                context?.filesDir ?: return, (filteredList.getOrNull(index = position) as? FileDataModel)?.fileName ?: return
            )
        )
    }

    private fun configureDeleteAction(position: Int) {
        context?.showTwoButtonsDialog(
            title = resources.getString(R.string.key_warning_label),
            message = resources.getString(R.string.key_delete_file_message),
            negativeButtonBlock = {
                (filesListRecyclerView.adapter as? FilesAdapter)?.restoreItem(
                    position = position
                )
            },
            positiveButtonText = resources.getString(R.string.key_delete_label),
            positiveButtonBlock = {
                uiScope.launch {
                    viewModel.deleteFile(
                        context = context ?: return@launch,
                        fileDataModel = filteredList.getOrNull(index = position) as? FileDataModel ?: return@launch
                    )
                }
            }
        )
    }

    private fun configureObservers() {
        viewModel.filesLiveData.observe(viewLifecycleOwner) { files ->
            fileList.clear()
            fileList.addAll(
                files
            )
            after(
                millis = 1000
            ) {
                searchViewState = files.isNotEmpty()
            }
            configureFileList(
                files = files
            )
        }
        viewModel.fileDeletionLiveData.observe(viewLifecycleOwner) {
            if (it)
                initializeFiles()
        }
    }

    private fun configureFileList(files: List<FileDataModel>, query: String? = null) {
        filteredList.clear()
        files.groupBy { it.fileDate.yearMonth }.toSortedMap(compareByDescending { it }).forEach { map ->
            map.value.filter { it.description?.toLowerCase(Locale.getDefault())?.contains(query?.toLowerCase(Locale.getDefault()) ?: "") == true }
                .takeIf { it.isNotEmpty() }?.let inner@ { filteredByQueryList ->
                    val header = if (map.key?.isSameYearAndMonthWithCurrentDate == true) resources.getString(R.string.key_this_month_label) else map.key?.monthFormattedString
                    filteredList.add(
                        HeaderModel(
                            dateTime = map.value.firstOrNull()?.fileDate,
                            header = header ?: resources.getString(R.string.key_empty_field_label)
                        )
                    )
                    filteredList.addAll(
                        filteredByQueryList.sortedByDescending { it.fileDate }
                    )
                }
        }

        if (filteredList.isEmpty())
            query.whenNull {
                filteredList.add(
                    EmptyModel(
                        text = resources.getString(R.string.key_no_files_message),
                        imageIconResource = R.drawable.ic_empty
                    )
                )
            }?.let {
                filteredList.add(
                    EmptyModel(
                        text = String.format(
                            resources.getString(R.string.key_no_results_found_value),
                            it
                        ),
                        imageIconResource = R.drawable.ic_search
                    )
                )
            }
        filesListRecyclerView.scheduleLayoutAnimation()
        (filesListRecyclerView.adapter as? FilesAdapter)?.reloadData()
    }

    private fun initializeFiles() {
        uiScope.launch {
            viewModel.initializeFiles(
                context = context ?: return@launch,
                userId = (activity as? MainActivity)?.userModel?.id
            )
        }
    }

}