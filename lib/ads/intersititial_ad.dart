import 'dart:math';

import 'package:max_ads/max_ads.dart';

enum InterstitialAdEvent {
  loaded,
  displayed,
  hidden,
  clicked,
  loadFailed,
  displayFailed,
}

typedef AdEventListener = void Function(InterstitialAdEvent);

class InterstitialAd {
  final String adUnitId;
  AdEventListener? listener;
  int retryAttempt = 0;

  InterstitialAd({
    required this.adUnitId,
    this.listener,
  })  : assert(adUnitId.isNotEmpty),
        assert(!MaxAds.interstitialAds.containsKey(adUnitId)) {
    listener ??= _defaultAdEventListener;
    if (MaxAds.sdkInitialized) {
      create();
      load();
    }
    MaxAds.interstitialAds[adUnitId] = this;
  }

  Future<bool> create() {
    return MaxAds.invokeMethod('createInterstitialAd', {'adUnitId': adUnitId});
  }

  Future<bool> load() {
    return MaxAds.invokeMethod('loadInterstitialAd', {'adUnitId': adUnitId});
  }

  Future<bool> isReady() async {
    return MaxAds.invokeMethod('isReadyInterstitialAd', {'adUnitId': adUnitId});
  }

  Future<bool> show() {
    return MaxAds.invokeMethod('showInterstitialAd', {'adUnitId': adUnitId});
  }

  Future<bool> dispose() {
    listener = null;
    MaxAds.interstitialAds.remove(adUnitId);
    return MaxAds.invokeMethod('disposeInterstitialAd', {'adUnitId': adUnitId});
  }

  void _defaultAdEventListener(InterstitialAdEvent adEvent) {
    switch (adEvent) {
      case InterstitialAdEvent.loaded:
        retryAttempt = 0;
        break;
      case InterstitialAdEvent.loadFailed:
        retryAttempt++;
        if (retryAttempt < 10) {
          Future.delayed(
            Duration(milliseconds: pow(2, min(5, retryAttempt)).toInt()),
            load,
          );
        }
        break;
      case InterstitialAdEvent.displayFailed:
        load();
        break;
      case InterstitialAdEvent.hidden:
        load();
        break;
      default:
        // Do nothing.
    }
  }
}
