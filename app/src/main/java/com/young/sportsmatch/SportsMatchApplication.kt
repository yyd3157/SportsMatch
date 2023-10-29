package com.young.sportsmatch

import android.app.Application
import com.young.sportsmatch.data.source.remote.ApiContainer

class SportsMatchApplication : Application() {

    companion object {
        val appContainer = ApiContainer()
    }

}