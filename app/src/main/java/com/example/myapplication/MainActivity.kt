package com.example.myapplication


//地図
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
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

import androidx.compose.foundation.layout.width
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.navigation.compose.rememberNavController
import java.util.PriorityQueue
import kotlin.math.abs
data class Edge(val to:String, val weight: Int)

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



var start : String = "A"
var goal : String = "F"


fun bSplineInterpolation(points: List<Pair<Float, Float>>, degree: Int = 3, numPoints: Int = 50): List<Pair<Float, Float>> {
    val n = points.size - 1
    if (n < degree) return points // 制御点が少ない場合、スプラインを適用せずそのまま

    val knots = mutableListOf<Float>()
    val tMin = 0f
    val tMax = 1f
    val step = (tMax - tMin) / (numPoints - 1)

    for (i in 0 until n + degree + 1) {
        knots.add(i.toFloat() / (n + degree))
    }

    fun basis(i: Int, d: Int, t: Float): Float {
        if (d == 0) {
            return if (knots[i] <= t && t < knots[i + 1]) 1f else 0f
        }
        val a = if (knots[i + d] == knots[i]) 0f else (t - knots[i]) / (knots[i + d] - knots[i]) * basis(i, d - 1, t)
        val b = if (knots[i + d + 1] == knots[i + 1]) 0f else (knots[i + d + 1] - t) / (knots[i + d + 1] - knots[i + 1]) * basis(i + 1, d - 1, t)
        return a + b
    }

    val interpolatedPoints = mutableListOf<Pair<Float, Float>>()

    var t = tMin
    while (t <= tMax) {
        var x = 0f
        var y = 0f
        for (i in 0..n) {
            val weight = basis(i, degree, t)
            x += weight * points[i].first
            y += weight * points[i].second
        }
        interpolatedPoints.add(Pair(x, y))
        t += step // Float でも適用できるように手動で増やす
    }


    return interpolatedPoints
}

val graph = mapOf(
    "A" to listOf(Edge("B", 4), Edge("C", 2), Edge("F", 6)),
    "B" to listOf(Edge("A", 4), Edge("C", 5), Edge("D", 10), Edge("G", 8)),
    "C" to listOf(Edge("A", 2), Edge("B", 5), Edge("D", 3), Edge("E", 7)),
    "D" to listOf(Edge("H", 10), Edge("C", 3), Edge("E", 8), Edge("H", 6)),
    "E" to listOf(Edge("F", 7), Edge("D", 8), Edge("I", 4)),
    "F" to listOf(Edge("A", 6), Edge("G", 5), Edge("J", 9)),
    "G" to listOf(Edge("B", 8), Edge("F", 5), Edge("H", 4), Edge("K", 6)),
    "H" to listOf(Edge("D", 6), Edge("G", 4), Edge("I", 7), Edge("L", 5)),
//    "I" to listOf(Edge("E", 4), Edge("H", 7), Edge("M", 8)),
//    "J" to listOf(Edge("F", 9), Edge("K", 3), Edge("N", 10)),
//    "K" to listOf(Edge("G", 6), Edge("J", 3), Edge("L", 2), Edge("O", 7)),
//    "L" to listOf(Edge("H", 5), Edge("K", 2), Edge("M", 4), Edge("P", 9)),
//    "M" to listOf(Edge("I", 8), Edge("L", 4), Edge("Q", 5)),
//    "N" to listOf(Edge("J", 10), Edge("O", 6), Edge("R", 7)),
//    "O" to listOf(Edge("K", 7), Edge("N", 6), Edge("P", 3), Edge("S", 8)),
//    "P" to listOf(Edge("L", 9), Edge("O", 3), Edge("Q", 2), Edge("T", 7)),
//    "Q" to listOf(Edge("M", 5), Edge("P", 2), Edge("U", 6)),
//    "R" to listOf(Edge("N", 7), Edge("S", 4), Edge("V", 9)),
//    "S" to listOf(Edge("O", 8), Edge("R", 4), Edge("T", 3), Edge("W", 7)),
//    "T" to listOf(Edge("P", 7), Edge("S", 3), Edge("U", 4), Edge("X", 6)),
//    "U" to listOf(Edge("Q", 6), Edge("T", 4), Edge("Y", 5)),
//    "V" to listOf(Edge("R", 9), Edge("W", 6), Edge("Z", 10)),
//    "W" to listOf(Edge("S", 7), Edge("V", 6), Edge("X", 2)),
//    "X" to listOf(Edge("T", 6), Edge("W", 2), Edge("Y", 3)),
//    "Y" to listOf(Edge("U", 5), Edge("X", 3), Edge("Z", 8)),
//    "Z" to listOf(Edge("V", 10), Edge("Y", 8))
)


@Composable
fun Page1Screen() {


    var startLocation by remember { mutableStateOf("A") }
    var goalLocation by remember { mutableStateOf("G") }
    var path by remember { mutableStateOf<List<String>?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("現在地: $startLocation", fontSize = 20.sp)
        Text("目的地: $goalLocation", fontSize = 20.sp)

        Button(onClick = {
//            val result = dijkstra(graph, startLocation, goalLocation)
            val result = aStar(graph, startLocation, goalLocation)
            path = result?.first
        }) {
            Text("最短経路を計算")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (path != null) {
            val points = path!!.mapIndexed { index, node ->
                Pair(index * 100f + 50f, (node[0] - 'A') * 100f + 50f)
            }

            val smoothPath = bSplineInterpolation(points)

            Canvas(modifier = Modifier.fillMaxSize()) {
                val paint = Paint().apply {
                    color = Color.Blue
                    strokeWidth = 5f
                    style = PaintingStyle.Stroke
                }

                val pathObj = Path().apply {
                    if (smoothPath.isNotEmpty()) {
                        moveTo(smoothPath[0].first, smoothPath[0].second)
                        smoothPath.forEach { (x, y) ->
                            lineTo(x, y)
                        }
                    }
                }

                drawPath(
                    path = pathObj,
                    color = Color.Blue, // ここを Paint ではなく Color に変更
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 5f) // Stroke を適用
                )

            }
        }
    }
}

fun lineTo(x: Float, y: Float) {

}

fun heuristic(node: String, goal: String): Int {
    return abs(node[0] - goal[0]) * 5 // 例: 文字の距離 * 5
}


fun aStar(graph: Map<String, List<Edge>>, start: String, goal: String): Pair<List<String>, Int>? {
    val openSet = PriorityQueue(compareBy<Pair<String, Int>> { it.second }) // 最小 f(n) で管理
    val gScore = mutableMapOf<String, Int>().withDefault { Int.MAX_VALUE }
    val fScore = mutableMapOf<String, Int>().withDefault { Int.MAX_VALUE }
    val cameFrom = mutableMapOf<String, String?>()

    gScore[start] = 0
    fScore[start] = heuristic(start, goal) // 初期の推定コスト
    openSet.add(start to fScore[start]!!)

    while (openSet.isNotEmpty()) {
        val (current, _) = openSet.poll()

        if (current == goal) break // ゴールに到達

        graph[current]?.forEach { edge ->
            val tentativeGScore = gScore.getValue(current) + edge.weight

            if (tentativeGScore < gScore.getValue(edge.to)) {
                cameFrom[edge.to] = current
                gScore[edge.to] = tentativeGScore
                fScore[edge.to] = tentativeGScore + heuristic(edge.to, goal)

                if (openSet.none { it.first == edge.to }) {
                    openSet.add(edge.to to fScore[edge.to]!!)
                }
            }
        }
    }

    return constructPath(cameFrom, start, goal, gScore[goal] ?: Int.MAX_VALUE)
}


//fun dijkstra(graph: Map<String, List<Edge>>, start: String, goal: String): Pair<List<String>, Int>? {
//    val distances = mutableMapOf<String, Int>().withDefault { Int.MAX_VALUE }
//    val previousNodes = mutableMapOf<String, String?>()
//    val priorityQueue = PriorityQueue(compareBy<Pair<String, Int>> { it.second })
//
//    distances[start] = 0
//    priorityQueue.add(start to 0)
//
//    while (priorityQueue.isNotEmpty()) {
//        val (current, currentDistance) = priorityQueue.poll()
//
//        // ゴールに到達したら探索を終了（早期終了）
//        if (current == goal) break
//
//        // すでに最短経路が確定したノードをスキップ
//        if (currentDistance > distances.getValue(current)) continue
//
//        graph[current]?.forEach { edge ->
//            val newDistance = currentDistance + edge.weight
//            if (newDistance < distances.getValue(edge.to)) {
//                distances[edge.to] = newDistance
//                previousNodes[edge.to] = current
//                priorityQueue.add(edge.to to newDistance)
//            }
//        }
//    }
//
//    return constructPath(previousNodes, start, goal, distances[goal] ?: Int.MAX_VALUE)
//}


fun constructPath(prev: Map<String, String?>, start: String, goal: String, distance: Int): Pair<List<String>, Int>? {
    if (distance == Int.MAX_VALUE) return null

    val path = mutableListOf<String>()
    var current: String? = goal
    while (current != null) {
        path.add(current)
        current = prev[current]
    }
    path.reverse()

    return if (path.first() == start) path to distance else null
}


//a
//@Composable
//fun Page1Screen() {
//    val context = LocalContext.current
//    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
//    var locationText by remember { mutableStateOf("位置情報未取得") }
//
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        horizontalAlignment = Alignment.CenterHorizontally,
////        verticalArrangement = Arrangement.Center
//         ){
//        Text(
//            text = "経度",
//            color = Color.Gray,
//            style = MaterialTheme.typography.headlineMedium,
//            modifier = Modifier.padding(16.dp)
//        )
//        Spacer(modifier = Modifier.height(20.dp))
//        Text(
//            text = "カナジ",
//            color = Color.Gray,
//            style = MaterialTheme.typography.headlineMedium,
//            modifier = Modifier.padding(16.dp)
//        )
//    }
//
//    fun fetchLocation() {
//        if (ActivityCompat.checkSelfPermission(
//                context, Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED &&
//            ActivityCompat.checkSelfPermission(
//                context, Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            locationText = "位置情報の権限がありません"
//            return
//        }
//
//        fusedLocationClient.getCurrentLocation(
//            Priority.PRIORITY_HIGH_ACCURACY, null
//        ).addOnSuccessListener { location: Location? ->
//            location?.let {
//                locationText = "緯度: ${it.latitude}, 経度: ${it.longitude}"
//            } ?: run {
//                locationText = "位置情報を取得できません"
//            }
//        }
//
//    }
//
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Button(onClick = { fetchLocation() }) {
//            Text("現在地を取得")
//        }
//        Text(text = locationText, style = MaterialTheme.typography.headlineMedium)
//    }
//}


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
            Spot_selection_screen(R.drawable.spot1,"スポット名2","森でーす")
            Spot_selection_screen(R.drawable.spot1,"スポット名3","森でーす")
            Spot_selection_screen(R.drawable.spot1,"スポット名4","森でーす")
            Spot_selection_screen(R.drawable.spot1,"スポット名5","森でーす")
            Spot_selection_screen(R.drawable.spot1,"スポット名6","森でーす")
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
    Text("Page5 Screeeeen")
}

// ---------- 5. 現在のルートを取得するヘルパー ----------
@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}
