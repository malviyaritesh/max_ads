import 'dart:io';

import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';
import 'package:max_ads/max_ads.dart';

class BannerAd extends StatefulWidget {
  final String adUnitId;
  final double height;

  BannerAd({
    Key? key,
    required this.adUnitId,
    this.height = 50.0,
  }) : super(key: key) {
    if (MaxAds.sdkInitialized) {
      create();
      load();
    }
    MaxAds.bannerAds[adUnitId] = this;
  }

  Future<bool> create() {
    return MaxAds.invokeMethod('createBannerAd', {
      'adUnitId': adUnitId,
      'height': height.toInt(),
    });
  }

  Future<bool> load() {
    return MaxAds.invokeMethod('loadBannerAd', {'adUnitId': adUnitId});
  }

  Future<bool> dispose() {
    MaxAds.bannerAds.remove(adUnitId);
    return MaxAds.invokeMethod('disposeBannerAd', {'adUnitId': adUnitId});
  }

  @override
  _BannerAdState createState() => _BannerAdState();
}

class _BannerAdState extends State<BannerAd> {
  @override
  Widget build(BuildContext context) {
    const viewTypeId = 'com.owliverse/max_ads/banner_ad';
    final Map<String, dynamic> creationParams = <String, dynamic>{
      'id': widget.adUnitId,
      'height': widget.height.toInt(),
    };
    final adView = AndroidView(
      viewType: viewTypeId,
      layoutDirection: TextDirection.ltr,
      creationParams: creationParams,
      creationParamsCodec: const StandardMessageCodec(),
    );
    return LimitedBox(
      maxHeight: widget.height,
      child: Platform.isAndroid ? adView : null,
    );
  }
}
