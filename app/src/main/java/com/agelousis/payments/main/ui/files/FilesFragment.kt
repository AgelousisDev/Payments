package com.agelousis.payments.main.ui.files

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.R
import com.agelousis.payments.custom.itemDecoration.HeaderItemDecoration
import com.agelousis.payments.databinding.FragmentFilesLayoutBinding
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.ui.files.adapters.FilesAdapter
import com.agelousis.payments.main.ui.files.enumerations.FileRowState
import com.agelousis.payments.main.ui.files.models.FileDataModel
import com.agelousis.payments.main.ui.files.models.HeaderModel
import com.agelousis.payments.main.ui.files.presenter.FilePresenter
import com.agelousis.payments.main.ui.files.viewModel.FilesViewModel
import com.agelousis.payments.main.ui.payments.models.EmptyModel
import com.agelousis.payments.utils.extensions.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class FilesFragment: Fragment(), FilePresenter {

    override fun onFileSelected(fileDataModel: FileDataModel) {
        File(context?.filesDir ?: return, fileDataModel.fileName ?: return).takeIf {
            it.exists()
        }?.let {
            findNavController().navigate(
                FilesFragmentDirections.actionFilesFragmentToPdfViewerFragment(
                    fileDataModel = fileDataModel
                )
            )
        } ?: context?.showSimpleDialog(
            title = resources.getString(R.string.key_warning_label),
            message = resources.getString(R.string.key_file_not_exists_message)
        )
    }

    override fun onFileLongPressed(adapterPosition: Int) {
        (filteredList.getOrNull(
            index = adapterPosition
        ) as? FileDataModel)?.fileRowState = (filteredList.getOrNull(
            index = adapterPosition
        ) as? FileDataModel)?.fileRowState?.other ?: FileRowState.NORMAL

        when((filteredList.getOrNull(
            index = adapterPosition
        ) as? FileDataModel)?.fileRowState) {
            FileRowState.NORMAL ->
                selectedFilePositions.remove(
                    filteredList.getOrNull(
                        index = adapterPosition
                    ) as? FileDataModel
                )
            FileRowState.SELECTED ->
                selectedFilePositions.add(
                    filteredList.getOrNull(
                        index = adapterPosition
                    ) as? FileDataModel
                )
        }
        configureAppBar()

        (binding?.filesListRecyclerView?.adapter as? FilesAdapter)?.reloadData()
    }

    private var binding: FragmentFilesLayoutBinding? = null
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val viewModel by lazy { ViewModelProvider(this).get(FilesViewModel::class.java) }
    private val fileList by lazy { arrayListOf<FileDataModel>() }
    private val filteredList by lazy { arrayListOf<Any>() }
    private var searchViewState: Boolean = false
        set(value) {
            field  = value
            binding?.searchLayout?.visibility = if (value) View.VISIBLE else View.GONE
        }
    private val selectedFilePositions by lazy { arrayListOf<FileDataModel?>() }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        configureObservers()
        selectedFilePositions.clear()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentFilesLayoutBinding.inflate(
            inflater,
            container,
            false
        ).also {
            it.userModel = (activity as? MainActivity)?.userModel
        }
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureSearchView()
        configureRecyclerView()
        initializeFiles()
    }

    private fun configureSearchView() {
        binding?.searchLayout?.onProfileImageClicked {
            (activity as? MainActivity)?.binding?.drawerLayout?.openDrawer(GravityCompat.START)
        }
        binding?.searchLayout?.onQueryListener {
            configureFileList(
                files = fileList,
                query = it
            )
        }
    }

    private fun configureRecyclerView() {
        binding?.filesListRecyclerView?.addOnItemTouchListener(object: RecyclerView.OnItemTouchListener {
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                if (e.action != MotionEvent.ACTION_UP)
                    return false
                val child = rv.findChildViewUnder(e.x, e.y)
                return if (child != null)
                    false
                else {
                    deselectFiles()
                    true
                }
            }
        })
        binding?.filesListRecyclerView?.layoutManager = GridLayoutManager(
            context ?: return,
            if (resources.isLandscape) 4 else 2
        ).also {
            it.spanSizeLookup = object: GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int) =
                    when(filteredList.getOrNull(index = position)) {
                        is HeaderModel -> if (resources.isLandscape) 4 else 2
                        is FileDataModel -> 1
                        else -> 1
                    }
            }
        }
        binding?.filesListRecyclerView?.adapter = FilesAdapter(
            list = filteredList,
            presenter = this
        )
        binding?.filesListRecyclerView?.addItemDecoration(
            HeaderItemDecoration(
                parent = binding?.filesListRecyclerView ?: return
            ) { position ->
                filteredList.getOrNull(
                    index = position
                ) is HeaderModel
            }
        )
    }

    fun configureDeleteAction() {
        context?.showTwoButtonsDialog(
            title = resources.getString(R.string.key_warning_label),
            message = resources.getString(R.string.key_delete_selected_files_message),
            positiveButtonText = resources.getString(R.string.key_delete_label),
            positiveButtonBlock = {
                uiScope.launch {
                    viewModel.deleteFiles(
                        context = context ?: return@launch,
                        fileDataModelList = selectedFilePositions
                    )
                }
            }
        )
    }

    private fun configureObservers() {
        viewModel.filesLiveData.observe(viewLifecycleOwner) { files ->
            initializeActualFiles(
                files = files
            )
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
            selectedFilePositions.clear()
            configureAppBar()
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
                            header = header ?: resources.getString(R.string.key_empty_field_label),
                            headerBackgroundColor = context?.let { ContextCompat.getColor(it, android.R.color.transparent) }
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
                        title = resources.getString(R.string.key_no_files_title_message),
                        message = resources.getString(R.string.key_no_files_message),
                        imageIconResource = R.drawable.ic_colored_pdf
                    )
                )
            }?.let {
                filteredList.add(
                    EmptyModel(
                        message = String.format(
                            resources.getString(R.string.key_no_results_found_value),
                            it
                        ),
                        imageIconResource = R.drawable.ic_colored_search
                    )
                )
            }
        binding?.filesListRecyclerView?.scheduleLayoutAnimation()
        (binding?.filesListRecyclerView?.adapter as? FilesAdapter)?.reloadData()
    }

    private fun initializeFiles() {
        uiScope.launch {
            viewModel.initializeFiles(
                context = context ?: return@launch,
                userId = (activity as? MainActivity)?.userModel?.id
            )
        }
    }

    private fun initializeActualFiles(files: List<FileDataModel>) {
        viewModel.createFilesIfRequired(
            context = context ?: return,
            files = files
        )
    }

    private fun deselectFiles() {
        selectedFilePositions.clear()
        filteredList.forEachIfEach(
            predicate = {
                it is FileDataModel
            }
        ) {
            (it as? FileDataModel)?.fileRowState = FileRowState.NORMAL
        }
        (binding?.filesListRecyclerView?.adapter as? FilesAdapter)?.reloadData()
        configureAppBar()
    }

    private fun configureAppBar() {
        (activity as? MainActivity)?.floatingButtonState = selectedFilePositions.isNotEmpty()
        if (selectedFilePositions.isNotEmpty())
            (activity as? MainActivity)?.appBarTitle = String.format(
                resources.getString(R.string.key_files_selected_value_label),
                selectedFilePositions.size
            )
        else
            (activity as? MainActivity)?.appBarTitle = resources.getString(R.string.key_files_label)
    }

}