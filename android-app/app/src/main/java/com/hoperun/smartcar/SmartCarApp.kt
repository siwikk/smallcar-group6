package com.hoperun.smartcar

import android.app.Application
import com.hoperun.smartcar.utils.PreferencesUtils

class SmartCarApp : Application() {
    override fun onCreate() {
        super.onCreate()
        PreferencesUtils.init(this)
    }
}
