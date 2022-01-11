import 'dart:io';

import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';
import 'package:max_ads/max_ads.dart';

class NativeAd extends StatefulWidget {
  NativeAd({
    Key? key,
    required this.adUnitId,
    this.height = 250,
  }) : super(key: key) {
    if (MaxAds.sdkInitialized) {
      create();
    }
    MaxAds.nativeAds[adUnitId] = this;
  }

  final String adUnitId;
  final int height;

  Future<bool> create() {
    return MaxAds.invokeMethod('createNativeAd', {
      'adUnitId': adUnitId,
    });
  }

  @override
  _NativeAdState createState() => _NativeAdState();
}

class _NativeAdState extends State<NativeAd> {
  @override
  Widget build(BuildContext context) {
    const viewTypeId = 'com.owliverse/max_ads/native_ad';
    final Map<String, dynamic> creationParams = <String, dynamic>{
      'adUnitId': widget.adUnitId,
    };
    final adView = AndroidView(
      viewType: viewTypeId,
      layoutDirection: TextDirection.ltr,
      creationParams: creationParams,
      creationParamsCodec: const StandardMessageCodec(),
    );
    return LimitedBox(
      maxHeight: widget.height.toDouble(),
      child: Platform.isAndroid ? adView : null,
    );
  }
}
