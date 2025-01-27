package moe.uni.view

import android.app.Application
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

class Application:Application(){
    override fun onCreate() {
        super.onCreate()
        Napier.base(DebugAntilog())
    }


}