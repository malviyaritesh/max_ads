import 'dart:async';
import 'dart:ui';

import 'package:flutter/services.dart';
import 'package:max_ads/ads/intersititial_ad.dart';

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
  static const Map<String, InterstitialAd> interstitialAds = {};

  static Future<void> initSdk() {
    if (sdkInitialized) {
      return Future.value();
    }
    _channel.setMethodCallHandler(_handleMethodCall);
    return _channel.invokeMethod('initSdk');
  }

  static void _initAllAds() {
    MaxAds.interstitialAds.forEach((adUnitId, ad) {
      ad.create();
      ad.load();
    });
  }

  static Future<dynamic> invokeMethod(
      String method, Map<String, dynamic> args) {
    return _channel.invokeMethod(method, args);
  }

  static Future _handleMethodCall(MethodCall call) {
    final Map<String, dynamic> args = call.arguments;
    if (call.method == 'sdkInitialized') {
      sdkInitialized = true;
      _initAllAds();
    } else if (call.method.startsWith('interstitialAd')) {
      final String adUnitId = args['adUnitId'];
      MaxAds.interstitialAds[adUnitId]
          ?.listener!(_interstitialAdMethodToEvent[call.method]!);
    }
    return Future.value();
  }
}
