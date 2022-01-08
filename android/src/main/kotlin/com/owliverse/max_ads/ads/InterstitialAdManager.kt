package com.owliverse.max_ads.ads

import android.app.Activity
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import io.flutter.plugin.common.MethodChannel

class InterstitialAdManager(
    private val channel: MethodChannel,
    private val activity: Activity
) : MaxAdListener {

    private val allAds = mutableMapOf<String, MaxInterstitialAd>()

    fun createAd(adUnitId: String) {
        allAds.putIfAbsent(adUnitId, MaxInterstitialAd(adUnitId, activity))
        allAds[adUnitId]?.setListener(this)
    }

    fun loadAd(adUnitId: String) {
        allAds[adUnitId]?.loadAd()
    }

    fun isReady(adUnitId: String): Boolean? {
        return allAds[adUnitId]?.isReady
    }

    fun showAd(adUnitId: String) {
        allAds[adUnitId]?.showAd()
    }

    fun disposeAd(adUnitId: String) {
        allAds[adUnitId]?.setListener(null)
        allAds[adUnitId]?.destroy()
        allAds.remove(adUnitId)
    }

    override fun onAdLoaded(ad: MaxAd?) {
        channel.invokeMethod("interstitialAdLoaded", mapOf("adUnitId" to ad?.adUnitId))
    }

    override fun onAdDisplayed(ad: MaxAd?) {
        channel.invokeMethod("interstitialAdDisplayed", mapOf("adUnitId" to ad?.adUnitId))
    }

    override fun onAdHidden(ad: MaxAd?) {
        channel.invokeMethod("interstitialAdHidden", mapOf("adUnitId" to ad?.adUnitId))
    }

    override fun onAdClicked(ad: MaxAd?) {
        channel.invokeMethod("interstitialAdClicked", mapOf("adUnitId" to ad?.adUnitId))
    }

    override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
        channel.invokeMethod("interstitialAdLoadFailed", mapOf("adUnitId" to adUnitId, "error" to error))
    }

    override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
        channel.invokeMethod("interstitialAdDisplayFailed", mapOf("adUnitId" to ad?.adUnitId, "error" to error))
    }
}