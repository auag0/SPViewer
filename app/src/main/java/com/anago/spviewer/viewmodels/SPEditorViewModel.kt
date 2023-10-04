package com.anago.spviewer.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anago.spviewer.activities.SPEditorActivity
import com.anago.spviewer.models.SPItem
import com.anago.spviewer.utils.SPParser
import com.topjohnwu.superuser.io.SuFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SPEditorViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val prefFile: MutableLiveData<SuFile?> = MutableLiveData(null)
    private var spItems: MutableLiveData<List<SPItem>> = MutableLiveData(emptyList())
    private var isModified: Boolean = false

    init {
        prefFile.value = savedStateHandle[SPEditorActivity.EXTRA_PREF_FILE_PATH]
            ?: throw Exception("required ${SPEditorActivity.EXTRA_PREF_FILE_PATH}")
        parsePrefFile()
    }

    fun getPrefFile(): LiveData<SuFile?> {
        return prefFile
    }

    fun getSPItems(): LiveData<List<SPItem>> {
        return spItems
    }

    private fun parsePrefFile() {
        spItems.value = emptyList()
        viewModelScope.launch(Dispatchers.IO) {
            val prefText = prefFile.value!!.newInputStream().bufferedReader().use { it.readText() }
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
                prefFile.value!!.newOutputStream().bufferedWriter().use {
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