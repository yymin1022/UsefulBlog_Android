package com.yong.blog.detail.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yong.blog.R
import com.yong.blog.common.exception.PostException
import com.yong.blog.common.ui.BlogUiStatus
import com.yong.blog.common.util.FirebaseUtil
import com.yong.blog.domain.model.PostData
import com.yong.blog.domain.repository.PostDetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.io.encoding.Base64

sealed class MarkdownElement {
    data class Image(val srcID: String): MarkdownElement()
    data class Text(val markdownContent: String): MarkdownElement()
}

data class DetailUiState(
    val appBarTitle: Int = R.string.detail_appbar_title,
    val postData: PostData? = null,
    val postImageMap: Map<String, Bitmap?> = emptyMap(),
    val postMarkdownContent: List<MarkdownElement> = emptyList(),
    val uiStatus: BlogUiStatus = BlogUiStatus.UI_STATUS_NORMAL
)

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: PostDetailRepository
): ViewModel() {
    companion object {
        private const val BITMAP_DOWNSCALE_WIDTH = 320
        private const val LOG_TAG = "PostDetail ViewModel"
    }

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState = _uiState.asStateFlow()

    fun logDetailEvent(postType: String, postID: String) {
        FirebaseUtil.logEvent(postType, postID)
    }

    fun getPostData(postType: String, postID: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(uiStatus = BlogUiStatus.UI_STATUS_LOADING) }

            try {
                val postData = repository.getPostData(postType, postID)
                if(postData == null) throw PostException("PostData [$postType] got error")

                val postUrl = postData.postUrl
                val postMarkdownContent = parseMarkdown(postType, postUrl, postData.postContent)
                _uiState.update {
                    it.copy(
                        postData = postData,
                        postMarkdownContent = postMarkdownContent,
                        uiStatus = BlogUiStatus.UI_STATUS_NORMAL
                    )
                }
            } catch(e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(uiStatus = BlogUiStatus.UI_STATUS_ERROR) }
            }
        }
    }

    private fun parseMarkdown(postType: String, postUrl: String, markdownContent: String): List<MarkdownElement> {
        val imageRegex = Regex("""!\[(.*?)]\((.*?)\)|<img.*?src="(.*?)".*?>""")

        var prevIdx = 0
        val parseRes = mutableListOf<MarkdownElement>()
        imageRegex.findAll(markdownContent).forEach { matchRes ->
            val textContent = markdownContent.substring(prevIdx, matchRes.range.first)
            if(textContent.isNotEmpty()) {
                parseRes.add(MarkdownElement.Text(textContent))
            }

            val srcID = matchRes.groupValues[2].ifEmpty { matchRes.groupValues[3] }
            parseRes.add(MarkdownElement.Image(srcID))
            requestPostImage(postType, postUrl, srcID)

            prevIdx = matchRes.range.last + 1
        }

        if(prevIdx < markdownContent.length) {
            val textContent = markdownContent.substring(prevIdx)
            parseRes.add(MarkdownElement.Text(textContent))
        }

        return parseRes
    }

    private fun requestPostImage(postType: String, postID: String, srcID: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(postImageMap = it.postImageMap + (srcID to null)) }

            try {
                val postImage = repository.getPostImage(postType, postID, srcID)
                if(postImage == null) {
                    Log.e(LOG_TAG, "PostImage [$postID] got error")
                    return@launch
                }

                val postImageBitmap = postImage.base64Str.let { base64Str ->
                    try {
                        val imageBytes = Base64.decode(base64Str)

                        val bitmapOptions = BitmapFactory.Options()
                        bitmapOptions.inJustDecodeBounds = true
                        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, bitmapOptions)
                        bitmapOptions.inSampleSize = downscaleBitmap(bitmapOptions)
                        bitmapOptions.inJustDecodeBounds = false
                        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, bitmapOptions)
                    } catch (e: IllegalArgumentException) {
                        e.printStackTrace()
                        null
                    }
                }

                _uiState.update { it.copy(postImageMap = it.postImageMap + (srcID to postImageBitmap)) }
            } catch(e: Exception) {
                e.printStackTrace()
                Log.e(LOG_TAG, "PostImage [$postID] request failed: ${e.message}")
            }
        }
    }

    private fun downscaleBitmap(options: BitmapFactory.Options): Int {
        val origWidth: Int = options.outWidth
        var inSampleSize = 1

        if(origWidth > BITMAP_DOWNSCALE_WIDTH) {
            val halfWidth: Int = origWidth / 2
            while((halfWidth / inSampleSize) >= origWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }
}