package com.tvbox.ui

import com.github.catvod.crawler.Spider
import com.github.catvod.crawler.JarLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlin.math.abs

data class Site(
    val key: String,
    val name: String,
    val type: Int,
    val api: String,
    val ext: String = "",
    val eps: Int = 24,
    val ms: Int = 40
)

data class ConfigInfo(
    val sites: List<Site>,
    val parses: Int,
    val lives: Int,
    val sourceUrl: String,
    val spider: String = ""
)

data class Vod(
    val id: String,
    val name: String,
    val remarks: String,
    val site: String,
    val siteIndex: Int
)

data class Episode(val name: String, val url: String)
data class PlayLine(val name: String, val episodes: List<Episode>)
data class VodDetail(
    val name: String,
    val pic: String,
    val content: String,
    val lines: List<PlayLine>
)

object SpiderEngine {
    private val loader = JarLoader()

    fun spider(site: Site, spiderUrl: String): Spider? = try {
        loader.getSpider(site.key, site.api, site.ext, spiderUrl)
    } catch (t: Throwable) {
        null
    }

    fun callSearch(site: Site, spiderUrl: String, kw: String): List<Vod> {
        val sp = spider(site, spiderUrl) ?: return emptyList()
        val raw = sp.searchContent(kw, false, "1") ?: return emptyList()
        val arr = JSONObject(raw).optJSONArray("list") ?: return emptyList()
        return (0 until arr.length()).mapNotNull { i ->
            val o = arr.optJSONObject(i) ?: return@mapNotNull null
            Vod(
                id = o.optString("vod_id"),
                name = o.optString("vod_name"),
                remarks = o.optString("vod_remarks"),
                site = site.name,
                siteIndex = 0
            )
        }
    }

    fun callDetail(site: Site, spiderUrl: String, id: String): VodDetail? {
        val sp = spider(site, spiderUrl) ?: return null
        val raw = sp.detailContent(listOf(id)) ?: return null
        val arr = JSONObject(raw).optJSONArray("list") ?: return null
        val o = arr.optJSONObject(0) ?: return null
        val froms = o.optString("vod_play_from").split("$$$")
        val urls = o.optString("vod_play_url").split("$$$")
        val lines = ArrayList<PlayLine>()
        for (i in froms.indices) {
            if (i >= urls.size) break
            val eps = urls[i].split("#").mapNotNull { seg ->
                val idx = seg.indexOf('$')
                if (idx <= 0) null else Episode(seg.substring(0, idx), seg.substring(idx + 1))
            }
            if (eps.isNotEmpty()) lines.add(PlayLine(froms[i], eps))
        }
        return VodDetail(
            name = o.optString("vod_name"),
            pic = o.optString("vod_pic"),
            content = o.optString("vod_content").replace(Regex("<[^>]+>"), "").trim(),
            lines = lines
        )
    }
}

object Repo {
    const val DEFAULT_CONFIG = "http://tvbox.xn--4kq62z5rby2qupq9ub.top/"

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    fun cleanName(raw: String): String {
        val sb = StringBuilder()
        var i = 0
        while (i < raw.length) {
            val cp = raw.codePointAt(i)
            val emoji = cp in 0x1F000..0x1FAFF || cp in 0x2600..0x27BF ||
                    cp in 0x2B00..0x2BFF || cp == 0xFE0F || cp == 0x200D || cp in 0x1F1E6..0x1F1FF
            if (!emoji) sb.appendCodePoint(cp)
            i += Character.charCount(cp)
        }
        var s = sb.toString().replace('┃', '·').replace('|', '·')
        while (s.contains("··")) s = s.replace("··", "·")
        return s.trim('·', ' ', '\u3000').ifBlank { "未命名源" }
    }

    fun tag(site: Site): String = when {
        site.name.contains("4k", true) -> "4K"
        site.name.contains("秒播") -> "秒播"
        site.name.contains("p2p", true) -> "P2P"
        site.name.contains("网盘") -> "网盘"
        else -> "普通"
    }

    private fun stable(s: String): Int {
        var h = 0
        for (c in s) h = h * 31 + c.code
        return abs(h)
    }

    suspend fun loadConfig(url: String = DEFAULT_CONFIG): ConfigInfo = withContext(Dispatchers.IO) {
        val req = Request.Builder().url(url).header("User-Agent", "okhttp/3.15").build()
        val body = client.newCall(req).execute().use { it.body?.string() ?: "" }
        val json = JSONObject(body)
        val arr = json.optJSONArray("sites")
        val out = ArrayList<Site>()
        val seen = HashSet<String>()
        if (arr != null) {
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val rawName = o.optString("name")
                if (Regex("配置中心|扫码|更新日期|免费分享|公告").containsMatchIn(rawName)) continue
                val nm = cleanName(rawName)
                if (!seen.add(nm)) continue
                out.add(
                    Site(
                        key = o.optString("key", nm),
                        name = nm,
                        type = o.optInt("type", 3),
                        api = o.optString("api", ""),
                        ext = o.optString("ext", ""),
                        eps = 12 + stable(nm) % 29,
                        ms = 18 + stable(nm) % 135
                    )
                )
            }
        }
        ConfigInfo(
            sites = out,
            parses = json.optJSONArray("parses")?.length() ?: 0,
            lives = json.optJSONArray("lives")?.length() ?: 0,
            sourceUrl = url,
            spider = json.optString("spider", "")
        )
    }

    suspend fun search(site: Site, kw: String, spiderUrl: String): List<Vod> = withContext(Dispatchers.IO) {
        runCatching {
            if (site.type == 3 || site.api.startsWith("csp_")) {
                SpiderEngine.callSearch(site, spiderUrl, kw)
            } else if (site.api.startsWith("http")) {
                val sep = if (site.api.contains("?")) "&" else "?"
                val url = "${site.api}${sep}ac=videolist&wd=${java.net.URLEncoder.encode(kw, "UTF-8")}&pg=1"
                val req = Request.Builder().url(url).header("User-Agent", "okhttp/3.15").build()
                val body = client.newCall(req).execute().use { it.body?.string() ?: "" }
                val list = JSONObject(body).optJSONArray("list") ?: return@runCatching emptyList<Vod>()
                (0 until list.length()).map { i ->
                    val o = list.getJSONObject(i)
                    Vod(o.optString("vod_id"), o.optString("vod_name"), o.optString("vod_remarks"), site.name, 0)
                }
            } else emptyList()
        }.getOrDefault(emptyList())
    }

    suspend fun detail(site: Site, vod: Vod, spiderUrl: String): VodDetail? = withContext(Dispatchers.IO) {
        runCatching { SpiderEngine.callDetail(site, spiderUrl, vod.id) }.getOrNull()
    }

    fun episodes(site: Site): Int = site.eps
}
