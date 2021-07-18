package com.agelousis.payments.base.interfaces

interface ViewContract<DB> {
    fun onBindData(binding: DB?)
}