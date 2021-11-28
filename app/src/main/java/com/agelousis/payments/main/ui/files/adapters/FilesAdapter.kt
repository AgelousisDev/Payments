package com.agelousis.payments.main.ui.files.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agelousis.payments.databinding.EmptyRowLayoutBinding
import com.agelousis.payments.databinding.FileRowLayoutBinding
import com.agelousis.payments.databinding.HeaderRowLayoutBinding
import com.agelousis.payments.main.ui.files.enumerations.FilesAdapterViewType
import com.agelousis.payments.main.ui.files.models.FileDataModel
import com.agelousis.payments.main.ui.files.models.HeaderModel
import com.agelousis.payments.main.ui.files.presenter.FilePresenter
import com.agelousis.payments.main.ui.files.viewHolders.FileViewHolder
import com.agelousis.payments.main.ui.files.viewHolders.HeaderViewHolder
import com.agelousis.payments.main.ui.payments.models.EmptyModel
import com.agelousis.payments.main.ui.payments.viewHolders.EmptyViewHolder

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

    @SuppressLint("NotifyDataSetChanged")
    fun reloadData() = notifyDataSetChanged()

}