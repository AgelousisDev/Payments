package com.agelousis.payments.base

import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts

typealias OnActivityResultBlock<Result> = (Result) -> Unit
class BaseActivityResult<Input, Result>(
    caller: ActivityResultCaller,
    contract: ActivityResultContract<Input, Result>,
    private var onActivityResultBlock: OnActivityResultBlock<Result>? = null
) {

    private var launcher: ActivityResultLauncher<Input>? = null

    init {
        launcher = caller.registerForActivityResult(contract, this::callOnActivityResult)
    }
    /**
     * Register activity result using a {@link ActivityResultContract} and an in-place activity result callback like
     * the default approach. You can still customise callback using {@link #launch(Object, OnActivityResult)}.
     */
    companion object {

        fun <Input, Result> registerForActivityResult(
            caller: ActivityResultCaller,
            contract: ActivityResultContract<Input, Result>,
            onActivityResultBlock: OnActivityResultBlock<Result>
        ) =  BaseActivityResult(
            caller = caller,
            contract = contract,
            onActivityResultBlock = onActivityResultBlock
        )

        fun <Input, Result> registerForActivityResult(
            caller: ActivityResultCaller,
            contract: ActivityResultContract<Input, Result>
        ) =  BaseActivityResult(caller = caller,
            contract = contract
        )

        fun <Input, Result> registerForActivityResult(
            caller: ActivityResultCaller
        ) =  BaseActivityResult(
            caller = caller,
            contract = ActivityResultContracts.StartActivityForResult()
        )

    }

    /**
     * Launch activity, same as {@link ActivityResultLauncher#launch(Object)} except that it allows a callback
     * executed after receiving a result from the target activity.
     */
    fun launch(input: Input, onActivityResultBlock: OnActivityResultBlock<Result>?) {
        this.onActivityResultBlock = onActivityResultBlock
        launcher?.launch(input)
    }

    /**
     * Same as {@link #launch(Object, OnActivityResult)} with last parameter set to {@code null}.
     */
    fun launch(input: Input) {
        launch(
            input = input,
            onActivityResultBlock = onActivityResultBlock
        )
    }

    private fun callOnActivityResult(result: Result) {
        onActivityResultBlock?.invoke(result)
    }
}