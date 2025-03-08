package com.example.myapplication


//地図
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import com.google.android.gms.location.FusedLocationProviderClient
import kotlin.math.pow
import kotlin.math.sqrt

data class Edge(val to:String, val weight: Int)

// マップの頂点データ（緯度, 経度, X座標, Y座標）
data class MapVertex(val lat: Double, val lon: Double, val x: Float, val y: Float)
// マップの領域データ
//val mapRegions = listOf(
//    listOf(
//        MapVertex(34.3290, 134.0435, 673f, 720f), // A（南西）
//        MapVertex(34.3290, 134.0450, 1280f, 720f), // B（南東）
//        MapVertex(34.3310, 134.0435, 673f, 350f), // C（北西）
//        MapVertex(34.3310, 134.0450, 1280f, 350f)  // D（北東）
//    )
//)

val mapRegions = listOf(
    listOf(
        MapVertex(34.32809341019379, 134.04192367609065, 0f, 0f), // A（南西）
        MapVertex(34.326880679260256, 134.04433338991794, 0f, 720f), // B（南東）
        MapVertex(34.332735039918816, 134.04281425912956, 1280f, 0f), // C（北西）
        MapVertex(34.33230692874956, 134.0465035767585, 1280f, 720f)  // D（北東）
    )
)


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
            ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
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

// ---------- 4. 5つのページ (サンプル) ----------

    //aStar() を関数化してcomponent_page1/page1ktに移した．

// ===================================================================================

@Composable
fun Page2Screen() {
    Text("Page2 Screen")
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
    Text("Page5 Screeeeen")
}

// ---------- 5. 現在のルートを取得するヘルパー ----------
@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}



fun interpolateXY(lat: Double, lon: Double, mapVertices: List<MapVertex>): Pair<Float, Float> {
    val minLat = mapVertices.minOf { it.lat }
    val maxLat = mapVertices.maxOf { it.lat }
    val minLon = mapVertices.minOf { it.lon }
    val maxLon = mapVertices.maxOf { it.lon }

    val minX = mapVertices.minOf { it.x }
    val maxX = mapVertices.maxOf { it.x }
    val minY = mapVertices.minOf { it.y }
    val maxY = mapVertices.maxOf { it.y }

    val x = minX + ((lat - minLat) / (maxLat - minLat)) * (maxX - minX)
    val y = minY + ((lon - minLon) / (maxLon - minLon)) * (maxY - minY)

    return Pair(x.toFloat(), y.toFloat())
}


// 地域を特定する関数（MapVertexリストを返す）
fun findContainingRegion(lat: Double, lon: Double): List<MapVertex>? {
    for (region in mapRegions) {
        val latitudes = region.map { it.lat }
        val longitudes = region.map { it.lon }

        val minLat = latitudes.minOrNull() ?: continue
        val maxLat = latitudes.maxOrNull() ?: continue
        val minLon = longitudes.minOrNull() ?: continue
        val maxLon = longitudes.maxOrNull() ?: continue

        if (lat in minLat..maxLat && lon in minLon..maxLon) {
            return region
        }
    }
    return null
}


fun findNearestRegion(lat: Double, lon: Double): List<MapVertex> {
    return mapRegions.minByOrNull { region ->
        region.minOf { vertex ->
            sqrt((vertex.lat - lat).pow(2) + (vertex.lon - lon).pow(2))
        }
    } ?: mapRegions.first()
}



// `Page1Screen` の実装
@Composable
fun Page1Screen() {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var locationText by remember { mutableStateOf("位置情報未取得") }
    var permissionGranted by remember { mutableStateOf(false) }

    // **MutableState の変更点**
    val currentX = remember { mutableStateOf(1280f) }
    val currentY = remember { mutableStateOf(720f) }

    // 位置情報の権限をリクエスト
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        permissionGranted = isGranted
    }

    // **LaunchedEffect で currentPosition の変更をログに出力**
    LaunchedEffect(currentX.value, currentY.value) {
        Log.d("Canvas", "更新された位置: X=${currentX.value}, Y=${currentY.value}")
    }

    // 位置情報を取得する関数
    fun fetchLocation(
        fusedLocationClient: FusedLocationProviderClient,
        onLocationReceived: (String, Float, Float) -> Unit
    ) {
        fusedLocationClient.getCurrentLocation(
            com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, null
        ).addOnSuccessListener { location ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                val region = findContainingRegion(latitude, longitude) ?: findNearestRegion(latitude, longitude)

                // ログ出力
                Log.d("Location", "緯度: $latitude, 経度: $longitude")

                // マップ座標に変換

                val (newX, newY) = region.let { interpolateXY(latitude, longitude, it) }
                    ?: Pair(450f, 450f)  // 万が一エラーが出た場合のデフォルト値

                // UIの状態を更新
                val newText = "緯度: $latitude, 経度: $longitude"
                onLocationReceived(newText, newX, newY)
            } else {
                Log.e("Location", "位置情報を取得できませんでした")
                onLocationReceived("位置情報を取得できません", 450f, 450f)
            }
        }.addOnFailureListener {
            Log.e("Location", "エラー発生: ${it.message}")
            onLocationReceived("位置情報の取得に失敗しました", 450f, 450f)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 📌 1. 背景画像（マップ）
        Image(
            painter = painterResource(id = R.drawable.map2), // マップ画像
            contentDescription = "マップ画像",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 📌 2. `Canvas` を `Box` の最上位レイヤーに配置
        Canvas(modifier = Modifier.matchParentSize()) {
            Log.d("Canvas", "描画処理実行: X=${currentX.value}, Y=${currentY.value}")

            drawCircle(
                color = Color.Red,
                radius = 20f,
                center = androidx.compose.ui.geometry.Offset(currentX.value, currentY.value)
            )
        }

        // 📌 3. 画面上の情報表示
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "現在地情報", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = locationText,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(20.dp))

            // 📌 4. 位置情報取得ボタン
            Button(onClick = {
                Log.d("ButtonClick", "ボタンが押されました - permissionGranted: $permissionGranted")
                if (permissionGranted) {
                    fetchLocation(fusedLocationClient) { newText, newX, newY ->
                        Log.d("LocationUpdate", "画面に反映: $newText")
                        locationText = newText

                        // 📌 `MutableState` の中身を更新
                        currentX.value = newX
                        currentY.value = newY
                        Log.d("Canvas", "更新された位置: X=${currentX.value}, Y=${currentY.value}")
                    }
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }) {
                Text("現在地を取得")
            }
        }
    }
}
