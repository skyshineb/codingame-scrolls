import java.util.*
import java.io.*
import java.math.*
import kotlin.collections.ArrayList

val mapSize = 4

class Graph {
    val adjacencyMap: HashMap<Tile, HashSet<Tile>> = HashMap()

    fun addEdge(sourceVertex: Tile, destinationVertex: Tile) {
        adjacencyMap
                .computeIfAbsent(sourceVertex){HashSet()}
                .add(destinationVertex)
        adjacencyMap
                .computeIfAbsent(destinationVertex){HashSet()}
                .add(sourceVertex)
    }

    override fun toString(): String = StringBuffer().apply {
        for (key in adjacencyMap.keys) {
            append("$key -> ")
            append(adjacencyMap[key]?.joinToString(", ", "[", "]\n"))
        }
    }.toString()
}

fun fillGraph(mapArray: ArrayList<ArrayList<Tile>>): Graph {
    val graph = Graph()
    for (x in 0 until mapSize){
        for (y in 0 until mapSize){
            val tile = mapArray[x][y]
            if (tile.up && tile.y != 0){
                val upperTile = mapArray[tile.x][tile.y - 1]
                if (upperTile.down) graph.addEdge(tile, upperTile)
            }
            if (tile.right && tile.x != (mapSize - 1)){
                val rigthTile = mapArray[tile.x + 1][tile.y]
                if (rigthTile.left) graph.addEdge(tile, rigthTile)
            }
            if (tile.down && tile.y != (mapSize - 1)){
                val lowerTile = mapArray[tile.x][tile.y + 1]
                if (lowerTile.up) graph.addEdge(tile, lowerTile)
            }
            if (tile.left && tile.x != 0){
                val leftTile = mapArray[tile.x - 1][tile.y]
                if (leftTile.up) graph.addEdge(tile, leftTile)
            }
        }
    }
    return graph
}

data class Tile(
        val up: Boolean,
        val right: Boolean,
        val down: Boolean,
        val left: Boolean,
        val x: Int,
        val y: Int) {
    companion object {
        fun parseTitle(s: String, x: Int, y:Int): Tile {
            return Tile(s[0] == '1', s[1] == '1', s[2] == '1', s[3] == '1', x, y)
        }
    }

    override fun toString(): String {
        return "[$x,$y]"
    }
}

data class Item(
        val itemName: String,
        val itemX: Int,
        val itemY: Int,
        val itemPlayerId: Int) {
    companion object {
        fun parseItem(itemName: String, x: Int, y:Int, itemPlayerId: Int): Item {
            return Item(itemName, x, y, itemPlayerId)
        }
    }
}

class ElvesDoStuff{

    /**
     * Help the Christmas elves fetch presents in a magical labyrinth!
     **/
    fun main(programInput : String) {
        val input = Scanner(programInput)

        val tiles = ArrayList<ArrayList<Tile>>()
        val items = ArrayList<Item>()
        // game loop
        while (true) {
            // 0 - PUSH 1 - MOVE
            val tileArray = ArrayList<ArrayList<String>>(7)
            val turnType = input.nextInt()
            for (i in 0 until mapSize) {
                val tmpTile = ArrayList<Tile>()
                for (j in 0 until mapSize) {
                    val tile = input.next()
                    tmpTile.add(Tile.parseTitle(tile, i, j))
                }
                tiles.add(tmpTile)
            }
            for (i in 0 until 2) {
                val numPlayerCards = input.nextInt() // the total number of quests for a player (hidden and revealed)
                val playerX = input.nextInt()
                val playerY = input.nextInt()
                val playerTile = input.next()
            }
            val numItems = input.nextInt() // the total number of items available on board and on player tiles
            for (i in 0 until numItems) {
                val itemName = input.next()
                val itemX = input.nextInt()
                val itemY = input.nextInt()
                val itemPlayerId = input.nextInt()
                items.add(Item(itemName, itemX, itemX, itemPlayerId))
            }
            val numQuests = input.nextInt() // the total number of revealed quests for both players
            for (i in 0 until numQuests) {
                val questItemName = input.next()
                val questPlayerId = input.nextInt()
            }

            // Write an action using println()
            // To debug: System.err.println("Debug messages...");

            println("PUSH 3 RIGHT") // PUSH <id> <direction> | MOVE <direction> | PASS
            println(fillGraph(tiles).toString())
        }
    }

    fun log(str:Any) = System.err.println(str.toString())

}
