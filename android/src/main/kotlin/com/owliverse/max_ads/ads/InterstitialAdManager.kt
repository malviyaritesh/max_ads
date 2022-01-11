package com.owliverse.max_ads.ads

import android.app.Activity
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.owliverse.max_ads.MaxAdsPlugin

class InterstitialAdManager : MaxAdListener {

    private val allAds = mutableMapOf<String, MaxInterstitialAd>()

    fun createAd(activity: Activity, adUnitId: String) {
        allAds[adUnitId] = MaxInterstitialAd(adUnitId, activity)
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
        MaxAdsPlugin.channel.invokeMethod("interstitialAdLoaded", mapOf("adUnitId" to ad?.adUnitId))
    }

    override fun onAdDisplayed(ad: MaxAd?) {
        MaxAdsPlugin.channel.invokeMethod(
            "interstitialAdDisplayed",
            mapOf("adUnitId" to ad?.adUnitId)
        )
    }

    override fun onAdHidden(ad: MaxAd?) {
        MaxAdsPlugin.channel.invokeMethod("interstitialAdHidden", mapOf("adUnitId" to ad?.adUnitId))
    }

    override fun onAdClicked(ad: MaxAd?) {
        MaxAdsPlugin.channel.invokeMethod(
            "interstitialAdClicked",
            mapOf("adUnitId" to ad?.adUnitId)
        )
    }

    override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
        MaxAdsPlugin.channel.invokeMethod(
            "interstitialAdLoadFailed",
            mapOf("adUnitId" to adUnitId, "error" to error)
        )
    }

    override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
        MaxAdsPlugin.channel.invokeMethod(
            "interstitialAdDisplayFailed",
            mapOf("adUnitId" to ad?.adUnitId, "error" to error)
        )
    }
}