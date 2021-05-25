package com.agelousis.payments.main.ui.clientsSelector

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.agelousis.payments.R
import com.agelousis.payments.databinding.ClientsSelectorFragmentLayoutBinding
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.ui.clientsSelector.presenters.ClientSelectorPresenter
import com.agelousis.payments.main.ui.clientsSelector.presenters.ClientsSelectorFragmentPresenter
import com.agelousis.payments.main.ui.clientsSelector.viewModel.ClientsSelectorViewModel
import com.agelousis.payments.main.ui.payments.PaymentsFragment
import com.agelousis.payments.main.ui.payments.models.ClientModel
import com.agelousis.payments.main.ui.payments.models.GroupModel
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.currentNavigationFragment
import com.agelousis.payments.utils.extensions.loaderState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ClientsSelectorDialogFragment: DialogFragment(), ClientsSelectorFragmentPresenter, ClientSelectorPresenter {

    companion object {

        fun show(
            supportFragmentManager: FragmentManager,
            clientModelList: List<ClientModel>
        ) {
            ClientsSelectorDialogFragment().also {
                it.clientModelList = clientModelList
            }.show(
                supportFragmentManager,
                Constants.CLIENTS_SELECTOR_FRAGMENT_TAG
            )
        }

    }

    override fun onCancel() {
        dismiss()
    }

    override fun onImport() {
        initializeClientsInsertion()
    }

    override fun onClientSelected(adapterPosition: Int, isSelected: Boolean) {
        filteredClientModelList.getOrNull(
            index = adapterPosition
        )?.isSelected = isSelected
    }

    private lateinit var binding: ClientsSelectorFragmentLayoutBinding
    private val viewModel by viewModels<ClientsSelectorViewModel>()
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private var clientModelList: List<ClientModel>? = null
    private val filteredClientModelList by lazy {
        arrayListOf<ClientModel>()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).also {
            it.window?.requestFeature(Window.FEATURE_NO_TITLE)
            it.window?.setBackgroundDrawableResource(android.R.color.transparent)
            it.window?.attributes?.windowAnimations = R.style.DialogAnimation
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = ClientsSelectorFragmentLayoutBinding.inflate(
            inflater,
            container,
            false
        ).also {
            it.userModel = (activity as? MainActivity)?.userModel
            it.presenter = this
            it.selectorState = true
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        configureRecyclerView()
    }

    private fun setupUI() {
        binding.searchLayout.onQueryListener { query ->
            this filterClientsWith query
        }
        binding.materialCheckBox.setOnCheckedChangeListener { _, isChecked ->
            binding.selectorState = !isChecked
            this selectAllClients isChecked
        }
    }

    private infix fun selectAllClients(state: Boolean) {
        filteredClientModelList.forEach { clientModel ->
            clientModel.isSelected = state
        }
        (binding.clientsSelectorRecyclerView.adapter as? ClientsSelectorAdapter)?.reloadData()
    }

    private infix fun filterClientsWith(query: String?) {
        filteredClientModelList.clear()
        filteredClientModelList.addAll(
            clientModelList?.filter {
                it.fullName.lowercase().contains(query?.lowercase() ?: "")
            } ?: listOf()
        )
        (binding.clientsSelectorRecyclerView.adapter as? ClientsSelectorAdapter)?.reloadData()
    }

    private fun configureRecyclerView() {
        filteredClientModelList.addAll(
            clientModelList ?: listOf()
        )
        binding.clientsSelectorRecyclerView.adapter = ClientsSelectorAdapter(
            clientModelList = filteredClientModelList,
            clientSelectorPresenter = this
        )
    }

    private fun initializeClientsInsertion() {
        (activity as? MainActivity)?.loaderState = true
        uiScope.launch {
            withContext(Dispatchers.Default) {
                for (map in filteredClientModelList.filter { it.isSelected }.groupBy { it.groupName })
                    viewModel.insertGroup(
                        context = context ?: return@withContext,
                        userId = (activity as? MainActivity)?.userModel?.id,
                        groupModel = GroupModel(
                            groupName = map.key ?: return@withContext,
                            color = map.value.firstOrNull()?.groupColor
                        )
                    ) { groupId ->
                        map.value.forEach {
                            it.groupId = groupId?.toInt()
                        }
                        uiScope.launch innerLaunch@ {
                            viewModel.insertClients(
                                context = context ?: return@innerLaunch,
                                userId = (activity as? MainActivity)?.userModel?.id,
                                clientModelList = map.value
                            ) {

                            }
                        }
                    }
                withContext(Dispatchers.Main) {
                    (activity as? MainActivity)?.loaderState = false
                    ((activity as? MainActivity)?.supportFragmentManager?.currentNavigationFragment as? PaymentsFragment)?.initializePayments()
                    dismiss()
                }
            }
        }
    }

}