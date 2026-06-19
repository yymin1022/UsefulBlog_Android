package com.yong.blog.list.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.yong.blog.R
import com.yong.blog.common.ui.BlogAppBar
import com.yong.blog.common.ui.BlogLoadingIndicator
import com.yong.blog.common.ui.BlogUiStatus
import com.yong.blog.common.ui.BlogErrorIndicator
import com.yong.blog.domain.model.PostList
import com.yong.blog.domain.model.PostListItem
import com.yong.blog.list.viewmodel.ListViewModel

@Composable
fun ListScreen(
    modifier: Modifier = Modifier,
    postType: String,
    navigateToDetail: (String, String) -> Unit,
    navigateToMain: () -> Unit,
    viewModel: ListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val uiStatus = uiState.uiStatus

    val appBarTitle = uiState.appBarTitle
    val postList = uiState.postList
    val thumbnailMap = uiState.postThumbnailMap

    val listScrollState = rememberSaveable(
        saver = LazyListState.Saver
    ) {
        LazyListState()
    }

    LaunchedEffect(postType) {
        viewModel.getAppBarTitle(postType)
        viewModel.getPostList(postType)
    }

    LaunchedEffect(Unit) {
        viewModel.logListEvent(postType)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            BlogAppBar(
                modifier = Modifier,
                titleText = String.format(stringResource(R.string.list_appbar_title), appBarTitle?.let { stringResource(it) } ?: ""),
                navigationIcon = {
                    IconButton(
                        onClick = navigateToMain
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.desc_back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        ListScreenBody(
            modifier = Modifier
                .padding(innerPadding),
            postType = postType,
            postList = postList,
            listScrollState = listScrollState,
            thumbnailMap = thumbnailMap,
            uiStatus = uiStatus,
            requestPostList = { viewModel.getPostList(postType) },
            navigateToDetail = navigateToDetail
        )
    }
}

@Composable
private fun ListScreenBody(
    modifier: Modifier = Modifier,
    postType: String,
    postList: PostList?,
    listScrollState: LazyListState,
    thumbnailMap: Map<String, Bitmap?>,
    uiStatus: BlogUiStatus,
    requestPostList: () -> Unit,
    navigateToDetail: (String, String) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 4.dp)
    ) {
        when(uiStatus) {
            BlogUiStatus.UI_STATUS_ERROR -> {
                BlogErrorIndicator(
                    modifier = Modifier,
                    onRetry = requestPostList
                )
            }

            BlogUiStatus.UI_STATUS_LOADING -> {
                BlogLoadingIndicator(
                    modifier = Modifier
                )
            }

            BlogUiStatus.UI_STATUS_NORMAL -> {
                PostList(
                    modifier = Modifier,
                    postType = postType,
                    postList = postList,
                    listScrollState = listScrollState,
                    thumbnailMap = thumbnailMap,
                    navigateToDetail = navigateToDetail
                )
            }
        }
    }
}

@Composable
private fun PostList(
    modifier: Modifier = Modifier,
    postType: String,
    postList: PostList?,
    listScrollState: LazyListState,
    thumbnailMap: Map<String, Bitmap?>,
    navigateToDetail: (String, String) -> Unit
) {
    if(postList != null) {
        LazyColumn(
            modifier = modifier,
            state = listScrollState,
        ) {
            items(
                count = postList.postCount,
                key = { idx -> postList.postList[idx].postID }
            ) { idx ->
                val post = postList.postList[idx]
                val postURL = post.postURL
                val postThumbnail = thumbnailMap[postURL]

                PostListItem(
                    modifier = Modifier,
                    postType = postType,
                    postData = post,
                    postThumbnail = postThumbnail,
                    onClick = navigateToDetail,
                )
            }
        }
    } else {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text("Nothing")
        }
    }
}

@Composable
private fun PostListItem(
    modifier: Modifier = Modifier,
    postType: String,
    postData: PostListItem,
    postThumbnail: Bitmap?,
    onClick: (String, String) -> Unit,
) {
    val postDate = postData.postDate
    val postID = postData.postID
    val postTag = postData.postTag
    val postTitle = postData.postTitle

    Row(
        modifier = modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = { onClick(postType, postID) })
    ) {
        PostListItemImage(
            modifier = Modifier,
            postThumbnail = postThumbnail
        )
        PostListItemText(
            modifier = Modifier,
            postDate = postDate,
            postTag = postTag,
            postTitle = postTitle
        )
    }
}

@Composable
private fun PostListItemImage(
    modifier: Modifier = Modifier,
    postThumbnail: Bitmap?,
) {
    Box(
        modifier = modifier
            .height(96.dp)
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        if(postThumbnail != null) {
            Image(
                modifier = Modifier
                    .fillMaxSize(),
                bitmap = postThumbnail.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        } else {
            BlogLoadingIndicator(modifier = Modifier)
        }
    }
}

@Composable
private fun PostListItemText(
    modifier: Modifier = Modifier,
    postDate: String,
    postTag: List<String>,
    postTitle: String
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        PostListItemTextTitle(
            modifier = Modifier,
            postTitle = postTitle
        )
        PostListItemTextDate(
            modifier = Modifier,
            postDate = postDate
        )
        PostListItemTextTag(
            modifier = Modifier,
            postTag = postTag
        )
    }
}

@Composable
private fun PostListItemTextDate(
    modifier: Modifier = Modifier,
    postDate: String
) {
    Text(
        modifier = modifier
            .padding(horizontal = 4.dp, vertical = 2.dp),
        text = postDate,
        color = MaterialTheme.colorScheme.onSecondary,
        fontSize = 12.sp,
    )
}

@Composable
private fun PostListItemTextTag(
    modifier: Modifier = Modifier,
    postTag: List<String>
) {
    Text(
        modifier = modifier
            .padding(horizontal = 4.dp, vertical = 2.dp),
        text = postTag.joinToString(separator = " ") { "#$it" },
        color = MaterialTheme.colorScheme.onSecondary,
        fontSize = 10.sp,
    )
}

@Composable
private fun PostListItemTextTitle(
    modifier: Modifier = Modifier,
    postTitle: String
) {
    Text(
        modifier = modifier
            .padding(horizontal = 4.dp, vertical = 4.dp),
        text = postTitle,
        color = MaterialTheme.colorScheme.primary,
        fontSize = 16.sp,
    )
}