package ru.nskovorodin.codingame.puzzles.easy.chuck_norris

import ru.nskovorodin.codingame.PuzzleBase
import java.io.InputStream
import java.util.*

class ChuckNorrisPuzzle: PuzzleBase() {
    override fun solve(input: InputStream): PuzzleBase {
        val input = Scanner(input)

        val msg = input.nextLine()

        val binaryMsg = msg.map { s ->
            charToBinString(s).also { System.err.println("$s is $it") }
        }.joinToString("").trim()


        System.err.println("$msg is $binaryMsg")

        val output = StringBuilder()
        var f = -1
        for (c in binaryMsg){

            if (c == '1' && f != 1){
                output.append(" 0 0")
                f = 1
            }
            else if (c == '0' && f != 0){
                output.append(" 00 0")
                f = 0
            }
            else output.append('0')
        }
        val res = output.toString().trim()


        println(res)

        return this
    }

    fun charToBinString(char: Char): String = String.format("%7s", Integer.toBinaryString(char.toInt() and 0xFF)).replace("^[ ]+".toRegex(), "0")

}