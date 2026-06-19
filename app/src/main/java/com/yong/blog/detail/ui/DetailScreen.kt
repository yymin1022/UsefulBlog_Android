package com.yong.blog.detail.ui

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.yong.blog.R
import com.yong.blog.common.ui.BlogAppBar
import com.yong.blog.common.ui.BlogLoadingIndicator
import com.yong.blog.common.ui.BlogUiStatus
import com.yong.blog.common.ui.BlogErrorIndicator
import com.yong.blog.detail.ui.markdown.MarkdownContent
import com.yong.blog.detail.viewmodel.DetailViewModel
import com.yong.blog.detail.viewmodel.MarkdownElement
import com.yong.blog.domain.model.PostData

@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    postType: String,
    postID: String,
    navigateToList: () -> Unit,
    navigateToMain: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val uiStatus = uiState.uiStatus

    val appBarTitle = uiState.appBarTitle
    val postData = uiState.postData
    val postImageMap = uiState.postImageMap
    val postMarkdownContent = uiState.postMarkdownContent

    LaunchedEffect(postType, postID) {
        viewModel.getPostData(postType, postID)
    }

    LaunchedEffect(Unit) {
        viewModel.logDetailEvent(postType, postID)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            BlogAppBar(
                modifier = Modifier,
                titleText = stringResource(appBarTitle),
                navigationIcon = {
                    IconButton(
                        onClick = navigateToList
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.desc_back)
                        )
                    }
                },
                actionIcon = {
                    IconButton(
                        onClick = navigateToMain
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = stringResource(R.string.desc_home)
                        )
                    }
                },
            )
        }
    ) { innerPadding ->
        DetailScreenBody(
            modifier = Modifier
                .padding(innerPadding),
            postData = postData,
            postImageMap = postImageMap,
            postMarkdownContent = postMarkdownContent,
            uiStatus = uiStatus,
            requestPostData = { viewModel.getPostData(postType, postID) },
        )
    }
}

@Composable
private fun DetailScreenBody(
    modifier: Modifier = Modifier,
    postData: PostData?,
    postImageMap: Map<String, Bitmap?>,
    postMarkdownContent: List<MarkdownElement>,
    uiStatus: BlogUiStatus,
    requestPostData: () -> Unit,
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier,
    ) {
        when(uiStatus) {
            BlogUiStatus.UI_STATUS_ERROR -> {
                BlogErrorIndicator(
                    modifier = Modifier
                        .fillMaxSize(),
                    onRetry = requestPostData,
                )
            }

            BlogUiStatus.UI_STATUS_LOADING -> {
                BlogLoadingIndicator(
                    modifier = Modifier
                        .fillMaxSize(),
                )
            }

            BlogUiStatus.UI_STATUS_NORMAL -> {
                postData?.let { data ->
                    val postDate = data.postDate
                    val postTag = data.postTag
                    val postTitle = data.postTitle

                    Column(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                    ) {
                        PostTitle(
                            modifier = Modifier,
                            title = postTitle,
                        )
                        PostDate(
                            modifier = Modifier,
                            date = postDate,
                        )
                        PostContentDivider(
                            modifier = Modifier,
                        )
                        PostContent(
                            modifier = Modifier,
                            markdownContent = postMarkdownContent,
                            postImageMap = postImageMap,
                        )
                        PostContentDivider(
                            modifier = Modifier,
                        )
                        PostTag(
                            modifier = Modifier,
                            tagList = postTag,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PostContent(
    modifier: Modifier = Modifier,
    markdownContent: List<MarkdownElement>,
    postImageMap: Map<String, Bitmap?>,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
    ) {
        MarkdownContent(
            modifier = Modifier,
            markdownContent = markdownContent,
            postImageMap = postImageMap,
        )
    }
}

@Composable
private fun PostContentDivider(
    modifier: Modifier = Modifier,
) {
    HorizontalDivider(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
    )
}

@Composable
private fun PostDate(
    modifier: Modifier = Modifier,
    date: String,
) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = date,
            color = MaterialTheme.colorScheme.onSecondary,
            fontSize = 14.sp,
        )
    }
}

@Composable
private fun PostTag(
    modifier: Modifier = Modifier,
    tagList: List<String>,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        tagList.forEach { tag ->
            Text(
                modifier = Modifier.padding(horizontal = 2.dp),
                text = "#$tag",
                color = MaterialTheme.colorScheme.onSecondary,
                fontSize = 14.sp,
            )
        }
    }
}

@Composable
private fun PostTitle(
    modifier: Modifier = Modifier,
    title: String,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
        )
    }
}