package com.example.myapplication.componentsPage2

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import kotlin.math.hypot
import kotlin.math.roundToInt


val spotPositions = mapOf(
    "spot1_folder" to Pair(767.dp, 544.dp),
    "spot2_folder" to Pair(785.dp, 446.dp),
    "spot3_folder" to Pair(697.dp, 369.dp),
    "spot4_folder" to Pair(654.dp, 478.dp),
    // ... 必要なスポットを追加
)

@Composable
fun Page2ScreenDetail(navController: NavController, backcolor: Color, folderName: MutableState<String>) {

    val context = LocalContext.current

    @Composable
    fun getJsonValue(folderName: String, jsonName: String, key: String): String {
        var jsonValue by remember { mutableStateOf("Loading...") }
        LaunchedEffect(key, folderName) {
            jsonValue = readJsonValueFromAssets(context, folderName, jsonName, key)
        }
        return jsonValue
    }

    @Composable
    fun JsonTextDisplay(folderName: String, jsonName: String, key: String, fontSize: Int, fontColor: Color) {
        val jsonValue = getJsonValue(folderName, jsonName, key)
        Text(text = jsonValue, style = TextStyle(fontSize = fontSize.sp, color = fontColor))
    }

    @Composable
    fun JsonTextDisplayBoldLetters(folderName: String, jsonName: String, key: String, fontSize: Int, fontColor: Color) {
        val jsonValue = getJsonValue(folderName, jsonName, key)
        Text(text = jsonValue, fontWeight = FontWeight.Bold, style = TextStyle(fontSize = fontSize.sp, color = fontColor))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, end = 24.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Button(
            onClick = { navController.navigate("list") },
            modifier = Modifier.height(50.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow, contentColor = Color.Black)
        ) {
            Text(text = "　戻る　", fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Button(
            onClick = { /* 経路処理 */ },
            modifier = Modifier.height(50.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red, contentColor = Color.Black)
        ) {
            Text(text = "　経路　", fontSize = 20.sp)
        }
    }

    Column(
        modifier = Modifier
            .background(backcolor)
            .fillMaxSize()
            .padding(top = 30.dp, start = 30.dp, end = 30.dp)
    ) {
        JsonTextDisplayBoldLetters(folderName.value, "sentence.json", "name", 50, Color.Black)
        Spacer(modifier = Modifier.height(15.dp))

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(top = 16.dp, start = 24.dp, end = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFFFA500).copy(alpha = 0.5f))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val jsonValueLeft = getJsonValue(folderName.value, "sentence.json", "one_word_explanation")
                Text(
                    text = jsonValueLeft,
                    fontSize = 40.sp,
                    modifier = Modifier.weight(3f)
                )

                val currentX = remember { mutableStateOf(1280f) }
                val currentY = remember { mutableStateOf(720f) }
                val dpDistance = calculateDpDistanceFromSpot(folderName.value, currentX.value, currentY.value, spotPositions)


                    Text(

                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontSize = 28.sp, color = Color.Gray)) {
                                append("ここから徒歩 ")
                            }
                            withStyle(style = SpanStyle(fontSize = 35.sp, color = Color.Black)) {
                                append("$dpDistance")
                            }
                            withStyle(style = SpanStyle(fontSize = 28.sp, color = Color.Gray)) {
                                append("分")
                            }

                        },
                        modifier = Modifier
                            .weight(2f)
                            .wrapContentWidth(Alignment.End)
                    )


            }

            Spacer(modifier = Modifier.height(15.dp))
            DisplayImagesFromFolder(context, folderName.value)
            Spacer(modifier = Modifier.height(25.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color.White)
                    .padding(30.dp)
            ) {
                JsonDetailedTextDisplay(folderName.value, "sentence.json")
                Spacer(modifier = Modifier.height(30.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFFFA500).copy(alpha = 0.5f))
                        .padding(25.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    JsonTextDisplay(folderName.value, "sentence.json", "additional_title", 35, Color.Black)
                    JsonTextDisplay(folderName.value, "sentence.json", "additional_story", 20, Color.DarkGray)
                }
            }
        }
    }
}

@Composable
fun DisplayImagesFromFolder(context: Context, folderName: String) {
    val imageList = remember { mutableStateListOf<String>() }
    LaunchedEffect(folderName) {
        try {
            val files = context.assets.list(folderName)
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
            .padding(24.dp),
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

@Composable
fun calculateDpDistanceFromSpot(
    spotLabel: String,
    currentX: Float,
    currentY: Float,
    spotPositions: Map<String, Pair<Dp, Dp>>
): Int {
    val density = LocalDensity.current
    val position = spotPositions[spotLabel] ?: return -1
    val targetXPx = with(density) { position.first.toPx() }
    val targetYPx = with(density) { position.second.toPx() }
    val distancePx = hypot(currentX - targetXPx, currentY - targetYPx)
    return (distancePx / 250f).roundToInt() + 1
}

@Composable
fun JsonDetailedTextDisplay(folderName: String, fileName: String) {
    val context = LocalContext.current
    var detailedValues by remember { mutableStateOf<List<String>>(emptyList()) }
    LaunchedEffect(folderName) {
        detailedValues = readDetailedValuesFromAssets(context, folderName, fileName, "detailed_title_")
    }
    Column(modifier = Modifier.padding(16.dp)) {
        detailedValues.forEachIndexed { index, value ->
            val assetPath = "file:///android_asset/"
            Row {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = value, fontSize = 35.sp, color = Color.Black)
                    val jsonValueLeft = getJsonValue(folderName, "sentence.json", "detailed_${index + 1}")
                    Text(text = jsonValueLeft, fontSize = 20.sp, color = Color.DarkGray)
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

@Composable
fun getJsonValue(folderName: String, jsonName: String, key: String): String {
    val context = LocalContext.current
    var jsonValue by remember { mutableStateOf("Loading...") }
    LaunchedEffect(key, folderName) {
        jsonValue = readJsonValueFromAssets(context, folderName, jsonName, key)
    }
    return jsonValue
}

suspend fun readJsonValueFromAssets(context: Context, folderName: String, fileName: String, key: String): String {
    return withContext(Dispatchers.IO) {
        try {
            val inputStream = context.assets.open("$folderName/$fileName")
            val jsonText = inputStream.bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(jsonText)
            jsonObject.optString(key, "'$key'に関する情報は設定されていません")
        } catch (e: IOException) {
            "Error reading JSON: ${e.message}"
        }
    }
}

suspend fun readDetailedValuesFromAssets(context: Context, folderName: String, fileName: String, searchWord: String): List<String> {
    return withContext(Dispatchers.IO) {
        try {
            val inputStream = context.assets.open("$folderName/$fileName")
            val jsonText = inputStream.bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(jsonText)
            jsonObject.keys().asSequence()
                .filter { it.startsWith(searchWord) }
                .map { jsonObject.getString(it) }
                .toList()
        } catch (e: Exception) {
            listOf("Error reading JSON: ${e.message}")
        }
    }
}
