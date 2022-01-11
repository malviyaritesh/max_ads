import 'dart:async';
import 'dart:ui';

import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';
import 'package:max_ads/ads/banner_ad.dart';
import 'package:max_ads/ads/intersititial_ad.dart';
import 'package:max_ads/ads/native_ad.dart';

class MaxAds {
  static const MethodChannel _channel = MethodChannel('com.owliverse/max_ads');
  static const Map<String, InterstitialAdEvent> _interstitialAdMethodToEvent = {
    'interstitialAdLoaded': InterstitialAdEvent.loaded,
    'interstitialAdDisplayed': InterstitialAdEvent.displayed,
    'interstitialAdHidden': InterstitialAdEvent.hidden,
    'interstitialAdClicked': InterstitialAdEvent.clicked,
    'interstitialAdLoadFailed': InterstitialAdEvent.loadFailed,
    'interstitialAdDisplayFailed': InterstitialAdEvent.displayFailed,
  };

  static bool sdkInitialized = false;
  static final Map<String, InterstitialAd> interstitialAds = {};
  static final Map<String, BannerAd> bannerAds = {};
  static final Map<String, NativeAd> nativeAds = {};

  static Future<void> initSdk() {
    if (sdkInitialized) {
      return Future.value();
    }
    _channel.setMethodCallHandler(_handleMethodCall);
    return _channel.invokeMethod('initSdk');
  }

  static void _initAllAds() {
    interstitialAds.forEach((adUnitId, ad) {
      ad.create();
      ad.load();
    });
    bannerAds.forEach((adUnitId, ad) {
      ad.create();
      ad.load();
    });
  }

  static Future<bool> invokeMethod(
      String method, Map<String, dynamic> args) async {
    final result = await _channel.invokeMethod(method, args);
    return result == true;
  }

  static Future<void> _handleMethodCall(MethodCall call) {
    final args = Map<String, dynamic>.from(call.arguments);
    if (call.method == 'sdkInitialized') {
      _initAllAds();
      sdkInitialized = true;
    } else if (call.method.startsWith('interstitialAd')) {
      final String adUnitId = args['adUnitId'];
      MaxAds.interstitialAds[adUnitId]
          ?.adEventListener(_interstitialAdMethodToEvent[call.method]!);
    }
    return Future.value(null);
  }
}
