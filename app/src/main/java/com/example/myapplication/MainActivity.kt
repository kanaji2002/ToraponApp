package com.example.myapplication

import android.Manifest
import android.os.Bundle
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

//地図
import java.util.*
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
                label = {}, // ラベルを非表示
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
var goal : String = "D"


@Composable
fun Page1Screen() {
    val graph = mapOf(
        "A" to listOf(Edge("B", 4), Edge("C", 2)),
        "B" to listOf(Edge("A", 4), Edge("C", 5), Edge("D", 10)),
        "C" to listOf(Edge("A", 2), Edge("B", 5), Edge("D", 3)),
        "D" to listOf(Edge("B", 10), Edge("C", 3), Edge("E", 8)),
        "E" to listOf(Edge("D", 8), Edge("A", 7))
    )

    var startLocation by remember { mutableStateOf(start) }
    var goalLocation by remember { mutableStateOf(goal) }
    var resultText by remember { mutableStateOf("結果がここに表示されます") }

    val locations = graph.keys.toList()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("現在地 $startLocation",
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("目的地 $goalLocation",

            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (startLocation == goalLocation) {
                    resultText = "現在地と目的地が同じです"
                } else {
                    val result = dijkstra(graph, startLocation, goalLocation)
                    resultText = result?.let {
                        "最短経路: ${it.first.joinToString(" → ")}\n合計距離: ${it.second}"
                    } ?: "経路が見つかりません。"
                }
            },
//            modifier = Modifier.fillMaxWidth()
        ) {
            Text("最短経路を計算")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = resultText,
            fontSize = 24.sp
        )
    }
}



fun dijkstra(graph: Map<String, List<Edge>>, start: String, goal: String): Pair<List<String>, Int>? {
    val distances = mutableMapOf<String, Int>().withDefault { Int.MAX_VALUE }
    val previousNodes = mutableMapOf<String, String?>()
    val priorityQueue = PriorityQueue<Pair<String, Int>>(compareBy { it.second })

    distances[start] = 0
    priorityQueue.add(start to 0)

    while (priorityQueue.isNotEmpty()) {
        val (current, currentDistance) = priorityQueue.poll()

        if (current == goal) break

        graph[current]?.forEach { edge ->
            val newDistance = currentDistance + edge.weight
            if (newDistance < distances.getValue(edge.to)) {
                distances[edge.to] = newDistance
                previousNodes[edge.to] = current
                priorityQueue.add(edge.to to newDistance)
            }
        }
    }

    return constructPath(previousNodes, start, goal, distances[goal] ?: Int.MAX_VALUE)
}

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
