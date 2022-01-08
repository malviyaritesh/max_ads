package com.owliverse.max_ads

import android.app.Activity
import android.content.Context
import androidx.annotation.NonNull
import com.applovin.sdk.AppLovinMediationProvider
import com.applovin.sdk.AppLovinSdk
import com.owliverse.max_ads.ads.InterstitialAdManager
import io.flutter.Log

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

class MaxAdsPlugin: FlutterPlugin, MethodCallHandler, ActivityAware {
  private lateinit var channel : MethodChannel
  private lateinit var activity: Activity
  private lateinit var interstitialAdManager: InterstitialAdManager

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "com.owliverse/max_ads")
    channel.setMethodCallHandler(this)
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    when (call.method) {
      "initSdk" -> {
        initSdk()
        result.success(true)
      }
      "createInterstitialAd" -> {
        interstitialAdManager.createAd(call.argument("adUnitId")!!)
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
      else -> result.notImplemented()
    }
  }

  private fun initSdk() {
    AppLovinSdk.getInstance(activity).mediationProvider = AppLovinMediationProvider.MAX
    AppLovinSdk.initializeSdk(activity) {
      channel.invokeMethod("sdkInitialized", emptyMap<String, String>())
    }
    interstitialAdManager = InterstitialAdManager(channel, activity)
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
