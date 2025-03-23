package com.example.myapplication.componentsPage1

import com.example.myapplication.Edge

import java.util.PriorityQueue
import kotlin.math.abs







//
//
//fun aStar(graph: Map<String, List<Edge>>, start: String, goal: String): Pair<List<String>, Int>? {
//    val openSet = PriorityQueue(compareBy<Pair<String, Int>> { it.second }) // 最小 f(n) で管理
//    val gScore = mutableMapOf<String, Int>().withDefault { Int.MAX_VALUE }
//    val fScore = mutableMapOf<String, Int>().withDefault { Int.MAX_VALUE }
//    val cameFrom = mutableMapOf<String, String?>()
//
//    gScore[start] = 0
//    fScore[start] = heuristic(start, goal) // 初期の推定コスト
//    openSet.add(start to fScore[start]!!)
//
//    while (openSet.isNotEmpty()) {
//        val (current, _) = openSet.poll()
//
//        if (current == goal) break // ゴールに到達
//
//        graph[current]?.forEach { edge ->
//            val tentativeGScore = gScore.getValue(current) + edge.weight
//
//            if (tentativeGScore < gScore.getValue(edge.to)) {
//                cameFrom[edge.to] = current
//                gScore[edge.to] = tentativeGScore
//                fScore[edge.to] = tentativeGScore + heuristic(edge.to, goal)
//
//                if (openSet.none { it.first == edge.to }) {
//                    openSet.add(edge.to to fScore[edge.to]!!)
//                }
//            }
//        }
//    }
//
//    return constructPath(cameFrom, start, goal, gScore[goal] ?: Int.MAX_VALUE)
//}
//
//
//fun heuristic(node: String, goal: String): Int {
//    return abs(node[0] - goal[0]) * 5 // 例: 文字の距離 * 5
//}
//
//
//fun constructPath(prev: Map<String, String?>, start: String, goal: String, distance: Int): Pair<List<String>, Int>? {
//    if (distance == Int.MAX_VALUE) return null
//
//    val path = mutableListOf<String>()
//    var current: String? = goal
//    while (current != null) {
//        path.add(current)
//        current = prev[current]
//    }
//    path.reverse()
//
//    return if (path.first() == start) path to distance else null
//}