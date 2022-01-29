package com.agelousis.payments.main.ui.groupModification.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.agelousis.payments.R

@Composable
fun GroupModificationLayout() {
    ConstraintLayout {
        val (imageConstrainedReference, groupCardInfoConstrainedReference) = createRefs()
        Image(
            painter = painterResource(
                R.drawable.ic_group
            ),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(
                    height = 150.dp
                )
                .constrainAs(imageConstrainedReference) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )
        Card(
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple(bounded = false),
            shape = RoundedCornerShape(12.dp),
            elevation = 10.dp,
            onClick = {

            },
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(groupCardInfoConstrainedReference) {
                    top.linkTo(imageConstrainedReference.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {

        }

    }
}

@Preview
@Composable
fun GroupModificationUIPreview() {
    GroupModificationLayout()
}