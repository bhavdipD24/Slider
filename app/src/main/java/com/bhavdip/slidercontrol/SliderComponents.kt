package com.bhavdip.slidercontrol

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SliderView(onScrollEnd: (Int) -> Unit, onScroll: (Int) -> Unit) {

    var offsetX by remember {
        mutableStateOf(0f)
    }


    val lineColor = MaterialTheme.colorScheme.primary
    val circleColor = MaterialTheme.colorScheme.background

    var isPressed by remember { mutableStateOf(false) }

    var progress by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .padding(horizontal = 30.dp),
        contentAlignment = Alignment.Center
    ) {

        val offsetY = remember { Animatable(0f) }

        Card(modifier = Modifier
            .height(70.dp)
            .fillMaxWidth()
            .align(Alignment.Center)
            .onGloballyPositioned {
                offsetX = (it.size.toSize().width / 2)
            }
            .draggable(orientation = Orientation.Horizontal,
                state = rememberDraggableState(onDelta = {
                    offsetX += it
                }),
                onDragStarted = { isPressed = true },
                onDragStopped = {
                    isPressed = false
                    onScrollEnd(progress)
                }),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                offsetX = offsetX.coerceIn(0f, canvasWidth)
                progress = ((offsetX / canvasWidth) * 100).roundToInt()
                drawLine(
                    start = Offset(x = 0f, y = canvasHeight / 2),
                    end = Offset(x = canvasWidth, y = canvasHeight / 2),
                    color = lineColor,
                    strokeWidth = 60f,
                    cap = StrokeCap.Round  // make line end points round
                )
            }
        }

        if (isPressed) {
            onScroll(progress)
        }

        Box(
            modifier = Modifier
                .size(35.dp)
                .align(Alignment.CenterStart)
                .offset {
                    IntOffset(
                        offsetX.toInt(),
                        -offsetY.value.toInt()
                    )
                }
                .background(shape = CircleShape, color = lineColor)
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(5.dp)
                    .align(Alignment.Center)
                    .background(color = circleColor, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "" + progress, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        LaunchedEffect(isPressed) {
            scope.launch {
                offsetY.animateTo(
                    if (isPressed) 80f else 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
        }

    }
}