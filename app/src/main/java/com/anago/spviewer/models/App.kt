package com.anago.spviewer.models

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable

@Suppress("DEPRECATION")
data class App(
    var packageName: String,
    var name: String,
    var icon: Bitmap
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readParcelable(Bitmap::class.java.classLoader)!!
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(packageName)
        dest.writeString(name)
        dest.writeParcelable(icon, 0)
    }

    companion object CREATOR : Parcelable.Creator<App> {
        override fun createFromParcel(parcel: Parcel): App {
            return App(parcel)
        }

        override fun newArray(size: Int): Array<App?> {
            return arrayOfNulls(size)
        }
    }
}