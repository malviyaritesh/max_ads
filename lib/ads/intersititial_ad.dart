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

  InterstitialAd({
    required this.adUnitId,
    this.listener,
  })  : assert(adUnitId.isNotEmpty),
        assert(!MaxAds.interstitialAds.containsKey(adUnitId)) {
    MaxAds.interstitialAds[adUnitId] = this;
    if (MaxAds.sdkInitialized) {
      create();
      load();
    }
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
    MaxAds.interstitialAds.remove(adUnitId);
    return MaxAds.invokeMethod('disposeInterstitialAd', {'adUnitId': adUnitId});
  }
}
