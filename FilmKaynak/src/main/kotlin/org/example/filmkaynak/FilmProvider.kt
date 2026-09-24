package org.example.filmkaynak

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.Qualities
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * Kasten yalnizca dogrudan ve izinli video URL'leri isler.
 * Anti-bot/DRM/captcha asma veya gomulu ucuncu taraf oynatici cozme uygulanmaz.
 * DOM secicileri tahminidir; hedef siteden 403 alindigi icin dogrulanmamistir.
 */
class FilmProvider : MainAPI() {
    override var mainUrl = "https://www.hdfilmcehennemi.nl"
    override var name = "FilmKaynak (beta)"
    override val lang = "tr"
    override val hasMainPage = true
    override val supportedTypes = setOf(TvType.Movie)

    // Bu rotalar ve seciciler hedef sitenin canli HTML'iyle dogrulanmalidir.
    override val mainPage = mainPageOf("/" to "Filmler")

    private fun abs(base: String, value: String?): String? {
        if (value.isNullOrBlank()) return null
        return runCatching { java.net.URI(base).resolve(value.trim()).toString() }.getOrNull()
    }
    private fun Element.image(): String? =
        selectFirst("img[data-src], img[data-original], img[src]")?.let {
            abs(mainUrl, it.attr("data-src").ifBlank { it.attr("data-original") }.ifBlank { it.attr("src") })
        }

    private fun cards(document: Document): List<SearchResponse> {
        // Genel WordPress kart yapilari; siteye ozel oldugu iddia edilmez.
        val nodes = document.select("article, .movie, .film, .post, .item")
        return nodes.mapNotNull { node ->
            val link = node.selectFirst("a[href]") ?: return@mapNotNull null
            val url = abs(mainUrl, link.attr("href")) ?: return@mapNotNull null
            if (!url.startsWith(mainUrl)) return@mapNotNull null
            val title = node.selectFirst("h2, h3, .title, .name")?.text()
                ?.takeIf { it.isNotBlank() }
                ?: link.attr("title").takeIf { it.isNotBlank() }
                ?: node.selectFirst("img[alt]")?.attr("alt")?.takeIf { it.isNotBlank() }
                ?: return@mapNotNull null
            newMovieSearchResponse(title, url, TvType.Movie) { posterUrl = node.image() }
        }.distinctBy { it.url }
    }

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val url = if (page <= 1) mainUrl else "$mainUrl/page/$page/"
        val document = app.get(url).document
        return newHomePageResponse(request.name, cards(document))
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val url = "$mainUrl/?s=${java.net.URLEncoder.encode(query, "UTF-8") }"
        return cards(app.get(url).document)
    }

    override suspend fun load(url: String): LoadResponse? {
        val document = app.get(url).document
        val title = document.selectFirst("h1")?.text()?.takeIf { it.isNotBlank() }
            ?: document.title().substringBefore("|").trim().takeIf { it.isNotBlank() }
            ?: return null
        val poster = document.selectFirst("meta[property=og:image]")?.attr("content")
            ?.let { abs(url, it) }
            ?: document.selectFirst("article img, .poster img")?.let {
                abs(url, it.attr("data-src").ifBlank { it.attr("src") })
            }
        val description = document.selectFirst("meta[name=description]")?.attr("content")
            ?: document.selectFirst(".description, .content, article p")?.text()
        return newMovieLoadResponse(title, url, TvType.Movie, url) {
            posterUrl = poster
            plot = description
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        val document = app.get(data).document
        val urls = linkedSetOf<String>()
        document.select("video[src], video source[src]").forEach { element ->
            abs(data, element.attr("src"))?.let(urls::add)
        }
        document.select("meta[property=og:video:url], meta[property=og:video]").forEach { element ->
            abs(data, element.attr("content"))?.let(urls::add)
        }
        urls.filter { it.startsWith("https://") &&
            (it.substringBefore('?').endsWith(".mp4", true) ||
             it.substringBefore('?').endsWith(".m3u8", true))
        }.forEach { video ->
            callback(ExtractorLink(
                name, name, video, data, Qualities.Unknown.value,
                isM3u8 = video.substringBefore('?').endsWith(".m3u8", true)
            ))
        }
        return urls.any { it.startsWith("https://") &&
            (it.substringBefore('?').endsWith(".mp4", true) ||
             it.substringBefore('?').endsWith(".m3u8", true)) }
    }
}
