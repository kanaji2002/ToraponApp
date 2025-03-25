package com.example.myapplication
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
//Osaka import(追加)
import androidx.compose.ui.graphics.Color
import com.example.myapplication.componentsPage1.Page1AppNavHost // AppNavHost.ktからインポート
import com.example.myapplication.componentsPage2.Page2AppNavHost // AppNavHost.ktからインポート
import com.example.myapplication.componentsPage5.Page5AppNavHost // AppNavHost.ktからインポート

import com.example.myapplication.utils.REQUIRED_PERMISSIONS
import com.example.myapplication.utils.allPermissionsGranted
import com.example.myapplication.utils.hideSystemUI

// ----------- MainActivity クラスを追加 -----------
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

            super.onCreate(savedInstanceState)
            // registerForActivityResult はここで宣言
            val requestPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                val allGranted = permissions.all { it.value }
                if (!allGranted) {
                    println("位置情報の権限が拒否されました")
                }
            }

            // Utils に分離した関数を使用
            if (!allPermissionsGranted(this)) {
                requestPermissionLauncher.launch(REQUIRED_PERMISSIONS)
            }
            setContent {
                MyTabletApp()
            }
        }

        override fun onWindowFocusChanged(hasFocus: Boolean) {
            super.onWindowFocusChanged(hasFocus)
            if (hasFocus) {
                hideSystemUI(window)  // これも UIUtils に分離可能
            }
        }
   }

    // ---------- 1. メイン画面で Scaffold + NavHost + BottomBar を構築 ----------
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MyTabletApp() {
        val navController = rememberNavController()

        Scaffold(
            bottomBar = { MyBottomBar(navController = navController) }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = BottomItem.Page1.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                // 5つのページを定義
                composable(BottomItem.Page1.route) { Page1Screen() }
                composable(BottomItem.Page2.route) { Page2Screen() }
                composable(BottomItem.Page3.route) { Page3Screen() }
                composable(BottomItem.Page4.route) { Page4Screen() }
                composable(BottomItem.Page5.route) { Page5Screen() }
            }
        }
    }

    // ---------- 2. 下部バー (NavigationBar) ----------
    @Composable
    fun MyBottomBar(navController: NavHostController) {
        val items = listOf(
            BottomItem.Page1,
            BottomItem.Page2,
            BottomItem.Page3,
            BottomItem.Page4,
            BottomItem.Page5,
        )

        val currentRoute = currentRoute(navController)

        NavigationBar {
            items.forEach { item ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(item.iconRes),
                            contentDescription = item.label
                        )
                    },
                    label = null,
                    selected = currentRoute == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                        }
                    }
                )
            }
        }
    }

    // ---------- 3. アイテム定義 ----------
    sealed class BottomItem(val route: String, val iconRes: Int, val label: String) {
        object Page1 : BottomItem("page1", R.drawable.distination, "Page1")
        object Page2 : BottomItem("page2", R.drawable.picture, "Page2")
        object Page3 : BottomItem("page3", R.drawable.coin, "Page3")
        object Page4 : BottomItem("page4", R.drawable.shop, "Page4")
        object Page5 : BottomItem("page5", R.drawable.three_dot, "Page5")
    }
// ===================================================================================

    @Composable
    fun Page1Screen() {
        // 設定
        val navController = rememberNavController()
        val backgroundColor = Color.Yellow.copy(alpha = 0.1f)// 観光スポット一覧ページの背景色
        val selectionBackgroundColor = Color.LightGray.copy(alpha = 0.5f)// 選択画面背景色
        Page1AppNavHost(navController, backgroundColor, selectionBackgroundColor)
    }

    // 観光スポット一覧ページ(担当:Osaka)
    @Composable
    fun Page2Screen() {
        // 設定
        val navController = rememberNavController()
        val backgroundColor = Color.Yellow.copy(alpha = 0.1f)// 観光スポット一覧ページの背景色
        val selectionBackgroundColor = Color.LightGray.copy(alpha = 0.5f)// 選択画面背景色
        Page2AppNavHost(navController, backgroundColor, selectionBackgroundColor)
    }

    @Composable
    fun Page3Screen() {
        Text("Page3 Screen")
    }

    @Composable
    fun Page4Screen() {
        Text("Page4 Screen")
    }

@Composable
fun Page5Screen() {
    // 設定
    val navController = rememberNavController()
    val backgroundColor = Color.Yellow.copy(alpha = 0.1f)// 観光スポット一覧ページの背景色
    val selectionBackgroundColor = Color.LightGray.copy(alpha = 0.5f)// 選択画面背景色
    Page5AppNavHost(navController, backgroundColor, selectionBackgroundColor)
}

    // ---------- 5. 現在のルートを取得するヘルパー ----------
    @Composable
    fun currentRoute(navController: NavHostController): String? {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        return navBackStackEntry?.destination?.route
    }
