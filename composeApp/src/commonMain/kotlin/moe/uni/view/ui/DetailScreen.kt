package moe.uni.view.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import coil3.compose.AsyncImage
import moe.uni.view.bean.YandPost


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    post: YandPost,
//    viewModel: DetailViewModel,
    onBackAction: () -> Unit,
//    onImageDownloaded: (Result) -> Unit
) {

    var scale by remember { mutableFloatStateOf(1f) }
    var rotation by remember { mutableFloatStateOf(0f) }
    val state = rememberTransformableState { zoomChange, _, rotationChange ->
        scale *= zoomChange
        rotation += rotationChange
    }
//    val context = LocalContext.current
    Box(
        modifier = Modifier
            .background(Color.Black)
            .fillMaxSize()
    ) {
        AsyncImage(
            model = post.sampleUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    rotationZ = rotation,
                )
                .transformable(state = state)
                .background(Color.Black)
        )

        Column {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    actionIconContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                title = {},
//                navigationIcon = {
//                    IconButton(onClick = onBackAction) {
//                        Icon(
//                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
//                            contentDescription = R.string.description_back.toString()
//                        )
//                    }
//                },
//                actions = {
//                    IconButton(onClick = {
//                        val defer = DownloadUtils.downloadImage(post.fileUrl)
//                        viewModel.viewModelScope.launch {
//                            val result = defer.await()
//                            onImageDownloaded.invoke(result)
//                        }
//                    }) {
//                        Icon(
//                            painter = painterResource(id = R.drawable.ic_twotone_arrow_downward_24),
//                            contentDescription = R.string.description_download.toString()
//                        )
//                    }
//                    IconButton(onClick = {
//                        share(post.sampleUrl, context)
//                    }) {
//                        Icon(
//                            imageVector = Icons.Default.Share,
//                            contentDescription = R.string.description_share.toString(),
//                            tint = Color.White
//                        )
//                    }
//                    IconButton(onClick = {
//                        setWallpaper(post.sampleUrl, context)
//                    }) {
//                        Icon(
//                            painter = painterResource(id = R.drawable.ic_twotone_wallpaper_24),
//                            contentDescription = R.string.description_use_to_wallpaper.toString(),
//                            tint = Color.White
//                        )
//                    }
//                },
            )
        }
    }

}