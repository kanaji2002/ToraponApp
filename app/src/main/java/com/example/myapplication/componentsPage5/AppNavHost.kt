package com.example.myapplication.componentsPage5

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun Page5AppNavHost(
    navController: NavHostController,
    backgroundColor: Color,
    selectionBackgroundColor: Color
) {
    NavHost(navController = navController, startDestination = "list") {
        // 🗺 観光スポット一覧ページ（横スクロールUIを表示）
        composable("list") {
            ScrollableCardRow() // ← 横スクロールのカード表示
        }


    }
}
