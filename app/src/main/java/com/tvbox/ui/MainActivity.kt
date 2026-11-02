package com.tvbox.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TVBoxTheme {
                val state = remember { AppState() }
                val scope = rememberCoroutineScope()

                LaunchedEffect(Unit) {
                    runCatching { Repo.loadConfig() }
                        .onSuccess {
                            state.config = it
                            state.sites = it.sites
                            state.loading = false
                        }
                        .onFailure {
                            state.error = it.message
                            state.sites = demo()
                            state.loading = false
                        }
                }

                BackHandler(enabled = state.sheet) { state.sheet = false }
                BackHandler(enabled = state.screen == Screen.Detail || state.screen == Screen.Player) {
                    state.screen = if (state.screen == Screen.Player) Screen.Detail else Screen.Home
                }

                Box(Modifier.fillMaxSize().background(Bg)) {
                    val showBar = state.screen == Screen.Home || state.screen == Screen.Search || state.screen == Screen.Sources
                    Scaffold(
                        containerColor = Bg,
                        bottomBar = {
                            if (showBar) {
                                BottomBar(
                                    current = when (state.screen) {
                                        Screen.Home -> 0
                                        Screen.Search -> 1
                                        else -> 2
                                    }
                                ) { idx ->
                                    state.screen = when (idx) {
                                        0 -> Screen.Home
                                        1 -> Screen.Search
                                        else -> Screen.Sources
                                    }
                                }
                            }
                        }
                    ) { pad ->
                        Box(Modifier.padding(pad)) {
                            when (state.screen) {
                                Screen.Home -> HomeScreen(
                                    state,
                                    onPickSource = { state.sheet = true },
                                    onSearch = { state.screen = Screen.Search },
                                    onOpenDetail = { state.vod = it; state.ep = 0; state.screen = Screen.Detail }
                                )
                                Screen.Search -> SearchScreen(state) {
                                    state.vod = it; state.ep = 0; state.screen = Screen.Detail
                                }
                                Screen.Sources -> SourcesScreen(state)
                                Screen.Detail -> DetailScreen(state) { state.screen = Screen.Player }
                                Screen.Player -> PlayerScreen(state)
                            }
                        }
                    }
                    if (state.loading) {
                        Box(Modifier.fillMaxSize().background(Bg), contentAlignment = Alignment.Center) {
                            Text("正在读取配置…", color = Text2, fontSize = 13.sp)
                        }
                    }
                    SourceSheet(state) { state.sheet = false }
                }
            }
        }
    }

    private fun demo(): List<Site> = listOf(
        Site("1", "玩偶·4K", 3, "", 25, 38),
        Site("2", "无名·4K", 3, "", 19, 94),
        Site("3", "多多·4K", 3, "", 25, 29),
        Site("4", "木偶·4K", 3, "", 36, 131),
        Site("5", "双星·4K", 3, "", 30, 102),
        Site("6", "虎斑·4K", 3, "", 25, 49),
        Site("7", "韩剧·秒播", 3, "", 20, 94),
        Site("8", "葵花·影视", 3, "", 20, 127)
    )
}
