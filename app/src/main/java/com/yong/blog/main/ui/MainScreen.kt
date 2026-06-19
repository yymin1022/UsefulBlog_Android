package com.yong.blog.main.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yong.blog.R
import com.yong.blog.common.util.FirebaseUtil

/**
 * Main UI of Useful Blog
 */
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    navigateToList: (String) -> Unit,
    navigateToDetail: (String, String) -> Unit,
) {
    // Effect for analytics
    LaunchedEffect(Unit) {
        FirebaseUtil.logMainEvent()
    }

    // Scaffold UI
    Scaffold(
        modifier = modifier,
    ) { innerPadding ->
        // Main UI
        MainScreenBody(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            navigateToList = navigateToList,
            navigateToDetail = navigateToDetail,
        )
    }
}

@Composable
private fun MainScreenBody(
    modifier: Modifier = Modifier,
    navigateToList: (String) -> Unit,
    navigateToDetail: (String, String) -> Unit,
) {
    Column(
        modifier = modifier
            .padding(horizontal = 20.dp, vertical = 40.dp),
    ) {
        // Main Title
        MainTitle(
            modifier = Modifier,
        )

        // Post button - blog
        PostListButton(
            modifier = Modifier,
            title = stringResource(R.string.post_type_blog),
            onClick = { navigateToList("blog") },
        )
        // Post button - project
        PostListButton(
            modifier = Modifier,
            title = stringResource(R.string.post_type_project),
            onClick = { navigateToList("project") },
        )
        // Post button - solving
        PostListButton(
            modifier = Modifier,
            title = stringResource(R.string.post_type_solving),
            onClick = { navigateToList("solving") },
        )
        // Post button - about
        PostListButton(
            modifier = Modifier,
            title = stringResource(R.string.post_type_about),
            onClick = { navigateToDetail("about", "Useful") },
        )
    }
}

/**
 * Main Title
 */
@Composable
private fun MainTitle(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(bottom = 20.dp),
    ) {
        MainTitleText(
            title = stringResource(R.string.main_title_1),
        )
        MainTitleText(
            title = stringResource(R.string.main_title_2),
            titleColor = MaterialTheme.colorScheme.primary,
        )
        MainTitleText(
            title = stringResource(R.string.main_title_3),
        )
    }
}

/**
 * Main Title Text
 */
@Composable
private fun MainTitleText(
    title: String,
    titleColor: Color? = null,
) {
    val textColor = titleColor
        ?: MaterialTheme.colorScheme.onBackground

    Text(
        text = title,
        color = textColor,
        fontSize = 35.sp,
    )
}

/**
 * Post List Button
 */
@Composable
private fun PostListButton(
    modifier: Modifier = Modifier,
    title: String,
    onClick: () -> Unit,
) {
    OutlinedButton(
        modifier = modifier,
        onClick = onClick,
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal,
        )
    }
}