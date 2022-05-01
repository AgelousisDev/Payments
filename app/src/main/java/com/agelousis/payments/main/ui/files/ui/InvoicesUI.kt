package com.agelousis.payments.main.ui.files.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.agelousis.payments.R
import com.agelousis.payments.main.MainActivity
import com.agelousis.payments.ui.composableViews.MaterialSearchViewLayout
import com.agelousis.payments.views.searchLayout.models.MaterialSearchViewDataModel
import com.agelousis.payments.views.searchLayout.presenter.MaterialSearchViewPresenter

@Composable
fun InvoicesLayout(
    materialSearchViewPresenter: MaterialSearchViewPresenter
) {
    val context = LocalContext.current
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val (searchViewConstrainedReference) = createRefs()
        MaterialSearchViewLayout(
            modifier = Modifier
                .constrainAs(searchViewConstrainedReference) {
                    start.linkTo(parent.start, 16.dp)
                    top.linkTo(parent.top, 16.dp)
                    end.linkTo(parent.end, 16.dp)
                    width = Dimension.fillToConstraints
                },
            materialSearchViewDataModel = MaterialSearchViewDataModel(
                hint = stringResource(id = R.string.key_search_in_invoices_label),
                profileImagePath = (context as? MainActivity)?.userModel?.profileImage,
                presenter = materialSearchViewPresenter
            )
        ) {

        }
    }
}