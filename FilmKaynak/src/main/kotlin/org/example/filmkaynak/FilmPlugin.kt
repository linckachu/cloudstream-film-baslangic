package org.example.filmkaynak

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin

@CloudstreamPlugin
class FilmPlugin : Plugin() {
    override fun load(context: android.content.Context) {
        registerMainAPI(FilmProvider())
    }
}
