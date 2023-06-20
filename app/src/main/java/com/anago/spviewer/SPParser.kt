package com.anago.spviewer

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.File
import java.io.StringReader

class SPParser(private val SPFilePath: String, private val useRootMode: Boolean = false) {
    private fun readTextFromFile(): String {
        return if (useRootMode) {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "cat", SPFilePath))
            process.inputStream.bufferedReader().use { it.readText() }
        } else {
            File(SPFilePath).bufferedReader().use { it.readText() }
        }
    }
    fun getAll(): Map<String, Any?> {
        val text = readTextFromFile()
        val xmlPullParser: XmlPullParser = Xml.newPullParser()
        xmlPullParser.setInput(StringReader(text))

        val map = mutableMapOf<String, Any?>()
        var key = ""
        var stringSet: MutableSet<String>? = null

        while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
            when (xmlPullParser.eventType) {
                XmlPullParser.START_TAG -> {
                    val tagName = xmlPullParser.name ?: continue

                    when (tagName) {
                        "set" -> {
                            stringSet = mutableSetOf()
                        }
                        "string" -> {
                            stringSet?.add(xmlPullParser.nextText())
                        }
                    }

                    key = xmlPullParser.getAttributeValue(null, "name") ?: continue
                    val value = xmlPullParser.getAttributeValue(null, "value")

                    map[key] = when (tagName) {
                        "string" -> xmlPullParser.nextText()
                        "long" -> value.toLongOrNull()
                        "int" -> value.toIntOrNull()
                        "boolean" -> value.toBooleanStrictOrNull()
                        "float" -> value.toFloatOrNull()
                        else -> null
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (xmlPullParser.name == "set") {
                        map[key] = stringSet!!.toSet()
                    }
                    stringSet = null
                }
            }
        }
        return map
    }

    fun getAllByList(): List<Pair<String, Any?>> {
        return getAll().toList()
    }

    inline fun <reified T> get(key: String): T? {
        val value = getAll()[key]
        return value as? T?
    }

    fun getString(key: String): String? {
        return get(key)
    }

    fun getLong(key: String): Long? {
        return get(key)
    }

    fun getInt(key: String): Int? {
        return get(key)
    }

    fun getBoolean(key: String): Boolean? {
        return get(key)
    }

    fun getFloat(key: String): Float? {
        return get(key)
    }

    fun contains(key: String): Boolean {
        return getAll().containsKey(key)
    }
}