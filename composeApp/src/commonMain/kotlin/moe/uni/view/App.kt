package moe.uni.view

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import moe.uni.view.ui.PostListScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {

    MaterialTheme {
        PostListScreen()
    }
}