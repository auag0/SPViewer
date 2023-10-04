package com.anago.spviewer.utils

import com.topjohnwu.superuser.io.SuFile

object FileUtils {
    @Suppress("SdCardPath")
    fun getAppDataDir(packageName: String): SuFile {
        val dataDirPaths = arrayOf(
            SuFile("/data_mirror/data_ce/null/0/${packageName}"),
            SuFile("/data/data/${packageName}"),
            SuFile("/data/user/0/${packageName}")
        )
        return dataDirPaths.maxBy { it.listFiles()?.size ?: -1 }
    }

    fun getFilesInDataFile(
        packageName: String,
        child: String,
        suffix: String
    ): Array<SuFile> {
        val appDataDir = getAppDataDir(packageName)
        val directory = SuFile(appDataDir, child)
        return directory.listFiles { file ->
            file.name.endsWith(suffix, true)
        } ?: emptyArray()
    }
}