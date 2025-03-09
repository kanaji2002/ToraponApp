package com.example.myapplication.componentsPase2

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException

// 観光スポット一覧ページ
@Composable
fun Page2ScreenList(navController: NavController, mainColor: Color, subColor: Color) {
    val assetPath: String = "file:///android_asset/"
    // 一覧画面
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = mainColor)
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
        // assetフォルダ中のスポットを追加
        Column(modifier = Modifier.verticalScroll(rememberScrollState())){
            FolderListDisplay(navController, subColor, assetPath)
        }
    }
}
// 観光スポット一覧ページの選択画面追加
@Composable
fun FolderListDisplay(navController: NavController, subColor: Color, assetPath: String) {
    val context = LocalContext.current
    var folderNames by remember { mutableStateOf(listOf<String>()) }
    LaunchedEffect(Unit) {
        folderNames = getSpotFolders(context)
    }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        folderNames.forEach {
            Spot_selection_screen(navController, subColor, assetPath,it)
        }
    }

}
suspend fun getSpotFolders(context: Context): List<String> = withContext(Dispatchers.IO) {
    runCatching {
        context.assets.list("")?.filter { it.matches(Regex("spot\\d+_folder")) } ?: emptyList()
    }.getOrDefault(emptyList())
}
// 観光スポット一覧ページの選択画面
@Composable
fun Spot_selection_screen(navController: NavController, backcolor: Color, assetPath: String, folderName: String) {
    // JSON用関数
    suspend fun readJsonValueFromAssets(context: Context, folderName: String, fileName: String, key: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.assets.open("$folderName/$fileName")
                val jsonText = inputStream.bufferedReader().use { it.readText() }
                val jsonObject = JSONObject(jsonText)
                jsonObject.optString(key, "'$key'に関する情報は設定されていません") // キーがなければデフォルト値
            } catch (e: IOException) {
                "Error reading JSON: ${e.message}"
            }
        }
    }
    @Composable
    fun getJsonValue(folderName:String,jsonName:String,key: String): String {
        val context = LocalContext.current
        var jsonValue by remember { mutableStateOf("Loading...") }
        LaunchedEffect(key) {
            jsonValue = readJsonValueFromAssets(context, folderName, jsonName, key)
        }
        return jsonValue
    }
    @Composable
    fun JsonTextDisplay(folderName:String,jsonName:String,key:String, fontSize:Int, fontColor: Color) {
        val jsonValue = getJsonValue(folderName,jsonName,key)
        Column() {
            Text(text = jsonValue,
                style = TextStyle(
                    fontSize = fontSize.sp,
                    color = fontColor)
            )
        }
    }
    Row (
        modifier = Modifier
            .height(300.dp)
            .fillMaxSize()
            .clip(RoundedCornerShape(25.dp))
            .background(color = backcolor)
    ){
        // スポットの画像
        Image(
            painter = rememberAsyncImagePainter("${assetPath}/${folderName}/top_fig.png"),
            contentDescription = null,
            modifier = Modifier
                .size(300.dp, 300.dp),// 幅、高さ
            contentScale = ContentScale.Crop
        )
        // スポット名・ボタン・説明文
        Column(
            modifier = Modifier.padding(start = 30.dp, top = 18.dp, end = 30.dp, bottom = 30.dp)
        ){
            // スポット名
            JsonTextDisplay(folderName,"sentence.json","name_furigana", 14, Color.Black)
            JsonTextDisplay(folderName,"sentence.json","name", 35, Color.Black)
            Spacer(modifier = Modifier.height(10.dp))
            // 詳細ボタン・経路ボタン
            Row{
                Button(
                    onClick = {
                        navController.navigate("Detail/${folderName}")
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
            Spacer(modifier = Modifier.height(10.dp))
            FullWidthDivider()//横線
            // 説明文
            Column(modifier = Modifier.verticalScroll(rememberScrollState())){
                Spacer(modifier = Modifier.height(10.dp))
                JsonTextDisplay(folderName,"sentence.json","top_explanation", 20, Color.Black)
            }


        }
    }
    Spacer(modifier = Modifier.height(20.dp))
}
// 幅いっぱいに直線を引く
@Composable
fun FullWidthDivider() {
    Divider(
        color = Color.Gray,  // 線の色
        thickness = 2.dp,    // 線の太さ
        modifier = Modifier.fillMaxWidth()
    )
}