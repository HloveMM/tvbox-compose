package com.tvbox.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

sealed interface Screen {
    data object Home : Screen
    data object Search : Screen
    data object Sources : Screen
    data object Detail : Screen
    data object Player : Screen
}

class AppState {
    var screen by mutableStateOf<Screen>(Screen.Home)
    var sites by mutableStateOf<List<Site>>(emptyList())
    var config by mutableStateOf<ConfigInfo?>(null)
    var currentSite by mutableStateOf(0)
    var loading by mutableStateOf(true)
    var error by mutableStateOf<String?>(null)
    var sheet by mutableStateOf(false)
    var vod by mutableStateOf<Vod?>(null)
    var detail by mutableStateOf<VodDetail?>(null)
    var detailLoading by mutableStateOf(false)
    var line by mutableStateOf(0)
    var ep by mutableStateOf(0)
    var filter by mutableStateOf("全部")

    val site: Site?
        get() = sites.getOrNull(currentSite)

    val spiderUrl: String
        get() = config?.spider.orEmpty()

    val currentEpisodes: List<Episode>
        get() = detail?.lines?.getOrNull(line)?.episodes.orEmpty()
}
