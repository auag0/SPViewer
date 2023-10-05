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
    private val _prefFile: MutableLiveData<SuFile> = MutableLiveData(
        savedStateHandle[SPEditorActivity.EXTRA_PREF_FILE_PATH]
            ?: throw Exception("required ${SPEditorActivity.EXTRA_PREF_FILE_PATH}")
    )
    val prefFile: LiveData<SuFile> = _prefFile

    private var _spItems: MutableLiveData<List<SPItem>> = MutableLiveData(emptyList())
    val spItems: LiveData<List<SPItem>> = _spItems
    private var isModified: Boolean = false

    init {
        parsePrefFile()
    }

    private fun parsePrefFile() {
        _spItems.value = emptyList()
        viewModelScope.launch(Dispatchers.IO) {
            val prefText = _prefFile.value!!.newInputStream().bufferedReader().use { it.readText() }
            val parsedSPItems = withContext(Dispatchers.Default) {
                SPParser.parseXmlText(prefText)
            }
            _spItems.postValue(parsedSPItems)
            isModified = false
        }
    }

    fun savePrefFile(complete: () -> Unit) {
        viewModelScope.launch(Dispatchers.Default) {
            val newSPItems = _spItems.value ?: emptyList()
            val xmlText = SPParser.createXmlText(newSPItems)
            withContext(Dispatchers.IO) {
                _prefFile.value!!.newOutputStream().bufferedWriter().use {
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
        return _spItems.value?.any { it.key == key } ?: false
    }

    fun editSPItem(oldKey: String, newSPItem: SPItem): String? {
        if (containsKey(newSPItem.key) && oldKey != newSPItem.key) {
            return "key already exists"
        }
        _spItems.value = _spItems.value?.map {
            if (it.key == oldKey) {
                newSPItem
            } else {
                it
            }
        }
        isModified = true
        return null
    }

    fun createSPItem(newSPItem: SPItem): String? {
        if (containsKey(newSPItem.key)) {
            return "key already exists"
        }
        _spItems.value = _spItems.value?.plus(newSPItem)
        isModified = true
        return null
    }

    fun deleteSPItem(spItem: SPItem) {
        _spItems.value = _spItems.value?.filterNot { it.key == spItem.key }
        isModified = true
    }
}