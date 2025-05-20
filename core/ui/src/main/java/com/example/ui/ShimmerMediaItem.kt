package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerMediaItem() {
    val brush = shimmerBrush()

    Column(
        modifier = Modifier
            .width(150.dp)
            .padding(16.dp)
    ) {
        // image placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(3f / 4f)
                .background(brush)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // title placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .background(brush)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .background(brush)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // score placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(16.dp)
                .background(brush)
        )
    }
}