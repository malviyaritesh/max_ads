package com.owliverse.max_ads.ads

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.FrameLayout
import com.applovin.mediation.MaxAd
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.owliverse.max_ads.MaxAdsPlugin
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

class NativeAdManager : MaxNativeAdListener() {
    private val allAds = mutableMapOf<String, FrameLayout>()

    private lateinit var nativeAdContainer: FrameLayout
    private lateinit var nativeAdLoader: MaxNativeAdLoader
    private var nativeAd: MaxAd? = null

    fun createAd(activity: Activity, adUnitId: String) {
        nativeAdContainer = activity.findViewById(com.applovin.sdk.R.id.native_ad_view_container)
        nativeAdLoader = MaxNativeAdLoader(adUnitId, activity)
        nativeAdLoader.setNativeAdListener(this)
        nativeAdLoader.loadAd()
    }

    fun disposeAd(adUnitId: String) {

    }

    override fun onNativeAdLoaded(nativeAdView: MaxNativeAdView?, ad: MaxAd?) {
        // Clean up any pre-existing native ad to prevent memory leaks.
        nativeAd?.let { nativeAdLoader.destroy(it) }
        // Save ad for cleanup.
        nativeAd = ad
        // Add ad view to view.
        nativeAdContainer.removeAllViews()
        nativeAdContainer.addView(nativeAdView)
    }

    inner class NativeAdView(context: Context, id: Int, private val creationParams: Map<*, *>?): PlatformView {
        override fun getView(): View {
            return allAds[creationParams?.get("adUnitId")]!!
        }

        override fun dispose() {}
    }
}