package com.agelousis.payments.guide.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.agelousis.payments.R
import com.agelousis.payments.guide.models.GuideModel
import com.agelousis.payments.guide.presenters.GuideActivityPresenter
import com.agelousis.payments.ui.*
import com.agelousis.payments.utils.extensions.parseBold
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState

@ExperimentalPagerApi
@Composable
fun GuideActivityLayout(
    guideModelList: List<GuideModel>,
    guideActivityPresenter: GuideActivityPresenter
) {
    ConstraintLayout(
        modifier = Modifier
            .background(
                color = colorResource(
                    id = R.color.whiteTwo
                )
            )
            .fillMaxSize()
    ) {
        val pagerState = rememberPagerState()
        val (
            pagerConstrainedReference,
            dotsIndicatorConstrainedReference,
            skipButtonConstrainedReference
        ) = createRefs()
        HorizontalPager(
            count = guideModelList.size,
            modifier = Modifier
                .constrainAs(pagerConstrainedReference) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }
                .fillMaxHeight(
                    fraction = 0.85f
                ),
            state = pagerState
        ) { page ->
            GuidePageLayout(
                guideModel = guideModelList[page]
            )
        }
        DotsIndicator(
            totalDots = guideModelList.size,
            selectedIndex = pagerState.currentPage,
            selectedColor = ColorAccent,
            unSelectedColor = colorResource(
                id = R.color.grey
            ),
            modifier = {
                constrainAs(dotsIndicatorConstrainedReference) {
                    start.linkTo(parent.start)
                    top.linkTo(pagerConstrainedReference.bottom)
                    end.linkTo(parent.end)
                }
            }
        )
        Button(
            onClick = {
                guideActivityPresenter.onSkip()
            },
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .padding(
                    all = 16.dp
                )
                .constrainAs(skipButtonConstrainedReference) {
                    top.linkTo(dotsIndicatorConstrainedReference.bottom)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                }
        ) {
            Text(
                text = stringResource(
                    id = R.string.key_skip_label
                ),
                style = textViewTitleLabelFont
            )
        }
    }
}

@Composable
fun GuidePageLayout(
    guideModel: GuideModel
) {
    when(LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE ->
            LandscapeGuidePage(
                guideModel = guideModel
            )
        else ->
            PortraitGuidePage(
                guideModel = guideModel
            )
    }
}

@Composable
fun PortraitGuidePage(
    guideModel: GuideModel
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple(bounded = false),
            shape = RoundedCornerShape(16.dp),
            elevation = 10.dp,
            onClick = {},
            modifier = Modifier
                .fillMaxSize(
                    fraction = 0.7f
                )
                .padding(
                    top = 16.dp
                )
        ) {
            Image(
                painter = painterResource(
                    id = guideModel.icon
                ),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
        Text(
            text = stringResource(
                id = guideModel.title
            ),
            style = textViewTitleLabelFont,
            color = colorResource(
                id = R.color.dayNightTextOnBackground
            ),
            modifier = Modifier
                .padding(
                    top = 16.dp
                ),
        )
        Text(
            text = buildAnnotatedString {
                guideModel.subtitleArray.forEach { subtitle ->
                    append("\u2022")
                    append("\t\t")
                    append(subtitle.parseBold())
                    append("\n")
                }
            },
            style = textViewValueLabelFont,
            color = colorResource(
                id = R.color.dayNightTextOnBackground
            ),
            modifier = horizontalMargin
        )
    }
}

@Composable
fun LandscapeGuidePage(
    guideModel: GuideModel
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple(bounded = false),
            shape = RoundedCornerShape(16.dp),
            elevation = 10.dp,
            onClick = {},
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(
                    fraction = 0.4f
                )
                .padding(
                    all = 16.dp
                )
        ) {
            Image(
                painter = painterResource(
                    id = guideModel.icon
                ),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(
                    id = guideModel.title
                ),
                style = textViewTitleLabelFont,
                color = colorResource(
                    id = R.color.dayNightTextOnBackground
                ),
                modifier = Modifier
                    .padding(
                        top = 16.dp
                    )
            )
            Text(
                text = buildAnnotatedString {
                    guideModel.subtitleArray.forEach { subtitle ->
                        append("\u2022")
                        append("\t\t")
                        append(subtitle.parseBold())
                        append("\n")
                    }
                },
                style = textViewValueLabelFont,
                color = colorResource(
                    id = R.color.dayNightTextOnBackground
                ),
                modifier = horizontalMargin
            )
        }
    }
}