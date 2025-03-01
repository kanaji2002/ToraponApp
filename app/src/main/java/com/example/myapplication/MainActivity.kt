package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
//Osaka import(追加)
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.width
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.fillMaxWidth


// ----------- MainActivity クラスを追加 -----------
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTabletApp()
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
@Composable
fun Page1Screen() {
    Text("Page1 Screen")
}

// スポット一覧(担当:Osaka)==============================================================
@Composable
fun Page2Screen() {
    //設定
    val backgroundcolor = Color.Yellow.copy(alpha = 0.1f)//画面背景色
    val selectionbackgroundcolor = Color.LightGray.copy(alpha = 0.5f)//選択画面背景色

    //関数一覧
    //直線を引く
    @Composable
    fun FullWidthDivider() {
        Divider(
            color = Color.Gray,  // 線の色
            thickness = 2.dp,    // 線の太さ
            modifier = Modifier.fillMaxWidth() // 幅いっぱい
        )
    }

    //選択画面
    @Composable
    fun Spot_selection_screen(figid: Int,spotname: String,explanation: String) {
        Row (
            //verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(300.dp)
                .fillMaxSize()
                .clip(RoundedCornerShape(25.dp))
                .background(color = selectionbackgroundcolor)
            //.padding(all = 30.dp)
        ){
            //スポットの画像
            Image(
                painter = painterResource(id = figid),
                contentDescription = "${spotname}_top",
                modifier = Modifier // 幅、高さ
                    .size(300.dp, 300.dp),
                contentScale = ContentScale.Crop
            )
            //スポット名・ボタン・スポット説明文
            Column(
                modifier = Modifier
                    //.fillMaxSize()
                    //.clip(RoundedCornerShape(25.dp))
                    //.background(color = selection_back_ground_color)
                    .padding(all = 30.dp)
            ){
                //スポット名
                Text(
                    text = spotname,
                    style = TextStyle(
                        fontSize = 35.sp,
                        color = Color.Black
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                //ボタン
                Row(){
                    // 詳細ボタン
                    Button(
                        onClick = { /* ボタン1の処理 */ },
                        modifier = Modifier
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Blue,
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "　詳細　", fontSize = 20.sp)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // 経路ボタン
                    Button(
                        onClick = { /* ボタン2の処理 */ },
                        modifier = Modifier
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,
                            contentColor = Color.Black
                        )
                    ) {
                        Text(text = "　経路　", fontSize = 20.sp)
                    }
                }
                //横線
                Spacer(modifier = Modifier.height(10.dp))
                FullWidthDivider()

                //スポット説明文(スクロール可)
                Column(modifier = Modifier.verticalScroll(rememberScrollState())){
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = explanation,
                        style = TextStyle(
                            fontSize = 20.sp,
                            color = Color.Black
                        )
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
    }

    //一覧画面(Main)
    Column(
        //horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            //.clip(RoundedCornerShape(10.dp))
            .background(color = backgroundcolor)
            .padding(all = 30.dp)
        //.verticalScroll(rememberScrollState())
    ) {
        Text(text = "スポット一覧",
            style = TextStyle(
                fontSize = 50.sp,
                color = Color.Black
            )
        )
        Spacer(modifier = Modifier.height(30.dp))

        //スポット一覧
        Column(modifier = Modifier.verticalScroll(rememberScrollState())){
            Spot_selection_screen(R.drawable.spot1,
                "スポット名1",
                "スポットAは有名な場所です。\nとにかく有名です。すごく有名です。\nかつてない程有名です。とっっっっっっっっっっっっっっっっっっにかく有名です。なんなんだよお前は！君も随分と馬鹿げた力の持ち主みたいだけど、頭が残念じゃ宝の持ち腐れってやつだよね？")
            Spot_selection_screen(R.drawable.spot2,"スポット名2","森でーす")
            Spot_selection_screen(R.drawable.spot1,"スポット名3","森でーす")
            Spot_selection_screen(R.drawable.spot2,"スポット名4","森でーす")
            Spot_selection_screen(R.drawable.spot1,"スポット名5","森でーす")
            Spot_selection_screen(R.drawable.spot2,"スポット名6","森でーす")
        }
    }
}
// ===================================================================================

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
