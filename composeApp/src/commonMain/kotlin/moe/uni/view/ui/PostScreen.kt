package moe.uni.view.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import moe.uni.view.bean.YandPost
import moe.uni.view.viewmodel.PostViewModel
import moe.uni.view.ui.widget.SearchBar
import moe.uni.view.ui.widget.SettingItem
import moe.uni.view.ui.widget.SettingType

@OptIn(
    ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class
)
@Composable
fun PostListScreen(viewModel: PostViewModel = viewModel()) {
    val postList by viewModel.postData.collectAsState()
    val listState = rememberLazyStaggeredGridState()
    var lastVisibleIndex by remember {
        mutableIntStateOf(0)
    }
    val coroutineScope = rememberCoroutineScope()


    /**
     * 1 scroll up
     * -1 scroll down
     * 0 default
     */
    var scrollDirectionState by remember {
        mutableIntStateOf(0)
    }

    val scope = rememberCoroutineScope()
    var searchBarHeightSize by remember {
        mutableStateOf(0.dp)
    }
    val density = LocalDensity.current

    val searchBarPadding by remember {
        mutableStateOf(24.dp)
    }

    var showBottomSheet by remember { mutableStateOf(false) }

    val refreshing = viewModel.loading.collectAsState()

    val pullRefreshState = rememberPullRefreshState(refreshing = refreshing.value, onRefresh = {
        scope.launch(Dispatchers.IO) {
            viewModel.refresh()
        }
    })


    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }.map { layoutInfo ->
            if (listState.firstVisibleItemIndex > lastVisibleIndex) {
                // 向上滚动
                scrollDirectionState = -1
            } else if (listState.firstVisibleItemIndex < lastVisibleIndex) {
                // 向下滚动
                scrollDirectionState = 1
            }
            lastVisibleIndex = listState.firstVisibleItemIndex

            layoutInfo.visibleItemsInfo.lastOrNull()?.index == postList.lastIndex
        }.distinctUntilChanged().collect { reachedEnd ->
            //避免初次加载数据时，list未绘制导致被判断为到达底部
            if (reachedEnd && listState.firstVisibleItemIndex != 0) {
                viewModel.loadData()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalStaggeredGrid(
            state = listState,
            columns = StaggeredGridCells.Adaptive(200.dp),
            verticalItemSpacing = 4.dp,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            content = {
                //空置一行显示search bar
                item(key = 0, span = StaggeredGridItemSpan.FullLine) {
                    Box(
                        modifier = Modifier
                            .height(searchBarHeightSize + searchBarPadding)
                            .fillMaxWidth()
                    )
                }

                items(items = postList.distinctBy { it.postId }, key = {
                    it.postId//提供key以保持列表顺序稳定
                }) {
                    Row(Modifier.animateItemPlacement(tween(durationMillis = 250))) {
                        PostItem(it, clickable = {
//                            DetailActivity.start(context,it)
                        })
                    }

                }
            },
            modifier = Modifier
                .background(Color.Transparent)
                .pullRefresh(pullRefreshState)
                .padding(horizontal = 4.dp)

        )

        PullRefreshIndicator(
            refreshing.value, pullRefreshState,
            Modifier
                .align(Alignment.TopCenter)
                .padding(searchBarHeightSize + searchBarPadding)
        )

        AnimatedVisibility(
            visible = scrollDirectionState != -1,
            enter = slideInVertically(
                // 进入动画，从顶部滑下
                initialOffsetY = { -it },
                animationSpec = tween(durationMillis = 300)
            ),
            exit = slideOutVertically(
                // 退出动画，向上滑动消失
                targetOffsetY = { -it },
                animationSpec = tween(durationMillis = 300)
            )
        ) {

            SearchBar(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = searchBarPadding / 2)
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .onGloballyPositioned {
                        searchBarHeightSize = with(density) {
                            it.size.height.toDp()
                        }
                    },
                searchEvent = {
                    scope.launch(Dispatchers.IO) {
                        viewModel.search(it)
                        listState.animateScrollToItem(index = 0)
                    }
                }, menuButtonAction = {
                    showBottomSheet = true
                }
            )
        }

        if (showBottomSheet) {
            Setting(onDismissRequest = {showBottomSheet = false}, onSettingChanged = {
                viewModel.refresh{
                    coroutineScope.launch(Dispatchers.Main){
                        listState.animateScrollToItem(0)

                    }
                }
            })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Setting(onDismissRequest:() -> Unit,onSettingChanged:() -> Unit){
//    var safeModel by remember {
//        mutableStateOf(Config.getSafeMode())
//    }
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.height(400.dp)
    ) {
        SettingItem(SettingType.Switch(
            "Safe mode", false
        ) {
//            safeModel = it
//            Config.setSafeMode(it)
//            onSettingChanged.invoke()
        })
        MaterialTheme.shapes.large
    }
}

@Composable
fun PostItem(post: YandPost, clickable: (YandPost) -> Unit) {
    Card {
        SubcomposeAsyncImage(model = post.sampleUrl,
            contentScale = ContentScale.FillWidth,
            contentDescription = null,
            loading = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(50.dp)
                            .align(Alignment.Center)
                    )
                }
            },
            success = {
                SubcomposeAsyncImageContent()
            },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable { clickable.invoke(post) })
    }

}