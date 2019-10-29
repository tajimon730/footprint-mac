package com.example.footprint

import android.app.Application
import io.realm.Realm

class Myapplication: Application(){

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }
}