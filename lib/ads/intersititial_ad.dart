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
  int loadRetryAttempt = 0;
  bool displayFailed = false;

  InterstitialAd({
    required this.adUnitId,
    this.listener,
  })  : assert(adUnitId.isNotEmpty),
        assert(!MaxAds.interstitialAds.containsKey(adUnitId)) {
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

  void adEventListener(InterstitialAdEvent adEvent) {
    switch (adEvent) {
      case InterstitialAdEvent.loaded:
        loadRetryAttempt = 0;
        break;
      case InterstitialAdEvent.loadFailed:
        loadRetryAttempt++;
        if (loadRetryAttempt < 10) {
          Future.delayed(
            Duration(milliseconds: pow(2, min(5, loadRetryAttempt)).toInt()),
            load,
          );
        }
        break;
      case InterstitialAdEvent.displayFailed:
        displayFailed = true;
        load();
        break;
      case InterstitialAdEvent.hidden:
        displayFailed = false;
        load();
        break;
      default:
      // Do nothing.
    }
    listener?.call(adEvent);
  }
}
