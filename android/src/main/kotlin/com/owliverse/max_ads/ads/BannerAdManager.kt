package com.owliverse.max_ads.ads

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdFormat
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAdView
import com.applovin.sdk.AppLovinAdSize
import com.applovin.sdk.AppLovinSdkUtils
import com.owliverse.max_ads.MaxAdsPlugin
import com.owliverse.max_ads.R
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

class BannerAdManager: MaxAdViewAdListener {
    companion object {
        val viewTypeId = "com.owliverse/max_ads/banner_ad"
    }

    private val allAds = mutableMapOf<String, MaxAdView>()
    private lateinit var adView: MaxAdView

    fun createAd(activity: Activity, adUnitId: String, height: Int) {
        adView = MaxAdView(adUnitId, activity)
        // Stretch to width of the screen for banners to be fully functional
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        // Get the adaptive banner height
        val heightPx = AppLovinSdkUtils.dpToPx(activity, height)
        adView.layoutParams = FrameLayout.LayoutParams(width, heightPx)
        // Set background or background color for banners to be fully functional
        adView.setBackgroundColor(0xFFFFFF)

        adView.setListener(this)
        allAds[adUnitId] = adView
    }

    fun loadAd(adUnitId: String) {
        allAds[adUnitId]?.loadAd()
    }

    fun disposeAd(adUnitId: String) {
        adView.destroy()
        allAds[adUnitId]?.setListener(null)
        allAds[adUnitId]?.destroy()
        allAds.remove(adUnitId)
    }

    override fun onAdLoaded(ad: MaxAd?) {
        MaxAdsPlugin.channel.invokeMethod("bannerAdLoaded", mapOf("adUnitId" to ad?.adUnitId))
    }

    override fun onAdDisplayed(ad: MaxAd?) {
        MaxAdsPlugin.channel.invokeMethod("bannerAdDisplayed", mapOf("adUnitId" to ad?.adUnitId))
    }

    override fun onAdHidden(ad: MaxAd?) {}

    override fun onAdClicked(ad: MaxAd?) {}

    override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
        MaxAdsPlugin.channel.invokeMethod("bannerAdLoadFailed", mapOf("adUnitId" to adUnitId, "error" to error))
    }

    override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {}

    override fun onAdExpanded(ad: MaxAd?) {}

    override fun onAdCollapsed(ad: MaxAd?) {}

    inner class BannerAdView(context: Context, id: Int, creationParams: Map<*, *>?) :
        PlatformView {
        override fun getView(): View {
            return adView
        }

        override fun dispose() {}
    }
}

class BannerViewFactory: PlatformViewFactory(StandardMessageCodec.INSTANCE) {
    override fun create(context: Context, viewId: Int, args: Any?): PlatformView {
        val creationParams = args as Map<*, *>?
        return MaxAdsPlugin.bannerAdManager.BannerAdView(context, viewId, creationParams)
    }
}