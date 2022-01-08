package com.owliverse.max_ads_example

import android.os.Bundle
import android.os.PersistableBundle
import com.applovin.sdk.AppLovinSdk
import io.flutter.embedding.android.FlutterActivity

class MainActivity: FlutterActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        val appLovinSdk = AppLovinSdk.getInstance(this)
        appLovinSdk.mediationProvider = "max"
        appLovinSdk.initializeSdk()
    }
}
