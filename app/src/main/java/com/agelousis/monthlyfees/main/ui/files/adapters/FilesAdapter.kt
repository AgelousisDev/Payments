package com.agelousis.monthlyfees.main.ui.files.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agelousis.monthlyfees.R
import com.agelousis.monthlyfees.databinding.EmptyRowLayoutBinding
import com.agelousis.monthlyfees.databinding.FileRowLayoutBinding
import com.agelousis.monthlyfees.databinding.HeaderRowLayoutBinding
import com.agelousis.monthlyfees.main.ui.files.enumerations.FilesAdapterViewType
import com.agelousis.monthlyfees.main.ui.files.models.FileDataModel
import com.agelousis.monthlyfees.main.ui.files.models.HeaderModel
import com.agelousis.monthlyfees.main.ui.files.presenter.FilePresenter
import com.agelousis.monthlyfees.main.ui.files.viewHolders.FileViewHolder
import com.agelousis.monthlyfees.main.ui.files.viewHolders.HeaderViewHolder
import com.agelousis.monthlyfees.main.ui.payments.models.EmptyModel
import com.agelousis.monthlyfees.main.ui.payments.viewHolders.EmptyViewHolder

class FilesAdapter(private val list: ArrayList<Any>, private val presenter: FilePresenter): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when(viewType) {
            FilesAdapterViewType.EMPTY_VIEW.type ->
                EmptyViewHolder(
                    binding = EmptyRowLayoutBinding.inflate(
                        inflater,
                        parent,
                        false
                    )
                )
            FilesAdapterViewType.HEADER_VIEW.type ->
                HeaderViewHolder(
                    binding = HeaderRowLayoutBinding.inflate(
                        inflater,
                        parent,
                        false
                    )
                )
            FilesAdapterViewType.FILE_VIEW.type ->
                FileViewHolder(
                    binding = FileRowLayoutBinding.inflate(
                        inflater,
                        parent,
                        false
                    )
                )
            else ->
                EmptyViewHolder(
                    binding = EmptyRowLayoutBinding.inflate(
                        inflater,
                        parent,
                        false
                    )
                )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? EmptyViewHolder)?.bind(
            emptyModel = list.getOrNull(
                index = position
            ) as? EmptyModel ?: return
        )
        (holder as? HeaderViewHolder)?.bind(
            headerModel = list.getOrNull(
                index = position
            ) as? HeaderModel ?: return
        )
        (holder as? FileViewHolder)?.bind(
            fileDataModel = list.getOrNull(
                index = position
            ) as? FileDataModel ?: return,
            presenter = presenter
        )
    }

    override fun getItemCount() = list.size

    override fun getItemViewType(position: Int): Int {
        (list.getOrNull(index = position) as? EmptyModel)?.let { return FilesAdapterViewType.EMPTY_VIEW.type }
        (list.getOrNull(index = position) as? HeaderModel)?.let { return FilesAdapterViewType.HEADER_VIEW.type }
        (list.getOrNull(index = position) as? FileDataModel)?.let { return FilesAdapterViewType.FILE_VIEW.type }
        return super.getItemViewType(position)
    }

    fun reloadData() = notifyDataSetChanged()

    fun restoreItem(position: Int) = notifyItemChanged(position)

    fun removeItemAndUpdate(context: Context, position: Int): Boolean {
        list.removeAt(position)
        notifyItemRemoved(position)
        //notifyItemRangeChanged(position, list.size)

        val uselessHeaderRow = list.filterIsInstance<HeaderModel>().firstOrNull { headerModel ->
            list.filterIsInstance<FileDataModel>().all { headerModel.dateTime != it.fileDate }
        }
        uselessHeaderRow?.let {
            val headerPosition = list.indexOf(it)
            list.removeAt(headerPosition)
            notifyItemRemoved(headerPosition)
            //notifyItemRangeChanged(headerPosition, list.size)
        }
        addEmptyViewIf(
            emptyRow = EmptyModel(
                title = context.resources.getString(R.string.key_no_files_title_message),
                message = context.resources.getString(R.string.key_no_files_message),
                imageIconResource = R.drawable.ic_colored_pdf
            )
        ) {
            list.isEmpty()
        }
        return list.any { it is EmptyModel }
    }

    private fun addEmptyViewIf(emptyRow: EmptyModel, predicate: () -> Boolean) {
        if (predicate()) {
            list.add(emptyRow)
            notifyItemInserted(0)
            notifyItemRangeChanged(0, list.size)
        }
    }

}