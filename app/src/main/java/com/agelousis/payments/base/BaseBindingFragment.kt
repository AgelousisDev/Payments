package com.agelousis.payments.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.agelousis.payments.base.interfaces.ViewContract

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseBindingFragment<DB: ViewDataBinding>(
    private val inflate: Inflate<DB>
) : Fragment(), ViewContract<DB> {

    var binding: DB? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflate.invoke(inflater, container, false)
        onBindData(
            binding = binding
        )
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onBindData(binding: DB?) {

    }

}