package com.example.myapplication.componentsPage1
//package utils

// MapUtils.kt
import androidx.navigation.NavController


import kotlin.math.pow
import kotlin.math.sqrt

data class MapVertex(val lat: Double, val lon: Double, val x: Float, val y: Float)

val mapRegions = listOf(
    listOf(
        MapVertex(34.32809341019379, 134.04192367609065, 0f, 0f),
        MapVertex(34.326880679260256, 134.04433338991794, 0f, 720f),
        MapVertex(34.332735039918816, 134.04281425912956, 1280f, 0f),
        MapVertex(34.33230692874956, 134.0465035767585, 1280f, 720f)
    )
)

fun interpolateXY(lat: Double, lon: Double, mapVertices: List<MapVertex>): Pair<Float, Float> {
    val minLat = mapVertices.minOf { it.lat }
    val maxLat = mapVertices.maxOf { it.lat }
    val minLon = mapVertices.minOf { it.lon }
    val maxLon = mapVertices.maxOf { it.lon }

    val minX = mapVertices.minOf { it.x }
    val maxX = mapVertices.maxOf { it.x }
    val minY = mapVertices.minOf { it.y }
    val maxY = mapVertices.maxOf { it.y }

    val x = minX + ((lat - minLat) / (maxLat - minLat)) * (maxX - minX)
    val y = minY + ((lon - minLon) / (maxLon - minLon)) * (maxY - minY)

    return Pair(x.toFloat(), y.toFloat())
}

fun findContainingRegion(lat: Double, lon: Double): List<MapVertex>? {
    for (region in mapRegions) {
        val minLat = region.minOf { it.lat }
        val maxLat = region.maxOf { it.lat }
        val minLon = region.minOf { it.lon }
        val maxLon = region.maxOf { it.lon }

        if (lat in minLat..maxLat && lon in minLon..maxLon) {
            return region
        }
    }
    return null
}

fun findNearestRegion(lat: Double, lon: Double): List<MapVertex> {
    return mapRegions.minByOrNull { region ->
        region.minOf { vertex ->
            sqrt((vertex.lat - lat).pow(2) + (vertex.lon - lon).pow(2))
        }
    } ?: mapRegions.first()
}
