package com.yong.blog.data.api.manager

import com.yong.blog.data.api.dto.request.PostDataRequest
import com.yong.blog.data.api.dto.request.PostImageRequest
import com.yong.blog.data.api.dto.request.PostListRequest
import com.yong.blog.data.api.dto.response.toDomain
import com.yong.blog.data.api.service.ApiService
import com.yong.blog.domain.model.PostData
import com.yong.blog.domain.model.PostImage
import com.yong.blog.domain.model.PostList

class ApiManagerImpl(private val api: ApiService): ApiManager {
    override suspend fun getPostData(
        postType: String,
        postID: String
    ): PostData? {
        return try {
            val requestDto = PostDataRequest(postID, postType)
            val responseDto = api.getPostData(requestDto)

            if(responseDto.resultCode != 200
                || responseDto.resultData == null) {
                null
            } else {
                responseDto.resultData.toDomain(postID)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun getPostImage(
        postType: String,
        postID: String,
        srcID: String
    ): PostImage? {
        return try {
            val requestDto = PostImageRequest(postID, postType, srcID)
            val responseDto = api.getPostImage(requestDto)

            if(responseDto.resultCode != 200
                || responseDto.resultData == null) {
                null
            } else {
                responseDto.resultData.toDomain()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun getPostList(postType: String): PostList? {
        return try {
            val requestDto = PostListRequest(postType)
            val responseDto = api.getPostList(requestDto)

            if(responseDto.resultCode != 200
                || responseDto.resultData == null) {
                null
            } else {
                responseDto.resultData.toDomain()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}