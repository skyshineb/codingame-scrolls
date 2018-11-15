package ru.nskovorodin.codingame

import java.io.InputStream
import java.lang.StringBuilder

abstract class PuzzleBase {
    private val outputString = StringBuilder()
    val solution: String
        get() = outputString.trim().toString()

    abstract fun solve(input: InputStream): PuzzleBase

    fun print(str: String){
        outputString.append(str)
    }
    fun println(str: String){
        outputString.appendln(str)
    }

}