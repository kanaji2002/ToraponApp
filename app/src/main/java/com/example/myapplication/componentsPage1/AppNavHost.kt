package com.example.myapplication.componentsPage1

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
//import findContainingRegion
//import findNearestRegion
//import interpolateXY
import kotlinx.coroutines.delay

// 観光スポット一覧ページと詳細ページの設定
@Composable
fun Page1AppNavHost(navController: NavHostController, backgroundColor: Color, selectionBackgroundColor: Color) {
    val context = LocalContext.current
    val fusedLocationClient =
        remember { LocationServices.getFusedLocationProviderClient(context) }
    var locationText by remember { mutableStateOf("位置情報未取得") }
    val permissionGranted = remember {
        mutableStateOf(
            ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }


    // **MutableState の変更点**
    val currentX = remember { mutableStateOf(1280f) }
    val currentY = remember { mutableStateOf(720f) }

    // 位置情報の権限をリクエスト
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        permissionGranted.value = isGranted
    }

    LaunchedEffect(Unit) {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
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
                val region = findContainingRegion(latitude, longitude) ?: findNearestRegion(
                    latitude,
                    longitude
                )

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



    LaunchedEffect(permissionGranted.value) {
        while (permissionGranted.value) {
            fetchLocation(fusedLocationClient) { newText, newX, newY ->
                locationText = newText
                currentX.value = newX
                currentY.value = newY
            }
            delay(5000) // 10秒ごとに更新
        }
    }


    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        //  1. 背景画像（マップ）
        Image(
            painter = painterResource(id = R.drawable.map2), // マップ画像
            contentDescription = "マップ画像",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )



        //  2. `Canvas` を `Box` の最上位レイヤーに配置
        val colorState = remember { mutableStateOf(0) }

        val animatedColor by animateColorAsState(
            targetValue = when (colorState.value) {
                0 -> Color.Red   // 青
                1 -> Color.Red  // 白
                2 -> Color.White
                else -> Color.White// 水色
            },
            animationSpec = tween(durationMillis = 700), // 500ms で色を変化
            label = "Blinking Animation"
        )

        // 🔹 500ms ごとに `colorState` を 0 → 1 → 2 → 0 ... とループさせる
        LaunchedEffect(Unit) {
            while (true) {
                delay(200) // 0.5秒ごとに色を変更
                colorState.value = (colorState.value + 1) % 4 // 0 → 1 → 2 → 0...
            }
        }

        Canvas(modifier = Modifier.matchParentSize()) {
            Log.d("Canvas", "描画処理実行: X=${currentX.value}, Y=${currentY.value}")


            // **外枠の白い円**
            drawCircle(
                color = Color.White, // 外枠の色
                radius = 20f, // 内部の円より少し大きく
                center = androidx.compose.ui.geometry.Offset(currentX.value, currentY.value),
                style = Stroke(width = 5f) // 4px の枠線
            )

            // **塗りつぶしの円（アニメーションカラー）**
            drawCircle(
                color = animatedColor,
                radius = 20f, // 内側の円
                center = androidx.compose.ui.geometry.Offset(currentX.value, currentY.value)
            )

        }


        //  3. 画面上の情報表示
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
//            Text(text = "現在地情報", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(20.dp))
//            Text(
//                text = locationText,
//                color = MaterialTheme.colorScheme.primary,
//                style = MaterialTheme.typography.bodyLarge
//            )
            Spacer(modifier = Modifier.height(20.dp))


        }
    }
}