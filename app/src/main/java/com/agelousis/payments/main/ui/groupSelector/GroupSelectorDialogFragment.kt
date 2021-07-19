package com.agelousis.payments.main.ui.groupSelector

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.agelousis.payments.R
import com.agelousis.payments.databinding.CountrySelectorFragmentLayoutBinding
import com.agelousis.payments.main.ui.groupSelector.adapters.GroupsSelectionAdapter
import com.agelousis.payments.main.ui.groupSelector.interfaces.GroupSelectorFragmentPresenter
import com.agelousis.payments.main.ui.payments.models.GroupModel
import com.agelousis.payments.utils.constants.Constants

class GroupSelectorDialogFragment: DialogFragment(), GroupSelectorFragmentPresenter {

    companion object {
        fun show(
            supportFragmentManager: FragmentManager,
            groupModelList: List<GroupModel>,
            groupSelectorFragmentPresenter: GroupSelectorFragmentPresenter,
        ) {
            GroupSelectorDialogFragment().also {
                it.groupModelList = groupModelList
                it.groupSelectorFragmentPresenter = groupSelectorFragmentPresenter
            }.show(
                supportFragmentManager,
                Constants.GROUP_SELECTOR_FRAGMENT_TAG
            )
        }
    }

    override fun onGroupSelected(groupModel: GroupModel) {
        groupSelectorFragmentPresenter?.onGroupSelected(
            groupModel = groupModel
        )
        dismiss()
    }

    private lateinit var binding: CountrySelectorFragmentLayoutBinding
    private var groupModelList: List<GroupModel>? = null
    private val filteredGroups = arrayListOf<GroupModel>()
    private var groupSelectorFragmentPresenter: GroupSelectorFragmentPresenter? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).also {
            it.window?.requestFeature(Window.FEATURE_NO_TITLE)
            it.window?.setBackgroundDrawableResource(android.R.color.transparent)
            it.window?.attributes?.windowAnimations = R.style.DialogAnimation
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = CountrySelectorFragmentLayoutBinding.inflate(
            inflater,
            container,
            false
        )
        binding.headerTitle = resources.getString(R.string.key_select_group_label)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        configureRecyclerView()
    }

    private fun setupUI() {
        binding.searchLayout.searchHint = resources.getString(R.string.key_search_in_groups_label)
        binding.searchLayout.onQueryListener { query ->
            filterGroups(
                query = query
            )
        }
    }

    private fun filterGroups(query: String?) {
        filteredGroups.clear()
        filteredGroups.addAll(
            groupModelList?.filter { countryDataModel ->
                countryDataModel.groupName?.lowercase()?.contains(
                    other = query?.lowercase() ?: ""
                ) == true
            } ?: listOf()
        )
        (binding.countryRecyclerView.adapter as? GroupsSelectionAdapter)?.reloadData()
    }

    private fun configureRecyclerView() {
        filteredGroups.addAll(
            groupModelList ?: listOf()
        )
        binding.countryRecyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.countryRecyclerView.adapter = GroupsSelectionAdapter(
            groupModelList = filteredGroups,
            groupSelectorFragmentPresenter = this
        )
        binding.countryRecyclerView.post {
            binding.countryRecyclerView.scrollToPosition(
                filteredGroups.indexOfFirst {
                    it.isSelected
                }
            )
        }
    }

}