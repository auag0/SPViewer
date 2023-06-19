package com.anago.spviewer.root

object Commands {
    fun hasRootAccess(): Boolean {
        val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "id"))
        val result = process.inputStream.bufferedReader().use { it.readText() }
        return result.contains("uid=0", true)
    }

    fun fileLists(directory: String): List<String> {
        val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "ls", directory))
        return process.inputStream.bufferedReader().readLines()
    }
}