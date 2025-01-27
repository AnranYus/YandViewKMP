package moe.uni.view

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "YandViewKMP",
    ) {
        App()
    }
}