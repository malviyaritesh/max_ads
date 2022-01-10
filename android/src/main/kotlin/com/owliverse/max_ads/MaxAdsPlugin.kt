package com.owliverse.max_ads

import android.app.Activity
import androidx.annotation.NonNull
import com.applovin.sdk.AppLovinAdSize
import com.applovin.sdk.AppLovinMediationProvider
import com.applovin.sdk.AppLovinSdk
import com.owliverse.max_ads.ads.BannerAdManager
import com.owliverse.max_ads.ads.BannerViewFactory
import com.owliverse.max_ads.ads.InterstitialAdManager

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

class MaxAdsPlugin: FlutterPlugin, MethodCallHandler, ActivityAware {

  companion object {
    val interstitialAdManager = InterstitialAdManager()
    val bannerAdManager = BannerAdManager()
    lateinit var channel : MethodChannel
  }

  lateinit var activity: Activity
  lateinit var pluginBinding: FlutterPlugin.FlutterPluginBinding

  override fun onAttachedToEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(binding.binaryMessenger, "com.owliverse/max_ads")
    channel.setMethodCallHandler(this)
    pluginBinding = binding
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    when (call.method) {
      "initSdk" -> {
        initSdk()
        result.success(true)
      }
      "createInterstitialAd" -> {
        interstitialAdManager.createAd(activity, call.argument("adUnitId")!!)
        result.success(true)
      }
      "loadInterstitialAd" -> {
        interstitialAdManager.loadAd(call.argument("adUnitId")!!)
        result.success(true)
      }
      "isReadyInterstitialAd" -> {
        result.success(interstitialAdManager.isReady(call.argument("adUnitId")!!))
      }
      "showInterstitialAd" -> {
        interstitialAdManager.showAd(call.argument("adUnitId")!!)
        result.success(true)
      }
      "disposeInterstitialAd" -> {
        interstitialAdManager.disposeAd(call.argument("adUnitId")!!)
        result.success(true)
      }
      "createBannerAd" -> {
        val height: Int = call.argument("height") ?: AppLovinAdSize.BANNER.height
        pluginBinding.platformViewRegistry.registerViewFactory(BannerAdManager.viewTypeId, BannerViewFactory())
        bannerAdManager.createAd(activity, call.argument("adUnitId")!!, height)
        result.success(true)
      }
      "loadBannerAd" -> {
        bannerAdManager.loadAd(call.argument("adUnitId")!!)
        result.success(true)
      }
        "disposeBannerAd" -> {
          bannerAdManager.disposeAd(call.argument("adUnitId")!!)
          result.success(true)
        }
      else -> result.notImplemented()
    }
  }

  private fun initSdk() {
    AppLovinSdk.getInstance(activity).mediationProvider = AppLovinMediationProvider.MAX
    AppLovinSdk.initializeSdk(activity) {
      channel.invokeMethod("sdkInitialized", emptyMap<String, String>())
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    activity = binding.activity
  }

  override fun onDetachedFromActivityForConfigChanges() {
    // Nothing to do
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    onAttachedToActivity(binding)
  }

  override fun onDetachedFromActivity() {
    // Nothing to do
  }
}
