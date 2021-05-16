package com.agelousis.payments.main.ui.clientsSelector

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.agelousis.payments.databinding.ClientsSelectorFragmentLayoutBinding
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.main.ui.clientsSelector.presenters.ClientsSelectorFragmentPresenter
import com.agelousis.payments.main.ui.payments.models.ClientModel
import com.agelousis.payments.utils.constants.Constants

class ClientsSelectorDialogFragment: DialogFragment(), ClientsSelectorFragmentPresenter {

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

    }

    private lateinit var binding: ClientsSelectorFragmentLayoutBinding
    private var clientModelList: List<ClientModel>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = ClientsSelectorFragmentLayoutBinding.inflate(
            inflater,
            container,
            false
        ).also {
            it.userModel = (activity as? MainActivity)?.userModel
            it.presenter = this
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureRecyclerView()
    }

    private fun configureRecyclerView() {

    }

}