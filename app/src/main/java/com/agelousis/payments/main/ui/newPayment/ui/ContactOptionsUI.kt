package com.agelousis.payments.main.ui.newPayment.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.agelousis.payments.main.ui.newPayment.enumerations.ContactType

typealias ContactTypeSelectionBlock = (contactType: ContactType) -> Unit

@Composable
fun ContactOptionsLayout(
    contactTypeList: List<ContactType>,
    contactTypeSelectionBlock: ContactTypeSelectionBlock
) {
    LazyRow(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 16.dp
            )
    ) {
        items(
            items = contactTypeList
        ) { contactType ->
            Box(
                modifier = Modifier
                    .padding(
                        top = 0.dp,
                        bottom = 16.dp
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(
                            bounded = true,
                            radius = 25.dp
                        ),
                        enabled = contactType.isEnabled,
                    ) {
                        contactTypeSelectionBlock(contactType)
                    }
            ) {
                Image(
                    painter = painterResource(
                        id = contactType.icon
                    ),
                    alpha = if (contactType.isEnabled) 1.0f else 0.2f,
                    contentDescription = null,
                    modifier = Modifier.size(
                        size = 40.dp
                    )
                )
            }
        }
    }
}