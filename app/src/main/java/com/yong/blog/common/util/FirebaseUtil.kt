package com.yong.blog.common.util

import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent

object FirebaseUtil {
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    fun init() {
        firebaseAnalytics = Firebase.analytics
    }

    fun logMainEvent() {
        firebaseAnalytics.logEvent("page_view") {
            param("page_path", "/")
            param("page_location", "app://blog/")
            param("page_title", "Main")
            param("source", "android")
        }
    }

    fun logEvent(postType: String, postID: String? = null) {
        if(postID == null) {
            logListEvent(postType)
        } else {
            logDetailEvent(postType, postID)
        }

        val pagePath = if (postID == null) "/$postType" else "/$postType/$postID"
        val pageTitle = if (postID == null) "Post List - $postType" else "Post Detail - $postID"

        firebaseAnalytics.logEvent("page_view") {
            param("page_path", pagePath)
            param("page_location", "app://blog$pagePath")
            param("page_title", pageTitle)
            param("source", "android")
        }
    }

    private fun logListEvent(postType: String) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM_LIST) {
            param(FirebaseAnalytics.Param.ITEM_CATEGORY, postType)
        }
    }

    private fun logDetailEvent(postType: String, postID: String) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(FirebaseAnalytics.Param.ITEM_CATEGORY, postType)
            param(FirebaseAnalytics.Param.ITEM_ID, postID)
        }

    }
}