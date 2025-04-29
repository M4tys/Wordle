package com.example.wordle.data

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.random.Random

class WordsRepository(private val context: Context) {

    fun loadWords(fileName: String): List<String> {
        val words = mutableListOf<String>()
        try {
            val inputStream = context.assets.open(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String? = reader.readLine()
            while (line != null) {
                words.add(line)
                line = reader.readLine()
            }
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return words.filter { it.length == 5 && it.all { char -> char.isUpperCase() } }
    }

    fun getRandomWord(words: List<String>): String? {
        return if (words.isNotEmpty()) {
            words[Random.nextInt(words.size)]
        } else {
            null
        }
    }
}