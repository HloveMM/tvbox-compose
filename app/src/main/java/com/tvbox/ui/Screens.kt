package com.tvbox.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

/* ---------------- 首页 ---------------- */
@Composable
fun HomeScreen(state: AppState, onPickSource: () -> Unit, onSearch: () -> Unit, onOpenDetail: (Vod) -> Unit) {
    val site = state.site
    LazyColumn(Modifier.fillMaxSize().background(Bg)) {
        item {
            Row(
                Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    Modifier.clip(RoundedCornerShape(999.dp))
                        .background(Color.White.copy(alpha = .07f))
                        .clickable { onPickSource() }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusDot(site?.ms ?: 40)
                    Spacer(Modifier.width(7.dp))
                    Text(
                        site?.name ?: "选择源",
                        color = Text1, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold,
                        maxLines = 1, overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.widthIn(max = 170.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("▾", color = Text2, fontSize = 11.sp)
                }
                Text("TVBox", color = Text1, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
        item {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color.White.copy(alpha = .06f))
                    .clickable { onSearch() }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) { Text("搜索影片名 / 演员 / 导演", color = Text3, fontSize = 12.5.sp) }
        }
        item {
            LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                itemsIndexed(listOf("推荐", "电视剧", "电影", "综艺", "动漫", "少儿")) { i, c ->
                    Pill(c, selected = i == 0)
                }
            }
        }
        item {
            Box(Modifier.padding(14.dp, 14.dp, 20.dp, 0.dp)) {
                Box(Modifier.fillMaxWidth().height(214.dp).clip(RoundedCornerShape(22.dp))) {
                    PosterArt(0, modifier = Modifier.fillMaxSize())
                    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xEB040508)))))
                    Column(Modifier.align(Alignment.BottomStart).padding(18.dp)) {
                        Box(
                            Modifier.clip(RoundedCornerShape(8.dp)).background(Accent.copy(alpha = .92f))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) { Text("● TVBox 独播", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                        Spacer(Modifier.height(9.dp))
                        Text("星际彼岸", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "科幻 · 2026 · 全 24 集 · ${state.sites.size} 源可选",
                            color = Color.White.copy(alpha = .72f), fontSize = 12.5.sp
                        )
                    }
                    Row(
                        Modifier.align(Alignment.BottomEnd).padding(16.dp)
                            .clip(RoundedCornerShape(999.dp)).background(Color.White.copy(alpha = .94f))
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("▶", color = Color(0xFF0A0B10), fontSize = 12.sp)
                        Spacer(Modifier.width(6.dp))
                        Text("立即播放", color = Color(0xFF0A0B10), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.Center) {
                repeat(4) { i ->
                    Box(
                        Modifier.padding(3.dp).height(5.dp).width(if (i == 0) 18.dp else 5.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (i == 0) Accent else Color.White.copy(alpha = .22f))
                    )
                }
            }
        }
        item { SectionHead("继续观看", "全部") }
        item {
            LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                itemsIndexed(listOf("长夜将明" to "看到 第 8 集", "山河令·续" to "看到 第 3 集", "云端之上" to "看到 第 12 集")) { i, it ->
                    PosterCard(it.first, it.second, seed = i + 2, progress = 0.62f - i * 0.2f)
                }
            }
        }
        item { SectionHead("今日热播", "更多") }
        itemsIndexed(
            listOf(
                Triple("雾港迷踪", "悬疑 · 更新至 16 集", "热度 9821"),
                Triple("剑落长安", "古装 · 全 40 集", "热度 9140"),
                Triple("星际彼岸", "科幻 · 更新至 24 集", "热度 8876"),
                Triple("林间四时", "纪录片 · 全 6 集", "热度 8210"),
                Triple("浮空岛", "动画 · 全 26 集", "热度 7902")
            )
        ) { i, r ->
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 11.dp)
                    .clickable { onOpenDetail(Vod("${i}", r.first, r.second, state.site?.name ?: "", state.currentSite)) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${i + 1}", color = if (i < 3) Accent else Text3, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.width(20.dp))
                Spacer(Modifier.width(13.dp))
                PosterArt(i + 1, modifier = Modifier.width(50.dp).height(68.dp))
                Spacer(Modifier.width(13.dp))
                Column(Modifier.weight(1f)) {
                    Text(r.first, color = Text1, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                    Spacer(Modifier.height(5.dp))
                    Text(r.second, color = Text3, fontSize = 11.5.sp, maxLines = 1)
                }
                Text(r.third, color = Accent, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}

/* ---------------- 聚合搜索（真实多源） ---------------- */
@Composable
fun SearchScreen(state: AppState, onOpenDetail: (Vod) -> Unit) {
    val palette = listOf(Color(0xFFFFB347), Color(0xFFFF6FA8), Color(0xFF5AC8FA), Color(0xFFB46BFF), Color(0xFF3DDC97))
    var groups by remember { mutableStateOf<List<Pair<Site, List<Vod>>>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var total by remember { mutableStateOf(0) }

    LaunchedEffect(state.currentSite, state.spiderUrl) {
        if (state.sites.isEmpty()) return@LaunchedEffect
        loading = true
        val targets = state.sites.take(6)
        val res = targets.mapNotNull { s ->
            val list = runCatching { Repo.search(s, "空枪", state.spiderUrl) }.getOrDefault(emptyList())
            if (list.isEmpty()) null else s to list
        }
        groups = res
        total = res.size
        loading = false
    }

    Column(Modifier.fillMaxSize().background(Bg)) {
        Row(Modifier.fillMaxWidth().padding(16.dp, 18.dp, 16.dp, 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.weight(1f).height(38.dp).clip(RoundedCornerShape(19.dp))
                    .background(Color.White.copy(alpha = .07f)).padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) { Text("空枪", color = Text1, fontSize = 13.sp) }
            Spacer(Modifier.width(8.dp))
            Text("精确", color = Accent, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
        Row(Modifier.weight(1f)) {
            LazyColumn(Modifier.width(98.dp).padding(start = 12.dp)) {
                item { Text("搜索(${if (loading) "…" else "$total"}/${state.sites.size})", color = Text3, fontSize = 11.sp, modifier = Modifier.padding(bottom = 8.dp)) }
                item {
                    Box(
                        Modifier.fillMaxWidth().padding(bottom = 10.dp).clip(RoundedCornerShape(8.dp))
                            .border(1.dp, Color.White.copy(alpha = .14f), RoundedCornerShape(8.dp))
                            .padding(vertical = 7.dp),
                        contentAlignment = Alignment.Center
                    ) { Text("全部显示", color = Text2, fontSize = 11.sp) }
                }
                itemsIndexed(state.sites.take(16)) { i, s ->
                    Text(
                        s.name, color = if (i == state.currentSite) Accent else Text2,
                        fontSize = 11.5.sp, maxLines = 1, overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(7.dp))
                            .background(if (i == state.currentSite) Accent.copy(alpha = .1f) else Color.Transparent)
                            .clickable { state.currentSite = i }
                            .padding(horizontal = 6.dp, vertical = 8.dp)
                    )
                }
            }
            LazyColumn(Modifier.weight(1f).padding(horizontal = 10.dp)) {
                if (loading) {
                    item { Text("正在连接采集源…", color = Text3, fontSize = 12.sp, modifier = Modifier.padding(12.dp)) }
                } else if (groups.isEmpty()) {
                    item { Text("没有结果。CSP 源需要蜘蛛 jar，首次会下载引擎，稍后再试。", color = Text3, fontSize = 12.sp, modifier = Modifier.padding(12.dp)) }
                }
                itemsIndexed(groups) { gi, g ->
                    Column {
                        Box(
                            Modifier.clip(RoundedCornerShape(6.dp))
                                .background(palette[gi % palette.size].copy(alpha = .16f))
                                .padding(horizontal = 9.dp, vertical = 4.dp)
                        ) { Text(g.first.name, color = palette[gi % palette.size], fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold) }
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                            g.second.take(2).forEachIndexed { k, v ->
                                Column(
                                    Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                                        .background(Color.White.copy(alpha = .04f))
                                        .clickable { onOpenDetail(v) }
                                ) {
                                    PosterArt(gi + k, modifier = Modifier.fillMaxWidth().height(108.dp))
                                    Column(Modifier.padding(7.dp)) {
                                        Text(v.name, color = Text1, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Spacer(Modifier.height(3.dp))
                                        Text(v.remarks.ifBlank { "—" }, color = Text3, fontSize = 9.5.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                }
                            }
                            if (g.second.size == 1) Spacer(Modifier.weight(1f))
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

/* ---------------- 详情（真实线路与选集） ---------------- */
@Composable
fun DetailScreen(state: AppState, onPlay: () -> Unit) {
    val vod = state.vod ?: return
    val site = state.site ?: return

    LaunchedEffect(vod.id, site.key) {
        state.detailLoading = true
        state.detail = Repo.detail(site, vod, state.spiderUrl)
        state.detailLoading = false
        state.line = 0
        state.ep = 0
    }

    val detail = state.detail
    val lines = detail?.lines ?: emptyList()
    val episodes = state.currentEpisodes

    LazyColumn(Modifier.fillMaxSize().background(Bg)) {
        item {
            Box(Modifier.fillMaxWidth().height(230.dp)) {
                PosterArt(0, modifier = Modifier.fillMaxSize())
                Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xDD08090D), Bg))))
                Text("‹", color = Color.White, fontSize = 26.sp, modifier = Modifier.padding(16.dp, 18.dp, 0.dp, 0.dp))
            }
        }
        item {
            Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp).offset(y = (-56).dp)) {
                PosterArt(1, modifier = Modifier.width(104.dp).height(148.dp))
                Spacer(Modifier.width(16.dp))
                Column(Modifier.padding(top = 60.dp)) {
                    Text(detail?.name ?: vod.name, color = Text1, fontSize = 23.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Spacer(Modifier.height(8.dp))
                    Text("★ 9.2   2026 · 全 ${episodes.size} 集", color = Text2, fontSize = 12.sp)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("科幻", "太空歌剧", "4K").forEach {
                            Box(Modifier.clip(RoundedCornerShape(6.dp)).background(Color.White.copy(alpha = .08f)).padding(horizontal = 8.dp, vertical = 3.dp)) {
                                Text(it, color = Text2, fontSize = 10.5.sp)
                            }
                        }
                    }
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp).offset(y = (-30).dp)) {
                Box(
                    Modifier.weight(1f).height(46.dp).clip(RoundedCornerShape(14.dp)).background(AccentBrush).clickable { onPlay() },
                    contentAlignment = Alignment.Center
                ) { Text("▶  立即播放", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold) }
                Spacer(Modifier.width(10.dp))
                listOf("⤓", "★").forEach {
                    Box(Modifier.width(56.dp).height(46.dp).clip(RoundedCornerShape(14.dp)).background(Color.White.copy(alpha = .07f)), contentAlignment = Alignment.Center) {
                        Text(it, color = Text1, fontSize = 16.sp)
                    }
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp), horizontalArrangement = Arrangement.SpaceAround) {
                listOf("下载" to "⤓", "快搜" to "⌕", "Exo" to "▶", "投屏" to "⇪", "设置" to "⚙").forEach {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(it.second, color = Text2, fontSize = 17.sp)
                        Spacer(Modifier.height(5.dp))
                        Text(it.first, color = Text3, fontSize = 10.5.sp)
                    }
                }
            }
        }
        if (state.detailLoading) {
            item { Text("正在解析线路…", color = Text3, fontSize = 12.sp, modifier = Modifier.padding(20.dp)) }
        }
        if (lines.isNotEmpty()) {
            item { Text("线路", color = Text1, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(20.dp, 14.dp, 20.dp, 8.dp)) }
            item {
                LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    itemsIndexed(lines) { i, l ->
                        Box(
                            Modifier.clip(RoundedCornerShape(11.dp))
                                .background(if (i == state.line) AccentBrush else Brush.horizontalGradient(listOf(Color.White.copy(alpha = .05f), Color.White.copy(alpha = .05f))))
                                .clickable { state.line = i; state.ep = 0 }
                                .padding(horizontal = 13.dp, vertical = 9.dp)
                        ) { Text(l.name, color = if (i == state.line) Color.White else Text2, fontSize = 12.sp, fontWeight = if (i == state.line) FontWeight.SemiBold else FontWeight.Normal) }
                    }
                }
            }
        }
        item {
            Text(
                (detail?.content?.takeIf { it.isNotBlank() } ?: "暂无简介"),
                color = Text2, fontSize = 12.5.sp, lineHeight = 21.sp, modifier = Modifier.padding(20.dp, 16.dp, 20.dp, 0.dp)
            )
        }
        item {
            Row(Modifier.fillMaxWidth().padding(20.dp, 18.dp, 20.dp, 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("选集", color = Text1, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Text("共 ${episodes.size} 集 · ${site.name}", color = Text3, fontSize = 12.sp)
            }
        }
        items((episodes.size + 4) / 5) { row ->
            Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                repeat(5) { col ->
                    val n = row * 5 + col
                    if (n < episodes.size) {
                        val on = n == state.ep
                        Box(
                            Modifier.weight(1f).height(38.dp).clip(RoundedCornerShape(10.dp))
                                .background(if (on) AccentBrush else Brush.horizontalGradient(listOf(Color.White.copy(alpha = .06f), Color.White.copy(alpha = .06f))))
                                .clickable { state.ep = n; onPlay() },
                            contentAlignment = Alignment.Center
                        ) { Text("${n + 1}", color = if (on) Color.White else Text2, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
                    } else Spacer(Modifier.weight(1f))
                }
            }
        }
        item { Spacer(Modifier.height(24.dp)) }
    }
}

/* ---------------- 播放（ExoPlayer） ---------------- */
@Composable
fun VideoPlayer(url: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val player = remember(url) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(url))
            prepare()
            playWhenReady = true
        }
    }
    DisposableEffect(url) { onDispose { player.release() } }
    AndroidView(factory = { PlayerView(it).apply { this.player = player } }, modifier = modifier)
}

@Composable
fun PlayerScreen(state: AppState) {
    val vod = state.vod ?: return
    val eps = state.currentEpisodes
    val current = eps.getOrNull(state.ep)
    val lines = state.detail?.lines ?: emptyList()

    Column(Modifier.fillMaxSize().background(Bg).verticalScroll(rememberScrollState())) {
        Box(Modifier.fillMaxWidth().aspectRatio(16f / 9f).background(Color.Black)) {
            if (current != null) {
                VideoPlayer(current.url, Modifier.fillMaxSize())
            } else {
                PosterArt(0, modifier = Modifier.fillMaxSize())
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("暂无可播放地址", color = Color.White.copy(alpha = .8f), fontSize = 13.sp)
                }
            }
        }
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("线路", color = Text3, fontSize = 11.sp)
            Spacer(Modifier.width(10.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                itemsIndexed(lines) { i, l ->
                    Box(
                        Modifier.clip(RoundedCornerShape(9.dp))
                            .background(if (i == state.line) Accent.copy(alpha = .16f) else Color.White.copy(alpha = .06f))
                            .clickable { state.line = i; state.ep = 0 }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) { Text(l.name, color = if (i == state.line) Color(0xFFFF9A86) else Text2, fontSize = 11.5.sp) }
                }
            }
        }
        Column(Modifier.padding(20.dp)) {
            Text("${state.detail?.name ?: vod.name} · 第 ${state.ep + 1} 集", color = Text1, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("来源 ${state.site?.name ?: ""} · ${current?.name ?: ""}", color = Text3, fontSize = 12.sp)
        }
        Spacer(Modifier.height(20.dp))
    }
}

/* ---------------- 数据源 ---------------- */
@Composable
fun SourcesScreen(state: AppState) {
    val cfg = state.config
    val filters = listOf("全部", "4K", "秒播", "P2P", "网盘")
    val list = if (state.filter == "全部") state.sites else state.sites.filter { Repo.tag(it) == state.filter }
    LazyColumn(Modifier.fillMaxSize().background(Bg)) {
        item { Text("数据源", color = Text1, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(20.dp, 20.dp, 20.dp, 12.dp)) }
        item {
            Column(
                Modifier.padding(horizontal = 20.dp).fillMaxWidth().clip(RoundedCornerShape(18.dp))
                    .background(Brush.linearGradient(listOf(Accent.copy(alpha = .16f), Color.White.copy(alpha = .03f))))
                    .padding(16.dp)
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("影视仓 · 主配置", color = Text1, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Box(Modifier.clip(RoundedCornerShape(6.dp)).background(OkColor.copy(alpha = .14f)).padding(horizontal = 8.dp, vertical = 3.dp)) {
                        Text(if (cfg != null) "已连接" else "连接中", color = Color(0xFF7FF0BC), fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(cfg?.sourceUrl ?: Repo.DEFAULT_CONFIG, color = Text3, fontSize = 10.5.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("${state.sites.size} 个源", "${cfg?.parses ?: 0} 解析", "${cfg?.lives ?: 0} 直播").forEach {
                        Box(Modifier.clip(RoundedCornerShape(8.dp)).background(Color.White.copy(alpha = .07f)).padding(horizontal = 9.dp, vertical = 4.dp)) {
                            Text(it, color = Text2, fontSize = 10.5.sp)
                        }
                    }
                }
            }
        }
        item { SectionHead("播放源", "${list.size} / ${state.sites.size}") }
        item {
            LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                itemsIndexed(filters) { _, f -> Pill(f, selected = state.filter == f) { state.filter = f } }
            }
        }
        itemsIndexed(list.take(40)) { i, s ->
            Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                StatusDot(s.ms)
                Spacer(Modifier.width(11.dp))
                Text(s.name, color = Text1, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                Box(Modifier.clip(RoundedCornerShape(5.dp)).background(Color.White.copy(alpha = .08f)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                    Text(if (s.type == 3) "CSP" else "JSON", color = Text3, fontSize = 9.5.sp)
                }
                Spacer(Modifier.width(10.dp))
                Text("${s.ms}ms", color = if (s.ms > 120) Text3 else if (s.ms > 80) WarnColor else Color(0xFF7FF0BC), fontSize = 10.5.sp)
            }
        }
        item { Spacer(Modifier.height(20.dp)) }
    }
}

/* ---------------- 选择采集源 ---------------- */
@Composable
fun SourceSheet(state: AppState, onClose: () -> Unit) {
    if (!state.sheet) return
    Box(
        Modifier.fillMaxSize().background(Color.Black.copy(alpha = .55f)).clickable { onClose() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            Modifier.fillMaxWidth().heightIn(max = 560.dp)
                .clip(RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp))
                .background(Color(0xFF14161D))
                .clickable(enabled = false) {}
                .padding(bottom = 16.dp)
        ) {
            Box(Modifier.padding(top = 8.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(Modifier.width(36.dp).height(4.dp).clip(RoundedCornerShape(3.dp)).background(Color.White.copy(alpha = .2f)))
            }
            Row(Modifier.fillMaxWidth().padding(18.dp, 14.dp, 18.dp, 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("选择采集源", color = Text1, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text("${state.sites.size} 个可用", color = Text3, fontSize = 11.sp)
            }
            LazyColumn(Modifier.weight(1f, fill = false).padding(horizontal = 8.dp)) {
                itemsIndexed(state.sites.take(30)) { i, s ->
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                            .background(if (i == state.currentSite) Accent.copy(alpha = .14f) else Color.Transparent)
                            .clickable { state.currentSite = i; onClose() }
                            .padding(horizontal = 12.dp, vertical = 11.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusDot(s.ms)
                        Spacer(Modifier.width(9.dp))
                        Text(
                            s.name, color = if (i == state.currentSite) Color(0xFFFF9A86) else Text2,
                            fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis,
                            fontWeight = if (i == state.currentSite) FontWeight.SemiBold else FontWeight.Normal,
                            modifier = Modifier.weight(1f)
                        )
                        Text("${s.eps}集 · ${s.ms}ms", color = Text3, fontSize = 10.5.sp)
                    }
                }
            }
        }
    }
}
