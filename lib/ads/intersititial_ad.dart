import 'package:flutter/services.dart';
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
  static const Map<String, InterstitialAdEvent> _methodToEvent = {
    'interstitialAdLoaded': InterstitialAdEvent.loaded,
    'interstitialAdDisplayed': InterstitialAdEvent.displayed,
    'interstitialAdHidden': InterstitialAdEvent.hidden,
    'interstitialAdClicked': InterstitialAdEvent.clicked,
    'interstitialAdLoadFailed': InterstitialAdEvent.loadFailed,
    'interstitialAdDisplayFailed': InterstitialAdEvent.displayFailed,
  };

  static const Map<String, InterstitialAd> allAds = {};

  final String adUnitId;
  AdEventListener? listener;

  InterstitialAd({
    required this.adUnitId,
    this.listener,
  })  : assert(adUnitId.isNotEmpty),
        assert(!allAds.containsKey(adUnitId)) {
    allAds[adUnitId] = this;
    if (MaxAds.sdkInitialized) {
      create();
      load();
    }
  }

  static void initAllAds() {
    allAds.forEach((String adUnitId, InterstitialAd ad) {
      ad.create();
      ad.load();
    });
  }

  Future<void> create() {
    return MaxAds.invokeMethod('createInterstitialAd', {'adUnitId': adUnitId});
  }

  Future<void> load() {
    return MaxAds.invokeMethod('loadInterstitialAd', {'adUnitId': adUnitId});
  }

  Future<bool> isReady() {
    return MaxAds.invokeMethod('isReadyInterstitialAd', {'adUnitId': adUnitId})
          as Future<bool>;
  }

  Future<void> show() {
    return MaxAds.invokeMethod('showInterstitialAd', {'adUnitId': adUnitId});
  }

  Future<void> dispose() {
    listener = null;
    allAds.remove(adUnitId);
    return MaxAds.invokeMethod('disposeInterstitialAd', {'adUnitId': adUnitId});
  }

  static void handleMethodCall(MethodCall call) {
    final Map<String, dynamic> args = call.arguments;
    final String adUnitId = args['adUnitId'];
    allAds[adUnitId]?.listener!(_methodToEvent[call.method]!);
  }
}
