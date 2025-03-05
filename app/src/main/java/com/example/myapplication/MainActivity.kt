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
import androidx.navigation.NavController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.horizontalScroll
import coil.compose.rememberImagePainter
import androidx.compose.material3.Text
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import java.io.BufferedReader
import java.io.InputStreamReader

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

//幅いっぱいに直線を引く
@Composable
fun FullWidthDivider() {
    Divider(
        color = Color.Gray,  // 線の色
        thickness = 2.dp,    // 線の太さ
        modifier = Modifier.fillMaxWidth()
    )
}

// assets フォルダ内の指定した txt ファイルを読み取る関数
fun readTextFileFromAssets(context: Context, filePath: String): String {
    return try {
        val inputStream = context.assets.open(filePath)  // ファイルを開く
        val reader = BufferedReader(InputStreamReader(inputStream))
        val text = reader.readText()  // 内容を読み取る
        reader.close()
        text  // 読み取った文字列を返す
    } catch (e: Exception) {
        "Error reading $filePath: ${e.message}"  // エラー時のメッセージ
    }
}


//選択画面
@Composable
fun Spot_selection_screen(navController: NavController, backcolor: Color, folderName: String) {
    Row (
        modifier = Modifier
            .height(300.dp)
            .fillMaxSize()
            .clip(RoundedCornerShape(25.dp))
            .background(color = backcolor)
    ){
        //スポットの画像
        Image(
            painter = rememberAsyncImagePainter("${folderName}/top_fig.png"),
            contentDescription = null,
            modifier = Modifier
                .size(300.dp, 300.dp),// 幅、高さ
            contentScale = ContentScale.Crop
        )
        //スポット名・ボタン・スポット説明文
        Column(
            modifier = Modifier.padding(all = 30.dp)
        ){
            //スポット名
            val context = LocalContext.current
            val textContent = readTextFileFromAssets(context, "${folderName}/name.txt")

            Text(
                text = textContent,//修正必用
                style = TextStyle(
                    fontSize = 35.sp,
                    color = Color.Black
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            //ボタン群
            Row{
                // 詳細ボタン
                Button(
                    onClick = {
                        navController.navigate("Detail")
                        //val folderName = "フォルダ"
                        //navController.navigate("Detail/$folderName")
                    },
                    modifier = Modifier
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Gray,
                        contentColor = Color.Black
                    )
                ) {
                    Text(text = "　詳細　", fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.width(16.dp))

                // 経路ボタン
                Button(
                    onClick = { /* ボタンの処理 */ },
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
                    text = "森でーす",//修正必用
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

@Composable
fun Page2ScreenList(navController: NavController, maincolor: Color, subcolor: Color) {
    //一覧画面
    Column(
        modifier = Modifier
            .fillMaxSize()
            //.clip(RoundedCornerShape(10.dp))
            .background(color = maincolor)
            .padding(top = 30.dp, start = 30.dp, end = 30.dp) // 上・左・右のみ余白
    ) {
        Text(text = " 観光スポット一覧",
            fontSize = 50.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Green.copy(alpha = 0.2f))
        )
        Spacer(modifier = Modifier.height(15.dp))

        //スポット一覧
        Column(modifier = Modifier.verticalScroll(rememberScrollState())){
            //Spot_selection_screen(navController, subcolor, R.drawable.spot3,"スポット名1","森でーす")
            Spot_selection_screen(navController, subcolor, "file:///android_asset/spot1_folder")
            Spot_selection_screen(navController, subcolor, "file:///android_asset/spot2_folder")
        }
    }
}

@Composable
fun Page2ScreenDetail(navController: NavController, backcolor: Color){//, folderName: String){
    //ボタン群
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 24.dp, end = 24.dp),
        horizontalArrangement = Arrangement.End,
    ){
        // 戻るボタン
        Button(
            onClick = { navController.navigate("list") }, // 戻る処理
            modifier = Modifier
                .height(50.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Yellow,
                contentColor = Color.Black)
        ){
            Text(text = "　戻る　", fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.width(16.dp))
        // 経路ボタン
        Button(
            onClick = { /* ボタンの処理 */ },
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

    Column(
        modifier = Modifier
            .background(color = backcolor)
            .fillMaxSize()
            .padding(top = 30.dp, start = 30.dp, end = 30.dp)
    ) {
        Text(text = "スポット詳細",
            fontWeight = FontWeight.Bold,
            fontSize = 50.sp,)

        Spacer(modifier = Modifier.height(15.dp))

        //詳細説明(スクロール)
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                //.background(color = backcolor)
                .fillMaxSize()
                .padding(top = 16.dp, start = 24.dp, end = 24.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFFFA500).copy(alpha = 0.5f))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                //左端
                Text(
                    text = "短い説明文",
                    fontSize = 40.sp,
                    modifier = Modifier.weight(3f) // 左側に寄せる
                )

                //右端
                Text(
                    text = "ここから徒歩●分",
                    fontSize = 35.sp,
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(modifier = Modifier.height(15.dp))

            //画像(横スクロール)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    //.clip(RoundedCornerShape(20.dp))
                    .background(Color.LightGray.copy(alpha = 0.5f))
                    .horizontalScroll(rememberScrollState())
                    //.padding(top = 24.dp, bottom = 24.dp,start = 24.dp, end = 24.dp),
                    .padding(all = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
                //.verticalScroll(rememberScrollState())
            ){
                Image(
                    painter = painterResource(id = R.drawable.spot3),
                    contentDescription = "スポット_top",
                    modifier = Modifier // 幅、高さ
                        .size(300.dp, 300.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(15.dp))
                Image(
                    painter = painterResource(id = R.drawable.spot4),
                    contentDescription = null,//"スポット_top",
                    modifier = Modifier // 幅、高さ
                        .size(300.dp, 300.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(15.dp))
                Image(
                    painter = painterResource(id = R.drawable.spot1),
                    contentDescription = null,//"スポット_top",
                    modifier = Modifier // 幅、高さ
                        .size(300.dp, 300.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(15.dp))
                Image(
                    painter = painterResource(id = R.drawable.spot2),
                    contentDescription = null,//"スポット_top",
                    modifier = Modifier // 幅、高さ
                        .size(300.dp, 300.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(15.dp))
                Image(
                    painter = painterResource(id = R.drawable.spot3),
                    contentDescription = null,//"スポット_top",
                    modifier = Modifier // 幅、高さ
                        .size(300.dp, 300.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.height(25.dp))
            //説明文
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(50.dp))
                    .background(color = Color.White)
                    .padding(top = 30.dp, bottom = 30.dp,start = 50.dp, end = 50.dp)
            ){
                //説明文＋画像
                Row{
                    //説明文
                    Column(
                        modifier = Modifier
                            .weight(1f) // Row の左半分を占める
                            //.fillMaxHeight()
                            .background(Color.White), // 背景色を付ける（確認用）
                        verticalArrangement = Arrangement.Center // 縦方向の中央揃え
                    ) {
                        Text(text = "タイトル", fontSize = 35.sp, color = Color.Black)
                        Text(text = "説明文ああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああ", fontSize = 20.sp, color = Color.DarkGray)
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    Image(
                        painter = painterResource(id = R.drawable.spot3),
                        contentDescription = null,//"スポット_top",
                        modifier = Modifier // 幅、高さ
                            .clip(RoundedCornerShape(20.dp))
                        //.size(300.dp, 300.dp)
                        //contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(30.dp))

                Row{
                    //説明文
                    Column(
                        modifier = Modifier
                            .weight(1f) // Row の左半分を占める
                            //.fillMaxHeight()
                            .background(Color.White), // 背景色を付ける（確認用）
                        verticalArrangement = Arrangement.Center // 縦方向の中央揃え
                    ) {
                        Text(text = "タイトル2", fontSize = 35.sp, color = Color.Black)
                        Text(text = "説明文2あああああああ", fontSize = 20.sp, color = Color.DarkGray)
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    Image(
                        painter = painterResource(id = R.drawable.spot4),
                        contentDescription = null,//"スポット_top",
                        modifier = Modifier // 幅、高さ
                            .clip(RoundedCornerShape(20.dp))
                        //.size(300.dp, 300.dp)
                        //contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(30.dp))

                //説明文のみ
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFFFA500).copy(alpha = 0.5f))
                        .padding(all = 25.dp),
                    verticalArrangement = Arrangement.Center // 縦方向の中央揃え
                ) {
                    Text(text = "歴史", fontSize = 35.sp, color = Color.Black)
                    Text(text = "説明文", fontSize = 20.sp, color = Color.DarkGray)
                }
            }

        }
    }

}

@Composable
fun Page2Screen() {
    //設定
    val backgroundcolor = Color.Yellow.copy(alpha = 0.1f)//一覧画面背景色
    val selectionbackgroundcolor = Color.LightGray.copy(alpha = 0.5f)//選択画面背景色
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "list"){ // 一覧ページから開始
        composable("list") { Page2ScreenList(navController, backgroundcolor, selectionbackgroundcolor) } // 一覧ページに`navController`を渡す
        composable("Detail") { Page2ScreenDetail(navController, backgroundcolor) } // 詳細ページに`navController`を渡す
    }
}
// ===================================================================================

@Composable
fun Page3Screen() {
    Text("Page3 Screen")

    Image(
        painter = rememberImagePainter("file:///android_asset/spot1_folder/spot1.png"),
        contentDescription = null
    )
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
