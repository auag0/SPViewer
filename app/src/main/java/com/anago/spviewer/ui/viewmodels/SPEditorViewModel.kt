package com.anago.spviewer.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anago.spviewer.SPParser
import com.anago.spviewer.models.SPItem
import com.topjohnwu.superuser.io.SuFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class SPEditorViewModel : ViewModel() {
    private var mIsModified: Boolean = false
    private val mItems: MutableLiveData<List<SPItem>> = MutableLiveData(emptyList())
    fun getItems(): LiveData<List<SPItem>> {
        return mItems
    }

    fun loadSharedPrefsFile(xmlFile: File) {
        viewModelScope.launch(Dispatchers.IO) {
            val xmlText = readTextFromFile(xmlFile.absolutePath)
            mItems.postValue(SPParser.parseXmlText(xmlText).toMutableList())
        }
    }

    fun isModified(): Boolean {
        return mIsModified
    }

    fun deleteItem(key: String) {
        val currentItems = mItems.value!!.toMutableList()
        val result = currentItems.removeIf { it.key == key }
        if (result) {
            mItems.value = currentItems
            mIsModified = true
        }
    }

    fun changeItem(oldItemKey: String, newItem: SPItem) {
        val currentItems = mItems.value!!.toMutableList()
        val index = currentItems.indexOfFirst { it.key == oldItemKey }
        if (index != -1) {
            currentItems[index] = newItem
            mItems.value = currentItems
            mIsModified = true
        }
    }

    fun addItem(newItem: SPItem) {
        val currentItems = mItems.value!!.toMutableList()
        currentItems.add(newItem)
        mItems.value = currentItems
        mIsModified = true
    }

    fun saveItemsToFile(saveTo: File, onCompleted: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val xmlText = SPParser.createXmlText(mItems.value!!)
            SuFile(saveTo.absolutePath).newOutputStream().bufferedWriter().use { out ->
                out.write(xmlText)
            }
            onCompleted()
        }
    }

    private fun readTextFromFile(xmlFilePath: String): String {
        return SuFile(xmlFilePath).newInputStream().bufferedReader().use { it.readText() }
    }
}