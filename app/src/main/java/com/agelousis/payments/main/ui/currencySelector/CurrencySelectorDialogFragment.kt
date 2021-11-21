package com.agelousis.payments.main.ui.currencySelector

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.agelousis.payments.R
import com.agelousis.payments.application.MainApplication
import com.agelousis.payments.databinding.CurrencySelectorFragmentLayoutBinding
import com.agelousis.payments.main.ui.currencySelector.adapters.CurrenciesAdapter
import com.agelousis.payments.main.ui.currencySelector.enumerations.CurrencyType
import com.agelousis.payments.main.ui.currencySelector.interfaces.CurrencySelectorFragmentPresenter
import com.agelousis.payments.ui.Typography
import com.agelousis.payments.ui.extraHorizontalMargin
import com.agelousis.payments.ui.horizontalMargin
import com.agelousis.payments.ui.textViewTitleFont
import com.agelousis.payments.utils.constants.Constants
import com.agelousis.payments.utils.extensions.currencySymbol
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class CurrencySelectorDialogFragment: DialogFragment(), CurrencyPresenter {

    companion object {
        fun show(supportFragmentManager: FragmentManager, currencySelectorFragmentPresenter: CurrencySelectorFragmentPresenter) {
            CurrencySelectorDialogFragment().also {
                it.currencySelectorFragmentPresenter = currencySelectorFragmentPresenter
            }.show(
                supportFragmentManager,
                Constants.CURRENCY_SELECTOR_FRAGMENT_TAG
            )
        }
    }

    override fun onCurrencySelected(currencyType: CurrencyType) {
        sharedPreferences?.currencySymbol = currencyType.symbol
        MainApplication.currencySymbol = currencyType.symbol
        currencySelectorFragmentPresenter?.onCurrencySelected(
            currencyType = currencyType
        )
        dismiss()
    }

    private lateinit var binding: CurrencySelectorFragmentLayoutBinding
    private var currencySelectorFragmentPresenter: CurrencySelectorFragmentPresenter? = null
    private val sharedPreferences by lazy { context?.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE) }
    private val currencyTypes by lazy { CurrencyType.values().toList() }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).also {
            it.window?.requestFeature(Window.FEATURE_NO_TITLE)
            it.window?.setBackgroundDrawableResource(android.R.color.transparent)
            it.window?.attributes?.windowAnimations = R.style.DialogAnimation
        }
    }

    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(
            context = context ?: return null
        ).apply {
            setContent {
                MaterialTheme(typography = Typography) {
                    CurrencySelectorLayout()
                }
            }
        }
    }

    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    @Composable
    fun CurrencySelectorLayout() {
        val cellState by remember { mutableStateOf(4) }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = colorResource(id = R.color.colorAccent),
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp
                        )
                    )
                    .height(
                        height = 80.dp
                    ).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(
                        id = R.string.key_select_currency_label
                    ),
                    style = textViewTitleFont,
                    color = colorResource(
                        id = R.color.white
                    )
                )
            }
            LazyVerticalGrid(
                contentPadding = PaddingValues(
                    all = 16.dp
                ),
                cells = GridCells.Fixed(cellState),
                modifier = Modifier.background(
                    color = colorResource(
                        id = R.color.nativeBackgroundColor
                    ),
                    shape = RoundedCornerShape(
                        bottomStart = 16.dp,
                        bottomEnd = 16.dp
                    )
                )
            ) {
               items(currencyTypes.size) { index ->
                   CurrencyType(
                       currencyType = currencyTypes[index]
                   )
               }
            }
        }
    }

    @ExperimentalMaterialApi
    @Composable
    fun CurrencyType(currencyType: CurrencyType) {
        Card(
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple(bounded = false),
            modifier = Modifier
                .size(
                    width = 70.dp,
                    height = 50.dp
                ).padding(
                    all = 0.dp
                ),
            shape = RoundedCornerShape(16.dp),
            onClick = {
                onCurrencySelected(
                    currencyType = currencyType
                )
            },
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = currencyType.icon),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
                Text(
                    text = currencyType.symbol,
                    style = textViewTitleFont,
                    color = colorResource(
                        id = R.color.white
                    ),
                    modifier = Modifier.shadow(
                        elevation = 8.dp
                    )
                )
            }
        }
    }

    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    @Preview
    @Composable
    fun ProfilePictureFragmentComposablePreview() {
        CurrencySelectorLayout()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //configureRecyclerView()
    }

    private fun configureRecyclerView() {
        binding.currencyRecyclerView.layoutManager = FlexboxLayoutManager(context, FlexDirection.ROW).also {
            it.flexDirection = FlexDirection.ROW
            it.justifyContent = JustifyContent.CENTER
            it.alignItems = AlignItems.CENTER
        }
        binding.currencyRecyclerView.adapter = CurrenciesAdapter(
            currencyTypes = currencyTypes,
            presenter = this
        )
    }

}