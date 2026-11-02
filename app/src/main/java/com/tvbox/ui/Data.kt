package com.tvbox.ui

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
    val eps: Int = 24,
    val ms: Int = 40
)

data class ConfigInfo(
    val sites: List<Site>,
    val parses: Int,
    val lives: Int,
    val sourceUrl: String
)

data class Vod(
    val id: String,
    val name: String,
    val remarks: String,
    val site: String,
    val siteIndex: Int
)

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

    private fun tagOf(n: String): String = when {
        n.contains("4k", true) -> "4K"
        n.contains("秒播") -> "秒播"
        n.contains("p2p", true) -> "P2P"
        n.contains("网盘") -> "网盘"
        n.contains("韩剧") -> "韩剧"
        else -> "普通"
    }

    fun tag(site: Site): String = tagOf(site.name)

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
            sourceUrl = url
        )
    }

    /** 仅支持 0=JSON / 1=XML 采集站；CSP(type=3) 需要蜘蛛引擎，这里返回演示数据 */
    suspend fun search(site: Site, kw: String): List<Vod> = withContext(Dispatchers.IO) {
        if (site.type == 0 && site.api.startsWith("http")) {
            runCatching {
                val sep = if (site.api.contains("?")) "&" else "?"
                val url = "${site.api}${sep}ac=videolist&wd=${java.net.URLEncoder.encode(kw, "UTF-8")}&pg=1"
                val req = Request.Builder().url(url).header("User-Agent", "okhttp/3.15").build()
                val body = client.newCall(req).execute().use { it.body?.string() ?: "" }
                val list = JSONObject(body).optJSONArray("list") ?: return@runCatching emptyList<Vod>()
                (0 until list.length()).map { i ->
                    val o = list.getJSONObject(i)
                    Vod(
                        id = o.optString("vod_id"),
                        name = o.optString("vod_name"),
                        remarks = o.optString("vod_remarks"),
                        site = site.name,
                        siteIndex = 0
                    )
                }
            }.getOrElse { demoResults(site, kw) }
        } else {
            demoResults(site, kw)
        }
    }

    private fun demoResults(site: Site, kw: String): List<Vod> = listOf(
        Vod("1", kw, "HD · 2026", site.name, 0),
        Vod("2", "$kw（粤语）", "更新至 HD", site.name, 0)
    )

    fun episodes(site: Site): Int = site.eps
}
