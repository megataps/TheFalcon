package com.megalabs.themovie

import android.app.Application
import com.megalabs.falcon.Falcon
import com.megalabs.falcon.FalconConfig
import com.megalabs.falcon.naming.Md5FileNameGenerator
import com.megalabs.themovie.service.DefaultServiceFactory
import com.megalabs.themovie.service.ServiceFactory

class DemoApplication: Application() {

    companion object {
        lateinit var INSTANCE: DemoApplication
            private set

        fun getServiceFactory() : ServiceFactory {
            return INSTANCE.mServiceFactory
        }
    }

    var mServiceFactory: ServiceFactory = DefaultServiceFactory()

    override fun onCreate() {
        super.onCreate()

        INSTANCE = this

        val falconConfig = FalconConfig(Md5FileNameGenerator(), false, 1000)
        Falcon.init(this, falconConfig)
    }
}