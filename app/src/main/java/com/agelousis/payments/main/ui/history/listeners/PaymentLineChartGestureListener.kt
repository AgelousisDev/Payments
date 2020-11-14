package com.agelousis.payments.main.ui.history.listeners

import android.view.MotionEvent
import com.agelousis.payments.utils.extensions.euroFormattedString
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener

abstract class PaymentLineChartGestureListener(private val chart: LineChart): OnChartGestureListener {

    override fun onChartDoubleTapped(me: MotionEvent?) {}
    override fun onChartFling(
        me1: MotionEvent?,
        me2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ) {}
    override fun onChartGestureEnd(
        me: MotionEvent?,
        lastPerformedGesture: ChartTouchListener.ChartGesture?
    ) {}
    override fun onChartGestureStart(
        me: MotionEvent?,
        lastPerformedGesture: ChartTouchListener.ChartGesture?
    ) {}
    override fun onChartLongPressed(me: MotionEvent?) {}
    override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {}
    override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {}

    override fun onChartSingleTapped(me: MotionEvent?) {
        val point = chart.getTransformer(YAxis.AxisDependency.LEFT).getValuesByTouchPoint(me?.x ?: return, me.y)
        onAmountSelected(
            amount = point.y.euroFormattedString ?: return
        )
    }

    abstract fun onAmountSelected(amount: String)
}