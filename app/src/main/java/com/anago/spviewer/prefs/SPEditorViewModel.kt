package com.anago.spviewer.prefs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.topjohnwu.superuser.io.SuFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SPEditorViewModel : ViewModel() {
    private lateinit var prefFile: SuFile
    private var spItems: MutableLiveData<List<SPItem>> = MutableLiveData(emptyList())
    private var isModified: Boolean = false

    fun getSPItems(): LiveData<List<SPItem>> {
        return spItems
    }

    fun setPrefFile(file: SuFile) {
        prefFile = file
        parsePrefFile()
    }

    private fun parsePrefFile() {
        spItems.value = emptyList()
        viewModelScope.launch(Dispatchers.IO) {
            val prefText = prefFile.newInputStream().bufferedReader().use { it.readText() }
            spItems.postValue(withContext(Dispatchers.Default) {
                SPParser.parseXmlText(prefText)
            })
            isModified = false
        }
    }

    fun savePrefFile(complete: () -> Unit) {
        viewModelScope.launch(Dispatchers.Default) {
            val newSPItems = spItems.value ?: emptyList()
            val xmlText = SPParser.createXmlText(newSPItems)
            withContext(Dispatchers.IO) {
                prefFile.newOutputStream().bufferedWriter().use {
                    it.write(xmlText)
                }
                complete()
            }
        }
    }

    fun isModified(): Boolean {
        return isModified
    }

    private fun containsKey(key: String): Boolean {
        val currentSPItems = spItems.value!!.toMutableList()
        return currentSPItems.indexOfFirst { it.key == key } != -1
    }

    fun editSPItem(oldKey: String, newSPItem: SPItem): String? {
        if (containsKey(newSPItem.key) && oldKey != newSPItem.key) {
            return "key already exists"
        }
        val currentSPItems = spItems.value!!.toMutableList()
        val index = currentSPItems.indexOfFirst { it.key == oldKey }
        if (index != -1) {
            currentSPItems[index] = newSPItem
            spItems.value = currentSPItems
            isModified = true
            return null
        }
        return "not found old item"
    }

    fun createSPItem(spItem: SPItem): String? {
        if (containsKey(spItem.key)) {
            return "key already exists"
        }
        val currentSPItems = spItems.value!!.toMutableList()
        currentSPItems.add(spItem)
        spItems.value = currentSPItems
        isModified = true
        return null
    }

    fun deleteSPItem(spItem: SPItem) {
        val currentSPItems = spItems.value!!.toMutableList()
        if (currentSPItems.remove(spItem)) {
            spItems.value = currentSPItems
            isModified = true
        }
    }
}