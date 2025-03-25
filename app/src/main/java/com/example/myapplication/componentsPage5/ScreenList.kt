package com.example.myapplication.componentsPage5

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScrollableCardRow() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
//            .background(Color(0xFFFCECD9))
            .padding(20.dp)
    ) {
        Text(
            text = "その他",
            fontSize = 60.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))
        Row(
            modifier = Modifier
                .horizontalScroll(scrollState)
                .padding(start = 25.dp)

        ) {
            CardItem("全体の歴史", Icons.Default.Home)
            CardItem("設定", Icons.Default.Settings)
            CardItem("公園のマナー", Icons.Default.Warning)
            CardItem("利用規約", Icons.Default.Info)
            CardItem("お問い合わせ", Icons.Default.Info)
        }
    }
}

@Composable
fun CardItem(title: String, icon: ImageVector) {
    Card(
        modifier = Modifier
            .width(350.dp)
            .height(500.dp)
            .padding(end = 50.dp),
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFDDDDDD))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(200.dp)
            )
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = title,
                fontSize = 50.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}
