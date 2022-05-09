package com.agelousis.payments.compose.composableViews

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.agelousis.payments.compose.ColorAccent
import com.agelousis.payments.compose.textViewTitleLabelFont

typealias NavigationIconBlock = () -> Unit

@Composable
fun TopAppBarLayout(
    modifier: Modifier = Modifier,
    title: String,
    elevation: Dp? = null,
    navigationIcon: ImageVector? = null,
    navigationIconBlock: NavigationIconBlock = {},
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        style = textViewTitleLabelFont
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = navigationIconBlock
                    ) {
                        Icon(
                            imageVector = navigationIcon ?: Icons.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = actions,
                backgroundColor = MaterialTheme.colorScheme.surface,
                contentColor = ColorAccent,
                elevation = elevation ?: 10.dp
            )
        },
        modifier = modifier,
        content = content
    )
}