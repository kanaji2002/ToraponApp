package com.example.myapplication.componentsPage2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun Page2AppNavHost(navController: NavHostController, backgroundColor: Color, selectionBackgroundColor: Color) {
    NavHost(navController = navController, startDestination = "list") {
        // 観光スポット一覧ページ
        composable("list") {
            Page2ScreenList(navController, backgroundColor, selectionBackgroundColor)
        }

        // 詳細ページ（folderName を引数として取得）
        composable("Detail/{folderName}") { backStackEntry ->
            val folderArg = backStackEntry.arguments?.getString("folderName") ?: "Unknown Folder"

            // 📌 MutableState に変換して渡す.
            val folderNameState = remember { mutableStateOf(folderArg) }

            Page2ScreenDetail(navController, backgroundColor, folderNameState)
        }
    }
}
