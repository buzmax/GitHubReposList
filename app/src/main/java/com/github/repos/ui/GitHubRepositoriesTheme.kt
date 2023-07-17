package com.github.repos.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun GitHubReposTheme(
    content: @Composable () -> Unit
) {
    val colors = lightColors(
        primary = Color.Blue,
        primaryVariant = Color.Blue,
        secondary = Color.Gray,
        onPrimary = Color.White
    )

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

private val Typography = Typography()

private val Shapes = Shapes()
