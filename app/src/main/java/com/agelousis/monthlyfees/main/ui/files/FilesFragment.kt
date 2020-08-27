package com.agelousis.monthlyfees.main.ui.files

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.agelousis.monthlyfees.databinding.FragmentFilesLayoutBinding
import com.agelousis.monthlyfees.main.MainActivity

class FilesFragment: Fragment() {

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
        configureRecyclerView()
    }

    private fun configureRecyclerView() {

    }

}