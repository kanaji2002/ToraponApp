package com.example.myapplication.componentsPage1
import androidx.compose.ui.zIndex

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
//import androidx.compose.ui.unit.Dp
import androidx.compose.ui.platform.LocalDensity
// 🔵 左上の戻るボタン
import androidx.compose.material3.OutlinedButton
import androidx.compose.foundation.BorderStroke


import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
//import findContainingRegion
//import findNearestRegion
//import interpolateXY
import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.draw.clip

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sqrt
import kotlin.math.pow

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


    val selectedSpotId = remember { mutableStateOf<String?>(null) }
    val pictSelectedSpotId = remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier.fillMaxSize()
        .clickable {
            // 🔻 マップをタップしたらポップアップもピクトも非選択にする
            selectedSpotId.value = null
            pictSelectedSpotId.value = null
        }


    ) {


        // 1. 背景画像（マップ）
        Image(
            painter = painterResource(id = R.drawable.map2),
            contentDescription = "マップ画像",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        TopRightIconMenu(


                pictSelectedSpotId = pictSelectedSpotId.value,
                onPictSelect = { pictSelectedSpotId.value = it },
                selectedSpotId = selectedSpotId
            )






                OutlinedButton(
                    onClick = {
                        selectedSpotId.value = if (selectedSpotId.value == "HIDE_ALL") null else "HIDE_ALL"
                        pictSelectedSpotId.value = null
                    },
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopStart)
                        .height(55.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color(0xFFC9C9C9),
                        contentColor = Color.Black
                    ),
                    border = BorderStroke(2.dp, Color.Black)
                ) {
                    Text(
                        text = if (selectedSpotId.value == "HIDE_ALL") " ON " else "OFF",
                        fontSize = 23.sp
                    )
                }







        // ポップアップ関数のスタート
        //1255*700がマップの最大エリア(左上(0,0)スタートの右X，下Yが正）
        SpotMarkerWithPopup(
            spotId = "spot1",
            selectedSpotId = selectedSpotId.value,
            onSelect = { selectedSpotId.value = it },
            buttonOffsetX = 767.dp,
            buttonOffsetY = 544.dp,
            popupOffsetX = -300.dp,
            popupOffsetY = (0).dp,
            pictureResId = R.drawable.spot1,
            descriptionTitle = "讃岐民芸館",
            descriptionKana = "さぬきみんげいかん",
            descriptionBody = "明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。",
            onRouteClick = { /* 経路案内 */ }
        )

        SpotMarkerWithPopup(
            spotId = "spot2",
            selectedSpotId = selectedSpotId.value,
            onSelect = { selectedSpotId.value = it },
            buttonOffsetX = 785.dp,
            buttonOffsetY = 446.dp,
            popupOffsetX = -300.dp,
            popupOffsetY = (0).dp,
            pictureResId = R.drawable.spot2,
            descriptionTitle = "商工奨励館",
            descriptionKana = "しょうこうしょうれいかん",
            descriptionBody = "明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。",
            onRouteClick = { /* 経路案内 */ }
        )

        SpotMarkerWithPopup(
            spotId = "spot3",
            selectedSpotId = selectedSpotId.value,
            onSelect = { selectedSpotId.value = it },
            buttonOffsetX = 697.dp,
            buttonOffsetY = 369.dp,
            popupOffsetX = -300.dp,
            popupOffsetY = (0).dp,
            pictureResId = R.drawable.spot3,
            descriptionTitle = "お手植松",
            descriptionKana = "おてうえまつ",
            descriptionBody = "明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。",
            onRouteClick = { /* 経路案内 */ }
        )

        SpotMarkerWithPopup(
            spotId = "spot4",
            selectedSpotId = selectedSpotId.value,
            onSelect = { selectedSpotId.value = it },
            buttonOffsetX = 654.dp,
            buttonOffsetY = 478.dp,
            popupOffsetX = 300.dp,
            popupOffsetY = (0).dp,
            pictureResId = R.drawable.spot4,
            descriptionTitle = "鶴亀松",
            descriptionKana = "つるかめまつ",
            descriptionBody = "明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。",
            onRouteClick = { /* 経路案内 */ }
        )


        SpotMarkerWithPopup(
            spotId = "spot5",
            selectedSpotId = selectedSpotId.value,
            onSelect = { selectedSpotId.value = it },
            buttonOffsetX = 591.dp,
            buttonOffsetY = 413.dp,
            popupOffsetX = 300.dp,
            popupOffsetY = (0).dp,
            pictureResId = R.drawable.spot5,
            descriptionTitle = "箱松・屏風松",
            descriptionKana = "はこまつ・びょうぶまつ",
            descriptionBody = "明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。",
            onRouteClick = { /* 経路案内 */ }
        )

        SpotMarkerWithPopup(
            spotId = "spot6",
            selectedSpotId = selectedSpotId.value,
            onSelect = { selectedSpotId.value = it },
            buttonOffsetX = 608.dp,
            buttonOffsetY = 227.dp,
            popupOffsetX = 300.dp,
            popupOffsetY = (0).dp,
            pictureResId = R.drawable.spot6,
            descriptionTitle = "百花園（薬園）跡",
            descriptionKana = "ひゃっかえんあと",
            descriptionBody = "明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。",
            onRouteClick = { /* 経路案内 */ }
        )

        SpotMarkerWithPopup(
            spotId = "spot7",
            selectedSpotId = selectedSpotId.value,
            onSelect = { selectedSpotId.value = it },
            buttonOffsetX = 483.dp,
            buttonOffsetY = 304.dp,
            popupOffsetX = 300.dp,
            popupOffsetY = (0).dp,
            pictureResId = R.drawable.spot7,
            descriptionTitle = "日暮亭",
            descriptionKana = "ひぐらしてい",
            descriptionBody = "明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。",
            onRouteClick = { /* 経路案内 */ }
        )

        SpotMarkerWithPopup(
            spotId = "spot8",
            selectedSpotId = selectedSpotId.value,
            onSelect = { selectedSpotId.value = it },
            buttonOffsetX = 412.dp,
            buttonOffsetY = 155.dp,
            popupOffsetX = 300.dp,
            popupOffsetY = (0).dp,
            pictureResId = R.drawable.spot8,
            descriptionTitle = "旧日暮亭",
            descriptionKana = "きゅうひぐらしてい",
            descriptionBody = "明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。",
            onRouteClick = { /* 経路案内 */ }
        )

        SpotMarkerWithPopup(
            spotId = "spot9",
            selectedSpotId = selectedSpotId.value,
            onSelect = { selectedSpotId.value = it },
            buttonOffsetX = 225.dp,
            buttonOffsetY = 261.dp,
            popupOffsetX = 300.dp,
            popupOffsetY = (0).dp,
            pictureResId = R.drawable.spot9,
            descriptionTitle = "掬月亭",
            descriptionKana = "きくげつてい",
            descriptionBody = "明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。",
            onRouteClick = { /* 経路案内 */ }
        )

        SpotMarkerWithPopup(
            spotId = "spot10",
            selectedSpotId = selectedSpotId.value,
            onSelect = { selectedSpotId.value = it },
            buttonOffsetX = 214.dp,
            buttonOffsetY = 326.dp,
            popupOffsetX = 300.dp,
            popupOffsetY = (0).dp,
            pictureResId = R.drawable.spot10,
            descriptionTitle = "根上五葉松",
            descriptionKana = "ねあがりごようまつ",
            descriptionBody = "明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。",
            onRouteClick = { /* 経路案内 */ }
        )

        SpotMarkerWithPopup(
            spotId = "spot12",
            selectedSpotId = selectedSpotId.value,
            onSelect = { selectedSpotId.value = it },
            buttonOffsetX = 143.dp,
            buttonOffsetY = 505.dp,
            popupOffsetX = 300.dp,
            popupOffsetY = (0).dp,
            pictureResId = R.drawable.spot12,
            descriptionTitle = "偃月橋",
            descriptionKana = "えんげつきょう",
            descriptionBody = "明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。",
            onRouteClick = { /* 経路案内 */ }
        )

        SpotMarkerWithPopup(
            spotId = "spot13",
            selectedSpotId = selectedSpotId.value,
            onSelect = { selectedSpotId.value = it },
            buttonOffsetX = 87.dp,
            buttonOffsetY = 430.dp,
            popupOffsetX = 300.dp,
            popupOffsetY = (0).dp,
            pictureResId = R.drawable.spot13,
            descriptionTitle = "楓岸",
            descriptionKana = "ふうがん",
            descriptionBody = "明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。",
            onRouteClick = { /* 経路案内 */ }
        )

        SpotMarkerWithPopup(
            spotId = "spot14",
            selectedSpotId = selectedSpotId.value,
            onSelect = { selectedSpotId.value = it },
            buttonOffsetX = 112.dp,
            buttonOffsetY = 575.dp,
            popupOffsetX = 300.dp,
            popupOffsetY = (0).dp,
            pictureResId = R.drawable.spot14,
            descriptionTitle = "飛来峰",
            descriptionKana = "ひらいほう",
            descriptionBody = "明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。",
            onRouteClick = { /* 経路案内 */ }
        )

        SpotMarkerWithPopup(
            spotId = "spot15",
            selectedSpotId = selectedSpotId.value,
            onSelect = { selectedSpotId.value = it },
            buttonOffsetX = 467.dp,
            buttonOffsetY = 533.dp,
            popupOffsetX = 300.dp,
            popupOffsetY = (0).dp,
            pictureResId = R.drawable.spot15,
            descriptionTitle = "芙蓉峰",
            descriptionKana = "ふようほう",
            descriptionBody = "明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。",
            onRouteClick = { /* 経路案内 */ }
        )

        SpotMarkerWithPopup(
            spotId = "spot16",
            selectedSpotId = selectedSpotId.value,
            onSelect = { selectedSpotId.value = it },
            buttonOffsetX = 977.dp,
            buttonOffsetY = 509.dp,
            popupOffsetX = -300.dp,
            popupOffsetY = (0).dp,
            pictureResId = R.drawable.spot16,
            descriptionTitle = "花しょうぶ園",
            descriptionKana = "はなしょうぶえん",
            descriptionBody = "明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。",
            onRouteClick = { /* 経路案内 */ }
        )

        SpotMarkerWithPopup(
            spotId = "spot17",
            selectedSpotId = selectedSpotId.value,
            onSelect = { selectedSpotId.value = it },
            buttonOffsetX = 864.dp,
            buttonOffsetY = 592.dp,
            popupOffsetX = -300.dp,
            popupOffsetY = (0).dp,
            pictureResId = R.drawable.spot17,
            descriptionTitle = "鴨場 ",
            descriptionKana = "かもば",
            descriptionBody = "明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。明治32年に建築された建物で〜略〜楽しめます。",
            onRouteClick = { /* 経路案内 */ }
        )




        val pictgramSpots = listOf(
            Triple("toilet", R.drawable.toilet, 870.dp to 500.dp),
            Triple("toilet", R.drawable.toilet, 270.dp to 50.dp),
            Triple("tabako", R.drawable.tabako, 500.dp to 200.dp), // 必要なら追加
            // 他のアイコンも追加可能
        )

        pictgramSpots.forEach { (id, resId, position) ->
            if (pictSelectedSpotId.value == id) {
                pictgramBox(
                    pictSpotId = id,
                    pictSelectedSpotId = pictSelectedSpotId.value,
                    onPictSelect = { pictSelectedSpotId.value = it },
                    iconNamePath = resId,
                    offsetX = position.first,
                    offsetY = position.second,
                    size = 52.dp
                )
            }
        }







    }





    fun calculateDpDistance(
        currentX: Float,
        currentY: Float,
        targetX: Dp,
        targetY: Dp
    ): Dp {
        val dx = currentX - targetX.value
        val dy = currentY - targetY.value
        val distancePx = sqrt(dx.pow(2) + dy.pow(2))
        return distancePx.dp
    }





    //  2. `Canvas` を `Box` の最上位レイヤーに配置
        val colorState = remember { mutableStateOf(0) }

        val animatedColor by animateColorAsState(
            targetValue = when (colorState.value) {
                0 -> Color.Red
                1 -> Color.Red
                2 -> Color.White
                else -> Color.White
            },
            animationSpec = tween(durationMillis = 1400), // 500ms で色を変化
            label = "Blinking Animation"
        )

        // 🔹 500ms ごとに `colorState` を 0 → 1 → 2 → 0 ... とループさせる
        LaunchedEffect(Unit) {
            while (true) {
                delay(350) // 0.5秒ごとに色を変更
                colorState.value = (colorState.value + 1) % 4 // 0 → 1 → 2 → 0...
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
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

            Spacer(modifier = Modifier.height(20.dp))


        }

    }

@Composable
fun TopRightIconMenu(
    pictSelectedSpotId: String?,
    onPictSelect: (String?) -> Unit,
    selectedSpotId: MutableState<String?> // ← 🔥 追加：観光スポットボタンの表示制御に使う
) {
    val pictTypes = listOf(
        "toilet" to R.drawable.toilet,
        "tabako" to R.drawable.tabako,
        "aed" to R.drawable.aed,
        "fork" to R.drawable.fork,
        "other" to R.drawable.other
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        Row(
            modifier = Modifier
                .background(Color.White, shape = RoundedCornerShape(12.dp))
                .padding(horizontal = 15.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            pictTypes.forEach { (id, iconResId) ->
                val isSelected = pictSelectedSpotId == id
                IconButton(
                    onClick = {
                        val nextId = if (isSelected) null else id
                        onPictSelect(nextId)

                        // 🔽 観光スポットボタンを非表示にする or 再表示する
                        selectedSpotId.value = if (nextId != null) "HIDE_ALL" else null
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            if (isSelected) Color(0xFF1E90FF) else Color(0xFF87CEFA),
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Image(
                        painter = painterResource(id = iconResId),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}



@Composable
fun SpotInfoPopup(
    pictureResId: Int,
    offsetX: Dp,
    offsetY: Dp,
    descriptionTitle: String,
    descriptionKana: String,
    descriptionBody: String,
    onClose: () -> Unit,
    onRouteClick: () -> Unit
) {
    val density = LocalDensity.current
    val popupOffset = with(density) {
        IntOffset(offsetX.roundToPx(), offsetY.roundToPx())
    }

    Popup(
        alignment = Alignment.Center,
        offset = popupOffset,
        onDismissRequest = onClose
    ) {
        Box {
            // バツボタン
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 16.dp, y = (-16).dp)
                    .zIndex(2f)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.batsu),
                    contentDescription = "閉じる",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFFF88))
                        .clickable { onClose() }
                )
            }

            // ポップアップ内容
            Box(
                modifier = Modifier
                    .width(400.dp)
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .border(1.dp, Color.Gray, shape = RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = pictureResId),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(276.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                        Text(descriptionKana, style = MaterialTheme.typography.bodyMedium, color = Color.Black)
                        Text(descriptionTitle, style = MaterialTheme.typography.titleLarge)
                        Text(descriptionBody, style = MaterialTheme.typography.bodyMedium)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onRouteClick,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .width(100.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text("経路")
                    }
                }
            }
        }
    }
}

@Composable
fun MapCircleButton(
    offsetX: Dp,
    offsetY: Dp,
    size: Dp = 44.dp,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "blueGlow")

    val colorState2 = remember { mutableStateOf(0) }
    val glowColor by animateColorAsState(
        targetValue = when (colorState2.value) {
            0 -> Color(0xFF30A7FF)
            1 -> Color(0xFF30A7FF)
            2 -> Color.White.copy(alpha = 0.6f)
            else -> Color.White.copy(alpha = 0.1f)
        },
        animationSpec = tween(durationMillis = 1400),
        label = "AnimatedGlowColor"
    )

    LaunchedEffect(Unit) {
        while (true) {
            delay(350)
            colorState2.value = (colorState2.value + 1) % 4
        }
    }

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "GlowAlpha"
    )

    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "GlowScale"
    )

    Box(
        modifier = Modifier
            .offset(x = offsetX, y = offsetY)
            .size(size),
        contentAlignment = Alignment.Center
    ) {
        // グローエフェクト
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = glowScale
                    scaleY = glowScale
                    alpha = glowAlpha
                }
        ) {
            drawCircle(
                color = glowColor,
                radius = size.toPx() / 2f,
                center = center
            )
        }

        // 外枠（白い固定円）
        Canvas(modifier = Modifier.size(size + 4.dp)) {
            drawCircle(
                color = Color.White,
                radius = (size.toPx() + 4.dp.toPx()) / 2f,
                center = center,
                style = Stroke(width = 4f)
            )
        }

        // 中心のクリック可能な丸（色：アニメーション）
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(glowColor)
                .clickable { onClick() }
        )
    }
}




@Composable
fun SpotMarkerWithPopup(
    spotId: String,
    selectedSpotId: String?,
    onSelect: (String?) -> Unit,
    buttonOffsetX: Dp,
    buttonOffsetY: Dp,
    popupOffsetX: Dp = 0.dp,
    popupOffsetY: Dp = 0.dp,
    pictureResId: Int,
    descriptionTitle: String,
    descriptionKana: String,
    descriptionBody: String,
    onRouteClick: () -> Unit
) {

   //ポップアップの表示のBoolean
    val isPopupVisible = selectedSpotId == spotId

    //ボタンの一括，または個別管理ができる．Boolean
    val shouldShowButton = selectedSpotId == null || selectedSpotId == spotId || selectedSpotId == "ALL"

// 🔕 すべて非表示の指示が出ている場合は return（何も描画しない）
    if (selectedSpotId == "HIDE_ALL") return


    // 🔵 ボタンは「何も選ばれていない」or「自分自身が選ばれている」ときに表示
    if (shouldShowButton) {
        MapCircleButton(
            offsetX = buttonOffsetX,
            offsetY = buttonOffsetY,
            onClick = {
                if (!isPopupVisible) {
                    onSelect(spotId) // 自分が選ばれてないなら選択
                }
            }
        )
    }


    // 🟡 ポップアップ表示
    if (isPopupVisible) {
        SpotInfoPopup(
            pictureResId = pictureResId,
            offsetX = popupOffsetX,
            offsetY = popupOffsetY,
            descriptionTitle = descriptionTitle,
            descriptionKana = descriptionKana,
            descriptionBody = descriptionBody,
            onClose = { onSelect(null) }, // 閉じたら全ボタンが復活
            onRouteClick = onRouteClick
        )
    }
}



@Composable
fun pictgramBox(
    pictSpotId: String,
    pictSelectedSpotId: String?,
    onPictSelect: (String?) -> Unit,
    iconNamePath: Int,
    offsetX: Dp,
    offsetY: Dp,
    size: Dp
) {
    // 表示するかどうか
    val shouldShow = pictSelectedSpotId != "HIDE_ALL" &&
            (pictSelectedSpotId == null || pictSelectedSpotId == pictSpotId || pictSelectedSpotId == "ALL")

    if (!shouldShow) return

    val isSelected = pictSelectedSpotId == pictSpotId

    Box(
        modifier = Modifier
            .offset(x = offsetX, y = offsetY)
            .size(size)
            .clip(RoundedCornerShape(8.dp))
            .background(
                    Color(0xFF87CEFA)
            )
            .border(3.dp, Color.Red, shape = RoundedCornerShape(8.dp)) // 赤い外枠
            .clickable {
                onPictSelect(pictSpotId)
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = iconNamePath),
            contentDescription = null,
            modifier = Modifier.size(size * 0.6f)
        )
    }
}
