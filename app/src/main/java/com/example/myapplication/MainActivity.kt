package com.example.myapplication
import android.Manifest
import android.content.pm.PackageManager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
//Osaka import(追加)
import androidx.compose.ui.graphics.Color
import com.example.myapplication.componentsPage2.Page2AppNavHost // AppNavHost.ktからインポート
import com.example.myapplication.componentsPage1.Page1AppNavHost // AppNavHost.ktからインポート

//Osaka import(追加)

import androidx.compose.ui.layout.ContentScale

import android.os.Build
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.LocationServices
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.delay


data class Edge(val to:String, val weight: Int)

// マップの頂点データ（緯度, 経度, X座標, Y座標）
data class MapVertex(val lat: Double, val lon: Double, val x: Float, val y: Float)

// ----------- MainActivity クラスを追加 -----------
class MainActivity : ComponentActivity() {


    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 位置情報の権限をリクエスト
        requestLocationPermission()
        setContent {
            MyTabletApp()
        }
    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }
    private fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let {
                it.hide(WindowInsets.Type.systemBars()) // ステータスバー & ナビゲーションバーを隠す
                it.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
        }
    }

    private fun requestLocationPermission() {
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val allGranted = permissions.all { it.value }
            if (!allGranted) {
                println("位置情報の権限が拒否されました")
            }
        }

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSIONS)
        }
    }

    private fun allPermissionsGranted(): Boolean {
        return REQUIRED_PERMISSIONS.all { permission ->
            ActivityCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED
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
        Text("Page5 Screen")
    }

    // ---------- 5. 現在のルートを取得するヘルパー ----------
    @Composable
    fun currentRoute(navController: NavHostController): String? {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        return navBackStackEntry?.destination?.route
    }


}




