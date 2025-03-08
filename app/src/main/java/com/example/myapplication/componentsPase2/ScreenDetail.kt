package com.example.myapplication.componentsPase2

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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

// 詳細ページ
@Composable
fun Page2ScreenDetail(navController: NavController, backcolor: Color, folderName: String){
    // JSON用関数
    suspend fun readJsonValueFromAssets(context: Context, folderName: String, fileName: String, key: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.assets.open("$folderName/$fileName")
                val jsonText = inputStream.bufferedReader().use { it.readText() }
                val jsonObject = JSONObject(jsonText)
                jsonObject.optString(key, "'$key'に関する情報は設定されていません") //キーがなければデフォルト値
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
        Column {
            Text(text = jsonValue,
                style = TextStyle(
                    fontSize = fontSize.sp,
                    color = fontColor)
            )
        }
    }
    @Composable
    fun JsonTextDisplayBoldLetters(folderName:String,jsonName:String,key:String, fontSize:Int, fontColor: Color) {
        val jsonValue = getJsonValue(folderName,jsonName,key)
        Column {
            Text(text = jsonValue,
                fontWeight = FontWeight.Bold,
                style = TextStyle(
                    fontSize = fontSize.sp,
                    color = fontColor)
            )
        }
    }
    // 戻るボタン・経路ボタン
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 24.dp, end = 24.dp),
        horizontalArrangement = Arrangement.End,
    ){
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
        Button(
            onClick = { /* ボタンの処理 */ }, // 経路のページへ
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
    // 詳細ページの外枠
    Column(
        modifier = Modifier
            .background(color = backcolor)
            .fillMaxSize()
            .padding(top = 30.dp, start = 30.dp, end = 30.dp)
    ) {
        // スポット名称
        JsonTextDisplayBoldLetters(folderName,"sentence.json","name", 50, Color.Black)
        Spacer(modifier = Modifier.height(15.dp))
        // 詳細説明(スクロール)
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(top = 16.dp, start = 24.dp, end = 24.dp),
        ) {
            Row(// 橙色背景(上)
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFFFA500).copy(alpha = 0.5f))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 詳細画面の一言スポット紹介(左端)
                val jsonValueLeft = getJsonValue(folderName,"sentence.json","one_word_explanation")
                Text(
                    text = jsonValueLeft,
                    fontSize = 40.sp,
                    modifier = Modifier.weight(3f) // 左側に寄せる
                )
                // 徒歩による移動時間表示(右端)
                Text(
                    text = "ここから徒歩●分",
                    fontSize = 35.sp,
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
            //画像一覧(横スクロール)
            val context = LocalContext.current
            DisplayImagesFromFolder(context, folderName)
            Spacer(modifier = Modifier.height(25.dp))
            // 説明文(白背景)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(50.dp))
                    .background(color = Color.White)
                    .padding(top = 30.dp, bottom = 30.dp,start = 50.dp, end = 50.dp)
            ){
                // 説明文＋画像
                JsonDetailedTextDisplay(folderName, "sentence.json")
                Spacer(modifier = Modifier.height(30.dp))
                // 橙色背景(下)の説明文
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFFFA500).copy(alpha = 0.5f))
                        .padding(all = 25.dp),
                    verticalArrangement = Arrangement.Center // 縦方向の中央揃え
                ) {
                    JsonTextDisplay(folderName,"sentence.json","additional_title", 35, Color.Black)
                    JsonTextDisplay(folderName,"sentence.json","additional_story", 20, Color.DarkGray)
                }
            }

        }
    }
}
// 詳細ページの画像表示(横スクロール)
@Composable
fun DisplayImagesFromFolder(context: Context, folderName: String) {
    val imageList = remember { mutableStateListOf<String>() }
    LaunchedEffect(Unit) {
        try {
            val files = context.assets.list(folderName) // フォルダ内の全ファイルを取得
            files?.filter { it.startsWith("sub_fig_") }?.forEach { imageList.add(it) }
        } catch (e: IOException) {
            Log.e("Assets", "Error reading folder: ${e.message}")
        }
    }
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(Color.LightGray.copy(alpha = 0.5f))
            .padding(all = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(imageList) { imageFile ->
            Image(
                painter = rememberAsyncImagePainter("file:///android_asset/$folderName/$imageFile"),
                contentDescription = null,
                modifier = Modifier
                    .size(300.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}
// 詳細ページの説明文＋画像(白背景)表示
@Composable
fun JsonDetailedTextDisplay(folderName: String, fileName: String) {
    val context = LocalContext.current
    var detailedValues by remember { mutableStateOf<List<String>>(emptyList()) }
    LaunchedEffect(Unit) {
        detailedValues = readDetailedValuesFromAssets(context, folderName, fileName, "detailed_title_")
    }
    Column(modifier = Modifier.padding(16.dp)) {
        detailedValues.forEachIndexed { index, value ->
            val assetPath: String = "file:///android_asset/"
            Row{
                Column(
                    modifier = Modifier
                        .weight(1f) // Row の左半分を占める
                        .background(Color.White), // 背景色を付ける（確認用）
                    verticalArrangement = Arrangement.Center // 縦方向の中央揃え
                ) {
                    Text(text = value, fontSize = 35.sp, color = Color.Black)
                    val jsonValueLeft = getJsonValue(folderName,"sentence.json","detailed_${index + 1}")
                    Text(
                        text = jsonValueLeft,
                        fontSize = 20.sp,
                        color = Color.DarkGray
                    )
                }
                Spacer(modifier = Modifier.height(15.dp))
                Image(
                    painter = rememberAsyncImagePainter("${assetPath}/${folderName}/detailed_Fig_${index + 1}.png"),
                    contentDescription = null,
                    modifier = Modifier
                        .size(300.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop

                )
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
// JSON用関数
@Composable
fun getJsonValue(folderName:String,jsonName:String,key: String): String {
    val context = LocalContext.current
    var jsonValue by remember { mutableStateOf("Loading...") }

    LaunchedEffect(key) {
        jsonValue = readJsonValueFromAssets(context, folderName, jsonName, key)
    }

    return jsonValue
}
suspend fun readJsonValueFromAssets(context: Context, folderName: String, fileName: String, key: String): String {
    return withContext(Dispatchers.IO) { //非同期処理
        try {
            val inputStream = context.assets.open("$folderName/$fileName")
            val jsonText = inputStream.bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(jsonText)

            jsonObject.optString(key, "'$key'に関する情報は設定されていません") //キーがなければデフォルト値
        } catch (e: IOException) {
            "Error reading JSON: ${e.message}"
        }
    }
}
// "detailed_1", "detailed_2", ... に該当するキーを検索
suspend fun readDetailedValuesFromAssets(context: Context, folderName: String, fileName: String, searchWord:String): List<String> {
    return withContext(Dispatchers.IO) {
        try {
            val inputStream = context.assets.open("$folderName/$fileName")
            val jsonText = inputStream.bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(jsonText)
            val detailedValues = mutableListOf<String>()
            for (key in jsonObject.keys()) {
                if (key.startsWith(searchWord)) {
                    detailedValues.add(jsonObject.getString(key))
                }
            }
            detailedValues
        } catch (e: Exception) {
            listOf("Error reading JSON: ${e.message}")
        }
    }
}