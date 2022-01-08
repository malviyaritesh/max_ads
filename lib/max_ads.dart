
import 'dart:async';
import 'dart:ui';

import 'package:flutter/services.dart';
import 'package:max_ads/ads/intersititial_ad.dart';

class MaxAds {

  static const MethodChannel _channel = MethodChannel('com.owliverse/max_ads');
  static bool sdkInitialized = false;
  
  static Future<void> initSdk() {
    if (sdkInitialized) {
      return Future.value();
    }
    _channel.setMethodCallHandler(_handleMethodCall);
    return _channel.invokeMethod('initSdk');
  }

  static Future<dynamic> invokeMethod(String method, Map<String, dynamic> args) {
    return _channel.invokeMethod(method, args);
  }

  static Future _handleMethodCall(MethodCall call) {
    if (call.method == 'sdkInitialized') {
      sdkInitialized = true;
      InterstitialAd.initAllAds();
    } else if (call.method.startsWith('interstitialAd')) {
      InterstitialAd.handleMethodCall(call);
    }
    return Future.value();
  }
}
